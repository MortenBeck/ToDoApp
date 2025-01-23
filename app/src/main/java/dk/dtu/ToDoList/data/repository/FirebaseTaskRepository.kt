package dk.dtu.ToDoList.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dk.dtu.ToDoList.domain.repository.TaskRepository
import dk.dtu.ToDoList.domain.model.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date

class FirebaseTaskRepository(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : TaskRepository {
    private val tasksCollection = firestore.collection("tasks")

    private val userId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")

    override fun getTasksFlow(): Flow<List<Task>> = callbackFlow {
        val registration = tasksCollection
            .whereEqualTo("userId", userId)
            .orderBy("deadline", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                error?.let { close(it); return@addSnapshotListener }
                snapshot?.let { trySend(it.toObjects(Task::class.java)) }
            }
        awaitClose { registration.remove() }
    }

    override fun observeTasks(
        tag: TaskTag?,
        priority: TaskPriority?,
        completed: Boolean?,
        favorite: Boolean?
    ): Flow<List<Task>> = callbackFlow {
        var query = tasksCollection.whereEqualTo("userId", userId)

        tag?.let { query = query.whereEqualTo("tag", it) }
        priority?.let { query = query.whereEqualTo("priority", it) }
        completed?.let { query = query.whereEqualTo("completed", it) }
        favorite?.let { query = query.whereEqualTo("favorite", it) }

        val registration = query
            .orderBy("deadline", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                error?.let { close(it); return@addSnapshotListener }
                snapshot?.let { trySend(it.toObjects(Task::class.java)) }
            }
        awaitClose { registration.remove() }
    }

    override suspend fun addTask(task: Task): Result<Unit> = runCatching {
        val taskWithUser = task.copy(userId = userId)
        tasksCollection.document(task.id).set(taskWithUser).await()
    }

    override suspend fun addTaskWithRecurrence(task: Task): Result<Unit> = runCatching {
        if (task.recurrence == null) {
            throw IllegalArgumentException("Task must have recurrence pattern")
        }

        val baseTask = task.copy(
            userId = userId,
            isRecurringParent = true,
            recurringGroupId = task.id
        )
        tasksCollection.document(task.id).set(baseTask).await()
    }

    override suspend fun updateTask(task: Task): Result<Unit> = runCatching {
        val taskWithUser = task.copy(userId = userId)
        tasksCollection.document(task.id).set(taskWithUser).await()
    }

    override suspend fun deleteTask(taskId: String): Result<Unit> = runCatching {
        tasksCollection.document(taskId).delete().await()
    }

    override suspend fun deleteRecurringGroup(recurringGroupId: String): Result<Unit> = runCatching {
        val tasks = tasksCollection
            .whereEqualTo("recurringGroupId", recurringGroupId)
            .whereEqualTo("userId", userId)
            .get()
            .await()

        val batch = firestore.batch()
        tasks.documents.forEach { doc ->
            batch.delete(tasksCollection.document(doc.id))
        }
        batch.commit().await()
    }

    override suspend fun updateRecurringGroup(task: Task, updateFutureOnly: Boolean): Result<Unit> = runCatching {
        val query = if (updateFutureOnly) {
            tasksCollection
                .whereEqualTo("recurringGroupId", task.recurringGroupId)
                .whereEqualTo("userId", userId)
                .whereGreaterThanOrEqualTo("deadline", task.deadline)
        } else {
            tasksCollection
                .whereEqualTo("recurringGroupId", task.recurringGroupId)
                .whereEqualTo("userId", userId)
        }

        val tasks = query.get().await()
        val batch = firestore.batch()
        tasks.documents.forEach { doc ->
            batch.set(tasksCollection.document(doc.id), task)
        }
        batch.commit().await()
    }

    override suspend fun getRecurringGroupTasks(recurringGroupId: String): Result<List<Task>> = runCatching {
        tasksCollection
            .whereEqualTo("recurringGroupId", recurringGroupId)
            .whereEqualTo("userId", userId)
            .orderBy("deadline", Query.Direction.ASCENDING)
            .get()
            .await()
            .toObjects(Task::class.java)
    }

    override suspend fun updateTaskField(taskId: String, updates: Map<String, Any>): Result<Unit> = runCatching {
        tasksCollection.document(taskId).update(updates).await()
    }

    override suspend fun getTasksByDeadlineRange(startDate: Date, endDate: Date): Result<List<Task>> = runCatching {
        tasksCollection
            .whereEqualTo("userId", userId)
            .whereGreaterThanOrEqualTo("deadline", startDate)
            .whereLessThanOrEqualTo("deadline", endDate)
            .orderBy("deadline", Query.Direction.ASCENDING)
            .get()
            .await()
            .toObjects(Task::class.java)
    }
}