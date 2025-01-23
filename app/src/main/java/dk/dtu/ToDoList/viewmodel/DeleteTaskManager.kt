package dk.dtu.ToDoList.viewmodel

import dk.dtu.ToDoList.model.data.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DeleteTaskManager {
    private val _taskToDelete = MutableStateFlow<Task?>(null)
    val taskToDelete: StateFlow<Task?> = _taskToDelete

    fun requestDelete(task: Task) {
        _taskToDelete.value = task
    }

    fun cancelDelete() {
        _taskToDelete.value = null
    }

    fun clearTask() {
        _taskToDelete.value = null
    }
}