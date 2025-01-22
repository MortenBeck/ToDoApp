package dk.dtu.ToDoList.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import dk.dtu.ToDoList.model.data.Task
import dk.dtu.ToDoList.model.repository.TaskCRUD
import kotlinx.coroutines.launch
import java.util.*

class TaskListViewModel(private val taskCRUD: TaskCRUD) : ViewModel() {
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

    init {
        loadTasks()
    }

    fun setTasks(newTasks: List<Task>) {
        _tasks.value = newTasks
    }

    fun requestDelete(task: Task) {
        _taskToDelete.value = task
    }

    fun confirmDelete(task: Task, deleteAll: Boolean) {
        viewModelScope.launch {
            if (deleteAll && task.recurringGroupId != null) {
                val result = taskCRUD.deleteRecurringGroup(task.recurringGroupId)
                if (result) {
                    _tasks.value = tasks.value.filterNot { it.recurringGroupId == task.recurringGroupId }
                }
            } else {
                val result = taskCRUD.deleteTask(task.id)
                if (result) {
                    _tasks.value = tasks.value - task
                }
            }
            _taskToDelete.value = null
        }
    }

    fun cancelDelete() {
        _taskToDelete.value = null
    }

    private fun loadTasks() {
        viewModelScope.launch {
            taskCRUD.getTasksFlow().collect { taskList ->
                _tasks.value = taskList
            }
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            try {
                if (taskCRUD.addTask(task)) {
                    loadTasks() // Reload tasks to reflect the addition
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                if (taskCRUD.updateTask(task)) {
                    loadTasks() // Reload tasks to reflect the update
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            try {
                if (taskCRUD.deleteTask(taskId)) {
                    loadTasks() // Reload tasks to reflect the deletion
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteRecurringGroup(groupId: String) {
        viewModelScope.launch {
            try {
                if (taskCRUD.deleteRecurringGroup(groupId)) {
                    loadTasks() // Reload tasks to reflect the deletion
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addTaskWithRecurrence(task: Task) {
        viewModelScope.launch {
            try {
                val result = taskCRUD.addTaskWithRecurrence(task)
                if (result) {
                    loadTasks() // Reload tasks to reflect the addition
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
