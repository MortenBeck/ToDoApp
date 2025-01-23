package dk.dtu.ToDoList.data.state

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class AppSettingsStateTest {

    @Test
    @DisplayName("Should initialize with default values")
    fun `test default initialization`() {
        val defaultState = AppSettingsState()

        assertEquals("light", defaultState.theme)
        assertEquals("english", defaultState.language)
        assertFalse(defaultState.showThemeDialog)
        assertFalse(defaultState.showLanguageDialog)
        assertFalse(defaultState.showDataUsageDialog)
        assertFalse(defaultState.showNotImplementedDialog)
        assertNull(defaultState.error)
        assertFalse(defaultState.isLoading)
        assertFalse(defaultState.isDataUsageEnabled)
    }

    @Test
    @DisplayName("Should verify equality for identical instances")
    fun `test equality`() {
        val state1 = AppSettingsState()
        val state2 = AppSettingsState()

        assertEquals(state1, state2)
        assertEquals(state1.hashCode(), state2.hashCode())
    }

    @Test
    @DisplayName("Should verify inequality for different instances")
    fun `test inequality`() {
        val state1 = AppSettingsState()
        val state2 = AppSettingsState(theme = "dark")

        assertNotEquals(state1, state2)
    }

    @Test
    @DisplayName("Should create a new instance when updating a property")
    fun `test immutability`() {
        val originalState = AppSettingsState()
        val updatedState = originalState.copy(theme = "dark")

        // Original state should remain unchanged
        assertEquals("light", originalState.theme)
        assertFalse(originalState.showThemeDialog)

        // Updated state should reflect the change
        assertEquals("dark", updatedState.theme)
        assertFalse(updatedState.showThemeDialog)
    }

    @Test
    @DisplayName("Should update individual properties correctly")
    fun `test updating properties`() {
        val initialState = AppSettingsState()

        val updatedState1 = initialState.copy(showThemeDialog = true)
        val updatedState2 = updatedState1.copy(showLanguageDialog = true)
        val updatedState3 = updatedState2.copy(error = "Something went wrong")
        val updatedState4 = updatedState3.copy(isLoading = true)

        // Assertions for each state
        assertTrue(updatedState1.showThemeDialog)
        assertFalse(updatedState1.showLanguageDialog)

        assertTrue(updatedState2.showThemeDialog)
        assertTrue(updatedState2.showLanguageDialog)

        assertEquals("Something went wrong", updatedState3.error)
        assertFalse(updatedState3.isLoading)

        assertTrue(updatedState4.isLoading)
    }
}
