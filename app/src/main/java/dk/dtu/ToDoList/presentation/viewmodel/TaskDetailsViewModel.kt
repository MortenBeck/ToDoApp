package dk.dtu.ToDoList.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dk.dtu.ToDoList.domain.model.TaskPriority
import dk.dtu.ToDoList.domain.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import dk.dtu.ToDoList.domain.model.Task
import dk.dtu.ToDoList.domain.model.TaskTag
import java.util.Date

class TaskDetailsViewModel(
    private val taskRepository: TaskRepository,
    private val initialTask: Task  // Add initialTask parameter
) : ViewModel() {
    private val _uiState = MutableStateFlow(TaskDetailsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(
            taskName = initialTask.name,
            priority = initialTask.priority,
            tag = initialTask.tag,
            deadline = initialTask.deadline.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        )}
    }

    fun updateTaskName(name: String) = _uiState.update { it.copy(taskName = name) }
    fun updatePriority(priority: TaskPriority) = _uiState.update { it.copy(priority = priority) }
    fun updateTag(taskTag: TaskTag) = _uiState.update { it.copy(tag = taskTag) }  // Change type to TaskTag
    fun updateDeadline(deadline: LocalDate) = _uiState.update { it.copy(deadline = deadline) }

    fun saveTask(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTask(task.copy(
                name = uiState.value.taskName,
                priority = uiState.value.priority,
                tag = uiState.value.tag,
                deadline = Date.from(uiState.value.deadline.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                modifiedAt = Date()
            ))
        }
    }

    fun requestDeleteTask(task: Task) = _uiState.update { it.copy(taskToDelete = task) }
    fun cancelDelete() = _uiState.update { it.copy(taskToDelete = null) }
    fun confirmDelete(deleteAll: Boolean) {
        viewModelScope.launch {
            uiState.value.taskToDelete?.let { task ->
                taskRepository.deleteTask(task.id)  // Use task.id instead of task
            }
            _uiState.update { it.copy(taskToDelete = null) }
        }
    }
}

data class TaskDetailsUiState(
    val taskName: String = "",
    val priority: TaskPriority = TaskPriority.LOW,
    val tag: TaskTag = TaskTag.PRIVATE,  // Change type to TaskTag
    val deadline: LocalDate = LocalDate.now(),
    val taskToDelete: Task? = null
)

class TaskDetailsViewModelFactory(
    private val taskRepository: TaskRepository,
    private val initialTask: Task
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskDetailsViewModel(taskRepository, initialTask) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}