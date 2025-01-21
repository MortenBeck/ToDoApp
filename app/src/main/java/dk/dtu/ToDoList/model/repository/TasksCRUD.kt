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

class TaskCRUD(private val context: Context) {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val tasksCollection = firestore.collection("tasks")

    private val userId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")

    private fun documentToTask(document: DocumentSnapshot): Task? {
        return try {
            document.toObject(Task::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun generateTaskId(): String {
        return tasksCollection.document().id
    }

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

    private fun generateUniqueTaskId(): String {
        return "${UUID.randomUUID()}_${System.currentTimeMillis()}"
    }

    suspend fun addTaskWithRecurrence(task: Task): Boolean {
        return try {
            Log.d("TaskCRUD", "===== Starting addTaskWithRecurrence =====")
            Log.d("TaskCRUD", "Task details - Name: ${task.name}, ID: ${task.id}, Recurrence: ${task.recurrence}")

            if (task.recurrence == null) {
                Log.d("TaskCRUD", "No recurrence - calling standard addTask")
                val result = addTask(task)
                Log.d("TaskCRUD", "addTask result: $result")
                return result
            }

            val recurringGroupId = generateUniqueTaskId()
            val calendar = Calendar.getInstance()
            calendar.time = task.deadline

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

            val numberOfInstances = when (task.recurrence) {
                RecurrencePattern.DAILY -> 7
                RecurrencePattern.WEEKLY -> 4
                RecurrencePattern.MONTHLY -> 3
                RecurrencePattern.YEARLY -> 1
                null -> 0
            }

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

    suspend fun updateRecurringGroup(task: Task, updateFutureOnly: Boolean = true): Boolean {
        return try {
            if (task.recurringGroupId == null) {
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
                    // If updating future only, skip tasks with earlier indices
                    if (updateFutureOnly && existingTask.recurringIndex < task.recurringIndex) {
                        return@forEach
                    }

                    // Maintain the original deadline intervals
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

        // Sort by deadline and creation date
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