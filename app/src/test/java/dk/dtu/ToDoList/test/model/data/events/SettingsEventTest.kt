package dk.dtu.ToDoList.data.events

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class SettingsEventTest {

    @Test
    @DisplayName("Should create instances of Profile events")
    fun `test Profile events creation`() {
        val loadProfile = SettingsEvent.Profile.LoadProfile
        val loadTaskStats = SettingsEvent.Profile.LoadTaskStats
        val toggleNotImplementedDialog = SettingsEvent.Profile.ToggleNotImplementedDialog(true)

        assertNotNull(loadProfile)
        assertNotNull(loadTaskStats)
        assertEquals(true, toggleNotImplementedDialog.show)
    }

    @Test
    @DisplayName("Should create instances of Account events")
    fun `test Account events creation`() {
        val toggleLogoutDialog = SettingsEvent.Account.ToggleLogoutDialog(true)
        val toggleDeleteDialog = SettingsEvent.Account.ToggleDeleteDialog(false)
        val logout = SettingsEvent.Account.Logout
        val deleteAccount = SettingsEvent.Account.DeleteAccount

        assertNotNull(toggleLogoutDialog)
        assertEquals(true, toggleLogoutDialog.show)

        assertNotNull(toggleDeleteDialog)
        assertEquals(false, toggleDeleteDialog.show)

        assertNotNull(logout)
        assertNotNull(deleteAccount)
    }

    @Test
    @DisplayName("Should create instances of App events")
    fun `test App events creation`() {
        val toggleDataUsageDialog = SettingsEvent.App.ToggleDataUsageDialog(true)
        val toggleThemeDialog = SettingsEvent.App.ToggleThemeDialog(false)
        val setTheme = SettingsEvent.App.SetTheme("Dark")
        val setLanguage = SettingsEvent.App.SetLanguage("en")

        assertNotNull(toggleDataUsageDialog)
        assertEquals(true, toggleDataUsageDialog.show)

        assertNotNull(toggleThemeDialog)
        assertEquals(false, toggleThemeDialog.show)

        assertNotNull(setTheme)
        assertEquals("Dark", setTheme.theme)

        assertNotNull(setLanguage)
        assertEquals("en", setLanguage.language)
    }

    @Test
    @DisplayName("Should verify equality of events with same data")
    fun `test equality of events`() {
        val event1 = SettingsEvent.App.SetTheme("Dark")
        val event2 = SettingsEvent.App.SetTheme("Dark")
        val event3 = SettingsEvent.App.SetTheme("Light")

        assertEquals(event1, event2)
        assertNotEquals(event1, event3)
    }

    @Test
    @DisplayName("Should ensure exhaustiveness in when expressions")
    fun `test exhaustiveness of events`() {
        val event: SettingsEvent = SettingsEvent.Profile.LoadProfile

        val result = when (event) {
            is SettingsEvent.Profile.LoadProfile -> "Load Profile"
            is SettingsEvent.Profile.LoadTaskStats -> "Load Task Stats"
            is SettingsEvent.Profile.ToggleNotImplementedDialog -> "Toggle Dialog"
            is SettingsEvent.Account.ToggleLogoutDialog -> "Logout Dialog"
            is SettingsEvent.Account.ToggleDeleteDialog -> "Delete Dialog"
            is SettingsEvent.Account.Logout -> "Logout"
            is SettingsEvent.Account.DeleteAccount -> "Delete Account"
            is SettingsEvent.App.ToggleDataUsageDialog -> "Data Usage Dialog"
            is SettingsEvent.App.ToggleNotImplementedDialog -> "Not Implemented Dialog"
            is SettingsEvent.App.ToggleThemeDialog -> "Theme Dialog"
            is SettingsEvent.App.ToggleLanguageDialog -> "Language Dialog"
            is SettingsEvent.App.SetTheme -> "Set Theme"
            is SettingsEvent.App.SetLanguage -> "Set Language"
        }

        assertNotNull(result)
        assertEquals("Load Profile", result)
    }
}
