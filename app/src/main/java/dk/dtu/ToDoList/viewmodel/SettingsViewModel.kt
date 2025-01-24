package dk.dtu.ToDoList.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dk.dtu.ToDoList.data.events.SettingsEvent
import dk.dtu.ToDoList.data.state.AppSettingsState
import dk.dtu.ToDoList.model.data.state.TaskStats
import dk.dtu.ToDoList.model.data.state.UserProfileState
import dk.dtu.ToDoList.model.repository.TaskCRUD
import dk.dtu.ToDoList.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * @author helped with chatGPT
 */
class SettingsViewModel(
    private val authRepository: AuthRepository,
    private val taskRepository: TaskCRUD
) : ViewModel() {

    private val _profileState = MutableStateFlow(UserProfileState())
    val profileState: StateFlow<UserProfileState> = _profileState.asStateFlow()

    private val _appSettingsState = MutableStateFlow(AppSettingsState())
    val appSettingsState: StateFlow<AppSettingsState> = _appSettingsState.asStateFlow()

    init {
        loadUserProfile()
        observeTaskStats()
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.Profile.LoadProfile -> loadUserProfile()
            is SettingsEvent.Profile.LoadTaskStats -> observeTaskStats()
            is SettingsEvent.Profile.ToggleNotImplementedDialog ->
                updateProfileDialogState("notImplemented", event.show)

            // Exchange Account.Logout with these for logout feature. (Dialog in AccountSettingsScreen.kt is also needed)
            // is SettingsEvent.Account.Logout -> handleLogout()
            // is SettingsEvent.Account.DeleteAccount -> handleDeleteAccount()
            // is SettingsEvent.Account.ToggleLogoutDialog ->
            //    updateProfileDialogState("logout", event.show)
            // is SettingsEvent.Account.ToggleDeleteDialog ->
            //    updateProfileDialogState("delete", event.show)


            is SettingsEvent.Account.Logout ->
                updateProfileDialogState("notImplemented", true)
            is SettingsEvent.Account.DeleteAccount ->
                updateProfileDialogState("notImplemented", true)

            is SettingsEvent.Account.ToggleLogoutDialog ->
                updateProfileDialogState("logout", event.show)
            is SettingsEvent.Account.ToggleDeleteDialog ->
                updateProfileDialogState("delete", event.show)

            is SettingsEvent.App.ToggleDataUsageDialog ->
                updateAppDialogState("dataUsage", event.show)
            is SettingsEvent.App.ToggleNotImplementedDialog ->
                updateAppDialogState("notImplemented", event.show)
            is SettingsEvent.App.ToggleThemeDialog ->
                updateAppDialogState("theme", event.show)
            is SettingsEvent.App.ToggleLanguageDialog ->
                updateAppDialogState("language", event.show)
            is SettingsEvent.App.SetTheme ->
                _appSettingsState.value = _appSettingsState.value.copy(theme = event.theme)
            is SettingsEvent.App.SetLanguage ->
                _appSettingsState.value = _appSettingsState.value.copy(language = event.language)
        }
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                _profileState.value = _profileState.value.copy(isLoading = true)
                _profileState.value = _profileState.value.copy(
                    isAnonymous = authRepository.isCurrentUserAnonymous(),
                    userEmail = authRepository.getCurrentUserEmail(),
                    username = authRepository.getCurrentUsername(),
                    isLoading = false
                )
            } catch (e: Exception) {
                _profileState.value = _profileState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }

    private fun observeTaskStats() {
        viewModelScope.launch {
            taskRepository.observeTasks().collect { tasks ->
                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val tomorrow = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                val todayTasks = tasks.filter { task ->
                    task.deadline >= today.time && task.deadline < tomorrow.time
                }

                val stats = TaskStats(
                    todayTasksCount = todayTasks.size,
                    completedTodayCount = todayTasks.count { it.completed },
                    upcomingTasksCount = tasks.count { !it.completed && it.deadline >= tomorrow.time }
                )

                _profileState.value = _profileState.value.copy(taskStats = stats)
            }
        }
    }

    private fun handleLogout() {
        viewModelScope.launch {
            try {
                authRepository.signOut()
            } catch (e: Exception) {
                _profileState.value = _profileState.value.copy(error = e.message)
            }
        }
    }

    private fun handleDeleteAccount() {
        viewModelScope.launch {
            try {
                authRepository.deleteCurrentUser()
            } catch (e: Exception) {
                _profileState.value = _profileState.value.copy(error = e.message)
            }
        }
    }

    private fun updateProfileDialogState(dialog: String, show: Boolean) {
        _profileState.value = when (dialog) {
            "notImplemented" -> _profileState.value.copy(showNotImplementedDialog = show)
            "logout" -> _profileState.value.copy(showLogoutDialog = show)
            "delete" -> _profileState.value.copy(showDeleteDialog = show)
            else -> _profileState.value
        }
    }

    private fun updateAppDialogState(dialog: String, show: Boolean) {
        _appSettingsState.value = when (dialog) {
            "dataUsage" -> _appSettingsState.value.copy(showDataUsageDialog = show)
            "notImplemented" -> _appSettingsState.value.copy(showNotImplementedDialog = show)
            "theme" -> _appSettingsState.value.copy(showThemeDialog = show)
            "language" -> _appSettingsState.value.copy(showLanguageDialog = show)
            else -> _appSettingsState.value
        }
    }
}