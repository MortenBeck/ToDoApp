package dk.dtu.ToDoList.model.data.state

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class UserProfileStateTest {

    @Test
    @DisplayName("Should initialize with default values")
    fun `test default initialization`() {
        val defaultState = UserProfileState()

        assertTrue(defaultState.isAnonymous)
        assertEquals("", defaultState.userEmail)
        assertEquals("", defaultState.username)
        assertEquals(TaskStats(), defaultState.taskStats)
        assertFalse(defaultState.isLoading)
        assertNull(defaultState.error)
        assertFalse(defaultState.showNotImplementedDialog)
        assertFalse(defaultState.showLogoutDialog)
        assertFalse(defaultState.showDeleteDialog)
    }

    @Test
    @DisplayName("Should verify equality for identical instances")
    fun `test equality`() {
        val state1 = UserProfileState(
            isAnonymous = false,
            userEmail = "test@example.com",
            username = "testuser"
        )
        val state2 = UserProfileState(
            isAnonymous = false,
            userEmail = "test@example.com",
            username = "testuser"
        )

        assertEquals(state1, state2)
        assertEquals(state1.hashCode(), state2.hashCode())
    }

    @Test
    @DisplayName("Should verify inequality for different instances")
    fun `test inequality`() {
        val state1 = UserProfileState(username = "user1")
        val state2 = UserProfileState(username = "user2")

        assertNotEquals(state1, state2)
    }

    @Test
    @DisplayName("Should create a new instance when updating a property")
    fun `test immutability`() {
        val originalState = UserProfileState(username = "user1", isAnonymous = true)
        val updatedState = originalState.copy(username = "user2", isAnonymous = false)

        // Original remains unchanged
        assertEquals("user1", originalState.username)
        assertTrue(originalState.isAnonymous)

        // Updated state reflects changes
        assertEquals("user2", updatedState.username)
        assertFalse(updatedState.isAnonymous)
    }

    @Test
    @DisplayName("Should update nested TaskStats correctly")
    fun `test nested taskStats updates`() {
        val initialState = UserProfileState(taskStats = TaskStats(todayTasksCount = 5, completedTodayCount = 3))
        val updatedState = initialState.copy(taskStats = initialState.taskStats.copy(completedTodayCount = 4))

        // Original state remains unchanged
        assertEquals(3, initialState.taskStats.completedTodayCount)

        // Updated state reflects nested changes
        assertEquals(4, updatedState.taskStats.completedTodayCount)
    }
}
