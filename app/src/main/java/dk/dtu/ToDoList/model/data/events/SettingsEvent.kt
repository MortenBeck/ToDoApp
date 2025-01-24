package dk.dtu.ToDoList.data.events

sealed class SettingsEvent {
    sealed class Profile : SettingsEvent() {
        object LoadProfile : Profile()
        object LoadTaskStats : Profile()
        data class ToggleNotImplementedDialog(val show: Boolean) : Profile()
    }

    sealed class Account : SettingsEvent() {
        data class ToggleLogoutDialog(val show: Boolean) : Account()
        data class ToggleDeleteDialog(val show: Boolean) : Account()
        object Logout : Account()
        object DeleteAccount : Account()
    }

    sealed class App : SettingsEvent() {
        data class ToggleDataUsageDialog(val show: Boolean) : App()
        data class ToggleNotImplementedDialog(val show: Boolean) : App()
        data class ToggleThemeDialog(val show: Boolean) : App()
        data class ToggleLanguageDialog(val show: Boolean) : App()
        data class SetTheme(val theme: String) : App()
        data class SetLanguage(val language: String) : App()
    }
}