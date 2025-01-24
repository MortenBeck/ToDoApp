package dk.dtu.ToDoList.model.repository

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dk.dtu.ToDoList.model.data.task.RecurrencePattern
import dk.dtu.ToDoList.model.data.task.Task
import dk.dtu.ToDoList.model.data.task.TaskPriority
import dk.dtu.ToDoList.model.data.task.TaskTag
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.Calendar
import java.util.UUID


/**
 * A repository class for managing [Task] objects stored in Firebase Firestore.
 *
 * This class provides methods for:
 * - Basic CRUD operations (Create, Read, Update, Delete).
 * - Handling tasks with recurrence patterns.
 * - Real-time task observation using [Flow].
 * - Filtering tasks with optional parameters.
 *
 * @property context The Android [Context] required for certain operations involving authentication.
 * @author Help from ChatGPT
 */

class TaskCRUD(private val context: Context) {

    /** A reference to the [FirebaseFirestore] instance for database operations. */
    private val firestore = FirebaseFirestore.getInstance()

    /** A reference to the [FirebaseAuth] instance for authentication. */
    private val auth = FirebaseAuth.getInstance()

    /** A reference to the "tasks" collection in Firestore. */
    private val tasksCollection = firestore.collection("tasks")


    /**
     * Retrieves the user ID of the currently authenticated user.
     *
     * @throws IllegalStateException If no user is authenticated.
     */
    private val userId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")


