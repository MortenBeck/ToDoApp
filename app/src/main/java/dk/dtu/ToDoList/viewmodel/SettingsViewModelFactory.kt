package dk.dtu.ToDoList.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dk.dtu.ToDoList.model.repository.TaskCRUD
import dk.dtu.ToDoList.repository.AuthRepository

class SettingsViewModelFactory(
    private val authRepository: AuthRepository,
    private val taskRepository: TaskCRUD
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(authRepository, taskRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}