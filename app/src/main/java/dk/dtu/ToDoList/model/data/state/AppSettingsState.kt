package dk.dtu.ToDoList.data.state

data class AppSettingsState(
    val theme: String = "light",
    val language: String = "english",
    val showThemeDialog: Boolean = false,
    val showLanguageDialog: Boolean = false,
    val showDataUsageDialog: Boolean = false,
    val showNotImplementedDialog: Boolean = false,
    val error: String? = null,
    val isLoading: Boolean = false,
    val isDataUsageEnabled: Boolean = false
)