    /**
     * Converts a Firestore [DocumentSnapshot] to a [Task] object, handling any exceptions
     * that may occur during deserialization.
     *
     * @param document The [DocumentSnapshot] to convert.
     * @return A [Task] object if successful; otherwise, `null`.
     */
    private fun documentToTask(document: DocumentSnapshot): Task? {
        return try {
            document.toObject(Task::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    /**
     * Generates a new unique task ID.
     *
     * @return A unique [String] ID generated from the "tasks" collection.
     */
    fun generateTaskId(): String {
        return tasksCollection.document().id
    }


    /**
     * Observes tasks in real-time, returning updates as a [Flow] of [Task] lists.
     *
     * Tasks are filtered by the authenticated user and sorted by deadline (ascending)
     * and creation date (descending).
     *
     * @return A [Flow] emitting lists of [Task] objects whenever data changes.
     * @throws IllegalStateException If the user is not authenticated.
     */
    fun getTasksFlow(): Flow<List<Task>> = callbackFlow {
        // Check authentication
        if (auth.currentUser == null) {
            close(IllegalStateException("User not authenticated"))
            return@callbackFlow
        }

        try {
            val registration = tasksCollection
                .whereEqualTo("userId", userId)
                .orderBy("deadline", Query.Direction.ASCENDING)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    snapshot?.let {
                        val tasks = it.documents.mapNotNull { doc ->
                            documentToTask(doc)
                        }
                        trySend(tasks)
                    }
                }

            awaitClose { registration.remove() }
        } catch (e: Exception) {
            close(e)
        }
    }


    /**
     * Adds a new [Task] to Firestore. Generates a new ID if none is provided.
     *
     * @param task The [Task] object to add.
     * @return `true` if the task was successfully added; otherwise, `false`.
     */
    suspend fun addTask(task: Task): Boolean {
        return try {
            val taskId = generateTaskId()
            Log.d("TaskCRUD", "Generated task ID: $taskId")

            val taskWithUserId = task.copy(
                id = taskId,
                userId = userId,
                createdAt = Date(),
                modifiedAt = Date()
            )

            val batch = firestore.batch()
            val docRef = tasksCollection.document(taskId)
            batch.set(docRef, taskWithUserId)
            batch.commit().await()

            Log.d("TaskCRUD", "Task added successfully with ID: $taskId")
            true
        } catch (e: Exception) {
            Log.e("TaskCRUD", "Error adding task", e)
            e.printStackTrace()
            false
        }
    }


    /**
     * Generates a unique task ID by combining a random UUID and the current system time in milliseconds.
     *
     * @return A unique [String] representing a task ID.
     */
    private fun generateUniqueTaskId(): String {
        return "${UUID.randomUUID()}_${System.currentTimeMillis()}"
    }


    /**
     * Adds a [Task] along with its recurring instances based on its [RecurrencePattern].
     *
     * @param task The base [Task] object.
     * @return `true` if all tasks were successfully added; otherwise, `false`.
     */
    suspend fun addTaskWithRecurrence(task: Task): Boolean {
        return try {
            Log.d("TaskCRUD", "===== Starting addTaskWithRecurrence =====")
            Log.d("TaskCRUD", "Task details - Name: ${task.name}, ID: ${task.id}, Recurrence: ${task.recurrence}")

            if (task.recurrence == null) {
                // No recurrence, just add Task normally
                Log.d("TaskCRUD", "No recurrence - calling standard addTask")
                val result = addTask(task)
                Log.d("TaskCRUD", "addTask result: $result")
                return result
            }

            val recurringGroupId = generateUniqueTaskId()
            val calendar = Calendar.getInstance()
            calendar.time = task.deadline

            // Create the parent (recurring) task
            val parentTask = task.copy(
                id = generateTaskId(), // Changed from generateUniqueTaskId
                userId = userId,
                createdAt = Date(),
                modifiedAt = Date(),
                recurringGroupId = recurringGroupId,
                isRecurringParent = true,
                recurringIndex = 0
            )

            Log.d("TaskCRUD", "Creating parent task with ID: ${parentTask.id}")
            tasksCollection.document(parentTask.id).set(parentTask).await()


            // Determine how many child tasks to create based on the recurrence pattern
            val numberOfInstances = when (task.recurrence) {
                RecurrencePattern.DAILY -> 7
                RecurrencePattern.WEEKLY -> 4
                RecurrencePattern.MONTHLY -> 3
                RecurrencePattern.YEARLY -> 1
            }

            // Create child tasks
            for (i in 1..numberOfInstances) {
                when (task.recurrence) {
                    RecurrencePattern.DAILY -> calendar.add(Calendar.DAY_OF_YEAR, 1)
                    RecurrencePattern.WEEKLY -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
                    RecurrencePattern.MONTHLY -> calendar.add(Calendar.MONTH, 1)
                    RecurrencePattern.YEARLY -> calendar.add(Calendar.YEAR, 1)
                }

                val recurringTask = task.copy(
                    id = generateTaskId(), // Changed from generateUniqueTaskId
                    userId = userId,
                    deadline = calendar.time,
                    createdAt = Date(),
                    modifiedAt = Date(),
                    recurringGroupId = recurringGroupId,
                    isRecurringParent = false,
                    recurringIndex = i
                )

                Log.d("TaskCRUD", "Creating recurring task ${i} with ID: ${recurringTask.id}")
                tasksCollection.document(recurringTask.id).set(recurringTask).await()
            }

            true
        } catch (e: Exception) {
            Log.e("TaskCRUD", "Error adding recurring task", e)
            false
        }
    }


    /**
     * Updates an existing [Task] in Firestore. Ensures the task belongs to the authenticated user.
     *
     * @param task The updated [Task] object.
     * @return `true` if the task was successfully updated; otherwise, `false`.
     */
    suspend fun updateTask(task: Task): Boolean {
        return try {
            // Verify the task belongs to the current user
            val existingTask = tasksCollection.document(task.id).get().await()

            if (existingTask.exists() && existingTask.getString("userId") == userId) {
                val taskWithUpdates = task.copy(
                    userId = userId,
                    modifiedAt = Date()
                )
                tasksCollection.document(task.id).set(taskWithUpdates).await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    /**
     * Deletes a [Task] from Firestore by its ID, ensuring it belongs to the authenticated user.
     *
     * @param taskId The Firestore document ID of the task to delete.
     * @return `true` if the task was successfully deleted; otherwise, `false`.
     */
    suspend fun deleteTask(taskId: String): Boolean {
        return try {
            // Verify the task belongs to the current user before deleting
            val task = tasksCollection.document(taskId).get().await()
            if (task.exists() && task.getString("userId") == userId) {
                tasksCollection.document(taskId).delete().await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    /**
     * Deletes all tasks in a given recurring group, identified by [recurringGroupId],
     * if they belong to the currently authenticated user.
     *
     * @param recurringGroupId The ID shared by all tasks in the recurring group.
     * @return `true` if all tasks in the group are successfully deleted; otherwise, `false`.
     */
    suspend fun deleteRecurringGroup(recurringGroupId: String): Boolean {
        return try {
            // Get all tasks in the recurring group
            val tasks = tasksCollection
                .whereEqualTo("recurringGroupId", recurringGroupId)
                .whereEqualTo("userId", userId)
                .get()
                .await()

            // Delete all tasks in the group
            val batch = firestore.batch()
            tasks.documents.forEach { doc ->
                batch.delete(tasksCollection.document(doc.id))
            }
            batch.commit().await()

            true
        } catch (e: Exception) {
            Log.e("TaskCRUD", "Error deleting recurring group", e)
            false
        }
    }


    /**
     * Updates all tasks in a recurring group with values from the provided [task].
     *
     * If [updateFutureOnly] is `true`, only tasks with a [Task.recurringIndex] greater than or equal
     * to the [task]'s index will be updated.
     *
     * @param task The [Task] containing updated fields. Its values are used to update matching tasks.
     * @param updateFutureOnly If `true`, only future tasks in the sequence are updated; otherwise, all are updated.
     * @return `true` if all tasks in the group are successfully updated; otherwise, `false`.
     */
    suspend fun updateRecurringGroup(task: Task, updateFutureOnly: Boolean = true): Boolean {
        return try {
            if (task.recurringGroupId == null) {
                // Not part of a recurring group, so just update a single task
                return updateTask(task)
            }

            val groupTasks = tasksCollection
                .whereEqualTo("recurringGroupId", task.recurringGroupId)
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val batch = firestore.batch()

            groupTasks.documents.forEach { doc ->
                val existingTask = doc.toObject(Task::class.java)
                if (existingTask != null) {
                    // Skip tasks with an earlier index if only updating future tasks
                    if (updateFutureOnly && existingTask.recurringIndex < task.recurringIndex) {
                        return@forEach
                    }

                    // Preserve each task's unique attributes (like deadlines and index)
                    val updatedTask = task.copy(
                        id = existingTask.id,
                        deadline = existingTask.deadline,
                        recurringIndex = existingTask.recurringIndex,
                        isRecurringParent = existingTask.isRecurringParent,
                        modifiedAt = Date()
                    )

                    batch.set(tasksCollection.document(existingTask.id), updatedTask)
                }
            }

            batch.commit().await()
            true
        } catch (e: Exception) {
            Log.e("TaskCRUD", "Error updating recurring group", e)
            false
        }
    }


    /**
     * Retrieves all tasks in a recurring group, ordered by their [Task.recurringIndex].
     *
     * @param recurringGroupId The ID shared by all tasks in the recurring group.
     * @return A list of [Task] objects ordered by their recurring index, or an empty list if none are found.
     */
    suspend fun getRecurringGroupTasks(recurringGroupId: String): List<Task> {
        return try {
            val querySnapshot = tasksCollection
                .whereEqualTo("recurringGroupId", recurringGroupId)
                .whereEqualTo("userId", userId)
                .orderBy("recurringIndex", Query.Direction.ASCENDING)
                .get()
                .await()

            querySnapshot.toObjects(Task::class.java)
        } catch (e: Exception) {
            Log.e("TaskCRUD", "Error getting recurring group tasks", e)
            emptyList()
        }
    }


    /**
     * Observes tasks in real-time as a [Flow] with optional filtering criteria.
     *
     * Tasks are filtered by [tag], [priority], [completed], and [favorite] values, and are sorted by
     * deadline (ascending) and creation date (descending). Only tasks belonging to the current user are returned.
     *
     * @param tag Optional [TaskTag] filter (e.g., WORK, SCHOOL).
     * @param priority Optional [TaskPriority] filter (e.g., HIGH, MEDIUM, LOW).
     * @param completed Optional Boolean filter indicating whether to filter by completion status.
     * @param favorite Optional Boolean filter indicating whether to filter by favorite status.
     * @return A [Flow] that emits lists of [Task] objects whenever Firestore data changes.
     */
    fun observeTasks(
        tag: TaskTag? = null,
        priority: TaskPriority? = null,
        completed: Boolean? = null,
        favorite: Boolean? = null
    ): Flow<List<Task>> = callbackFlow {
        var query = tasksCollection.whereEqualTo("userId", userId)

        // Apply filters if provided
        tag?.let { query = query.whereEqualTo("tag", it) }
        priority?.let { query = query.whereEqualTo("priority", it) }
        completed?.let { query = query.whereEqualTo("completed", it) }
        favorite?.let { query = query.whereEqualTo("favorite", it) }

        // Sort by deadline (ascending), then by createdAt (descending)
        query = query.orderBy("deadline", Query.Direction.ASCENDING)
            .orderBy("createdAt", Query.Direction.DESCENDING)

        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            snapshot?.let {
                val tasks = it.toObjects(Task::class.java)
                trySend(tasks)
            }
        }

        awaitClose { registration.remove() }
    }


    /**
     * Updates specific fields of a [Task] in Firestore. This method ensures the task belongs to
     * the currently authenticated user before applying updates. Automatically adds a `modifiedAt`
     * timestamp to the updated fields.
     *
     * @param taskId The ID of the [Task] to update.
     * @param updates A map of field names and their corresponding updated values.
     * @return `true` if the task fields are successfully updated; otherwise, `false`.
     */
    suspend fun updateTaskField(taskId: String, updates: Map<String, Any>): Boolean {
        return try {
            // Verify the task belongs to the current user before updating
            val task = tasksCollection.document(taskId).get().await()
            if (task.exists() && task.getString("userId") == userId) {
                val updatesWithTimestamp = updates.toMutableMap().apply {
                    put("modifiedAt", Date())
                }
                tasksCollection.document(taskId).update(updatesWithTimestamp).await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    /**
     * Retrieves tasks within a specified deadline range for the authenticated user.
     *
     * @param startDate The start date of the range (inclusive).
     * @param endDate The end date of the range (inclusive).
     * @return A list of [Task] objects matching the criteria; an empty list if none found.
     */
    suspend fun getTasksByDeadlineRange(
        startDate: Date,
        endDate: Date
    ): List<Task> {
        return try {
            val querySnapshot = tasksCollection
                .whereEqualTo("userId", userId)
                .whereGreaterThanOrEqualTo("deadline", startDate)
                .whereLessThanOrEqualTo("deadline", endDate)
                .orderBy("deadline", Query.Direction.ASCENDING)
                .get()
                .await()

            querySnapshot.toObjects(Task::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}