package dk.dtu.ToDoList.model.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dk.dtu.ToDoList.model.data.Task
import dk.dtu.ToDoList.model.data.TaskPriority
import dk.dtu.ToDoList.model.data.TaskTag
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date

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
            val taskWithUserId = task.copy(
                id = generateTaskId(),
                userId = userId,
                createdAt = Date(),
                modifiedAt = Date()
            )
            tasksCollection.document(taskWithUserId.id).set(taskWithUserId).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun updateTask(task: Task): Boolean {
        return try {
            // Verify the task belongs to the current user
            val existingTask = tasksCollection.document(task.id).get().await()

            val existingUserId = existingTask.getString("userId")
            if (existingTask.exists() && existingUserId == userId) {
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

    private fun generateTaskId(): String {
        return tasksCollection.document().id
    }
}