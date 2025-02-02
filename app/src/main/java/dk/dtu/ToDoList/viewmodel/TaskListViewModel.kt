package dk.dtu.ToDoList.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import dk.dtu.ToDoList.model.data.task.Task
import dk.dtu.ToDoList.model.data.task.TaskPriority
import dk.dtu.ToDoList.model.data.task.TaskTag
import dk.dtu.ToDoList.model.repository.TaskCRUD
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.util.*

/**
 * @author refactored by chatGPT
 */

class TaskListViewModel(
    private val taskCRUD: TaskCRUD,
    private val deleteTaskManager: DeleteTaskManager = DeleteTaskManager()
) : ViewModel() {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks
    val taskToDelete = deleteTaskManager.taskToDelete
    private val _originalTasks = MutableStateFlow<List<Task>>(emptyList())

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
        deleteTaskManager.requestDelete(task)
    }

    fun cancelDelete() {
        deleteTaskManager.cancelDelete()
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
            deleteTaskManager.clearTask()
        }
    }

    fun resetToOriginal() {
        _tasks.value = _originalTasks.value
    }

    fun loadTasks() {
        viewModelScope.launch {
            taskCRUD.getTasksFlow().collect { taskList ->
                _originalTasks.value = taskList
                _tasks.value = taskList
            }
        }
    }

    fun addTask(task: Task) = viewModelScope.launch {
        try {
            if (taskCRUD.addTask(task)) loadTasks()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        try {
            if (taskCRUD.updateTask(task)) loadTasks()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addTaskWithRecurrence(task: Task) = viewModelScope.launch {
        try {
            if (taskCRUD.addTaskWithRecurrence(task)) loadTasks()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun filterTasks(
        filteredTasks: List<Task>
    ) {
        _tasks.value = if (filteredTasks.isEmpty()) {
            emptyList()
        } else {
            filteredTasks
        }
    }
    fun applyFilters(
        dateRange: Pair<Date?, Date?>? = null,
        selectedTag: TaskTag? = null,
        selectedPriority: TaskPriority? = null,
        hideCompletedTasks: Boolean = false
    ) {
        val filteredList = _originalTasks.value.filter { task ->
            val dateMatches = if (dateRange?.first != null && dateRange.second != null) {
                val taskDate = task.deadline.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                val startDate = dateRange.first!!.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                val endDate = dateRange.second!!.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                !taskDate.isBefore(startDate) && !taskDate.isAfter(endDate)
            } else true

            val tagMatches = selectedTag == null || task.tag == selectedTag
            val priorityMatches = selectedPriority == null || task.priority == selectedPriority
            val completionMatches = !hideCompletedTasks || !task.completed

            dateMatches && tagMatches && priorityMatches && completionMatches
        }

        _tasks.value = filteredList
    }


}