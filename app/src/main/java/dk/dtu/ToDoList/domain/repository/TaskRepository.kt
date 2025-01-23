package dk.dtu.ToDoList.domain.repository

import dk.dtu.ToDoList.domain.model.Task
import dk.dtu.ToDoList.domain.model.TaskPriority
import dk.dtu.ToDoList.domain.model.TaskTag
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface TaskRepository {
    fun getTasksFlow(): Flow<List<Task>>
    fun observeTasks(
        tag: TaskTag? = null,
        priority: TaskPriority? = null,
        completed: Boolean? = null,
        favorite: Boolean? = null
    ): Flow<List<Task>>

    suspend fun addTask(task: Task): Result<Unit>
    suspend fun addTaskWithRecurrence(task: Task): Result<Unit>
    suspend fun updateTask(task: Task): Result<Unit>
    suspend fun deleteTask(taskId: String): Result<Unit>
    suspend fun deleteRecurringGroup(recurringGroupId: String): Result<Unit>
    suspend fun updateRecurringGroup(task: Task, updateFutureOnly: Boolean): Result<Unit>
    suspend fun getRecurringGroupTasks(recurringGroupId: String): Result<List<Task>>
    suspend fun updateTaskField(taskId: String, updates: Map<String, Any>): Result<Unit>
    suspend fun getTasksByDeadlineRange(startDate: Date, endDate: Date): Result<List<Task>>
}