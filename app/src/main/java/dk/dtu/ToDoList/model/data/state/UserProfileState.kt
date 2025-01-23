package dk.dtu.ToDoList.model.data.state

data class UserProfileState(
    val isAnonymous: Boolean = true,
    val userEmail: String = "",
    val username: String = "",
    val taskStats: TaskStats = TaskStats(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showNotImplementedDialog: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val showDeleteDialog: Boolean = false
)