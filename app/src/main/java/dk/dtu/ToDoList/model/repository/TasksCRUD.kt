package dk.dtu.ToDoList.model.repository

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dk.dtu.ToDoList.model.data.RecurrencePattern
import dk.dtu.ToDoList.model.data.Task
import dk.dtu.ToDoList.model.data.TaskPriority
import dk.dtu.ToDoList.model.data.TaskTag
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.Calendar
import java.util.UUID


/**
 * A repository class that provides CRUD (Create, Read, Update, Delete) operations
 * for [Task] objects stored in Firebase Firestore.
 *
 * @property context The Android [Context] needed for certain operations (notably authentication checks).
 */
class TaskCRUD(private val context: Context) {

    /** A reference to the [FirebaseFirestore] instance. */
    private val firestore = FirebaseFirestore.getInstance()

    /** A reference to the [FirebaseAuth] instance. */
    private val auth = FirebaseAuth.getInstance()

    /** A reference to the "tasks" collection in Firestore. */
    private val tasksCollection = firestore.collection("tasks")


    /**
     * Retrieves the user ID of the currently authenticated user.
     *
     * @throws IllegalStateException if no user is currently authenticated.
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
     * Generates a new document ID from the Firestore "tasks" collection.
     *
     * @return A [String] representing a unique document ID.
     */
    private fun generateTaskId(): String {
        return tasksCollection.document().id
    }


    /**
     * Returns a [Flow] that emits a list of [Task] objects in real-time as changes occur
     * in the Firestore "tasks" collection.
     *
     * Tasks are filtered by the authenticated user and sorted by deadline (ascending),
     * then by createdAt (descending).
     *
     * @throws IllegalStateException if the user is not authenticated.
     * @return A [Flow] emitting lists of [Task] objects whenever the data changes.
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
     * Adds a new [Task] to the Firestore "tasks" collection for the currently authenticated user.
     *
     * @param task The [Task] object to add. If `id` is empty, a new one is generated.
     * @return `true` if the operation is successful, `false` otherwise.
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
     * Generates a unique ID using [UUID] plus the current timestamp.
     *
     * @return A [String] combining a random UUID and the system time in milliseconds.
     */
    private fun generateUniqueTaskId(): String {
        return "${UUID.randomUUID()}_${System.currentTimeMillis()}"
    }


    /**
     * Adds a [Task] with a given [RecurrencePattern]. Creates a "parent" task
     * and a specified number of recurring child tasks, depending on the recurrence type.
     *
     * @param task The base [Task] to add. Its [recurrence] property determines how tasks are repeated.
     * @return `true` if all tasks (parent and child) are added successfully, `false` otherwise.
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
                null -> 0
            }

            // Create child tasks
            for (i in 1..numberOfInstances) {
                when (task.recurrence) {
                    RecurrencePattern.DAILY -> calendar.add(Calendar.DAY_OF_YEAR, 1)
                    RecurrencePattern.WEEKLY -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
                    RecurrencePattern.MONTHLY -> calendar.add(Calendar.MONTH, 1)
                    RecurrencePattern.YEARLY -> calendar.add(Calendar.YEAR, 1)
                    null -> {}
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
     * Updates an existing [Task] in the Firestore "tasks" collection, ensuring it belongs to
     * the currently authenticated user.
     *
     * @param task A [Task] containing the updated fields. The existing task is matched by its [Task.id].
     * @return `true` if the update is successful, `false` otherwise.
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
     * Deletes a [Task] by its document ID if it belongs to the currently authenticated user.
     *
     * @param taskId The Firestore document ID of the [Task].
     * @return `true` if the deletion is successful, `false` otherwise.
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
     * @return `true` if the deletion is successful, `false` otherwise.
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
     * Updates all tasks in a recurring group with values from the provided [task]. If [updateFutureOnly] is `true`,
     * only tasks whose [Task.recurringIndex] is greater than or equal to the [task]'s index will be updated.
     *
     * @param task The [Task] whose values will be used to update matching tasks in the group.
     * @param updateFutureOnly If `true`, only future tasks in the sequence are updated.
     * @return `true` if the update is successful, `false` otherwise.
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
     * Retrieves all tasks in a recurring group, ordered by [Task.recurringIndex].
     *
     * @param recurringGroupId The ID shared by all tasks in the recurring group.
     * @return A list of [Task] objects, or an empty list if none are found or an error occurs.
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
     * Observes tasks in real-time as a [Flow] based on optional filter parameters:
     * [tag], [priority], [completed], and [favorite]. Only tasks belonging to the
     * current user are returned. Results are sorted by deadline (ascending) and by
     * creation date (descending).
     *
     * @param tag An optional [TaskTag] filter (e.g., WORK, SCHOOL).
     * @param priority An optional [TaskPriority] filter (HIGH, MEDIUM, LOW).
     * @param completed An optional boolean indicating whether to filter by completion status.
     * @param favorite An optional boolean indicating whether to filter by "favorite" status.
     * @return A [Flow] that emits lists of [Task] objects whenever the Firestore query snapshot changes.
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
     * Updates specific fields of a [Task] in Firestore. Only proceeds if the
     * task belongs to the currently authenticated user. Also adds a `modifiedAt`
     * timestamp automatically.
     *
     * @param taskId The Firestore document ID of the [Task] to update.
     * @param updates A map of fields and their new values.
     * @return `true` if the update is successful, `false` otherwise.
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
     * Retrieves all tasks with a [Task.deadline] between the given [startDate] and [endDate],
     * inclusive, belonging to the currently authenticated user.
     *
     * @param startDate The inclusive lower bound of the date range.
     * @param endDate The inclusive upper bound of the date range.
     * @return A list of [Task] objects matching the criteria, or an empty list if none are found.
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