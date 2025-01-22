package dk.dtu.ToDoList.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import dk.dtu.ToDoList.model.data.Task
import java.util.*

class TaskListViewModel : ViewModel() {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    private val _taskToDelete = MutableStateFlow<Task?>(null)
    val taskToDelete: StateFlow<Task?> = _taskToDelete

    val categorizedTasks: StateFlow<Map<String, List<Task>>> = tasks.map { tasks ->
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val tomorrowStart = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        mapOf(
            "Expired" to tasks.filter { it.deadline < todayStart && !it.completed }.sortedBy { it.deadline },
            "Today" to tasks.filter { it.deadline >= todayStart && it.deadline < tomorrowStart }.sortedBy { it.deadline },
            "Future" to tasks.filter { it.deadline >= tomorrowStart }.sortedBy { it.deadline },
            "Completed" to tasks.filter { it.deadline < todayStart && it.completed }.sortedBy { it.deadline }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun setTasks(newTasks: List<Task>) {
        _tasks.value = newTasks
    }

    fun requestDelete(task: Task) {
        _taskToDelete.value = task
    }

    fun confirmDelete(task: Task, deleteAll: Boolean) {
        val updatedTasks = if (deleteAll && task.recurringGroupId != null) {
            tasks.value.filterNot { it.recurringGroupId == task.recurringGroupId }
        } else {
            tasks.value - task
        }
        _tasks.value = updatedTasks
        _taskToDelete.value = null
    }

    fun cancelDelete() {
        _taskToDelete.value = null
    }
}
