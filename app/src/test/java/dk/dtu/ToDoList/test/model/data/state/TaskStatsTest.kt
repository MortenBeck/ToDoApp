package dk.dtu.ToDoList.model.data.state

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class TaskStatsTest {

    @Test
    @DisplayName("Should initialize with default values")
    fun `test default initialization`() {
        val defaultStats = TaskStats()

        assertEquals(0, defaultStats.todayTasksCount)
        assertEquals(0, defaultStats.completedTodayCount)
        assertEquals(0, defaultStats.upcomingTasksCount)
        assertEquals(0f, defaultStats.completionRate)
    }

    @Test
    @DisplayName("Should calculate completionRate correctly for non-zero tasks")
    fun `test completionRate calculation with tasks`() {
        val stats = TaskStats(todayTasksCount = 10, completedTodayCount = 7)

        assertEquals(0.7f, stats.completionRate)
    }

    @Test
    @DisplayName("Should return completionRate as 0 when there are no tasks")
    fun `test completionRate calculation with zero tasks`() {
        val stats = TaskStats(todayTasksCount = 0, completedTodayCount = 0)

        assertEquals(0f, stats.completionRate)
    }

    @Test
    @DisplayName("Should return completionRate as 0 when completedTodayCount is zero")
    fun `test completionRate calculation with zero completed tasks`() {
        val stats = TaskStats(todayTasksCount = 5, completedTodayCount = 0)

        assertEquals(0f, stats.completionRate)
    }

    @Test
    @DisplayName("Should verify equality for identical instances")
    fun `test equality`() {
        val stats1 = TaskStats(10, 5, 15)
        val stats2 = TaskStats(10, 5, 15)

        assertEquals(stats1, stats2)
        assertEquals(stats1.hashCode(), stats2.hashCode())
    }

    @Test
    @DisplayName("Should verify inequality for different instances")
    fun `test inequality`() {
        val stats1 = TaskStats(10, 5, 15)
        val stats2 = TaskStats(12, 5, 10)

        assertNotEquals(stats1, stats2)
    }

    @Test
    @DisplayName("Should create a new instance when updating a property")
    fun `test immutability`() {
        val originalStats = TaskStats(todayTasksCount = 10, completedTodayCount = 5, upcomingTasksCount = 15)
        val updatedStats = originalStats.copy(todayTasksCount = 12)

        // Original should remain unchanged
        assertEquals(10, originalStats.todayTasksCount)
        assertEquals(5, originalStats.completedTodayCount)
        assertEquals(15, originalStats.upcomingTasksCount)

        // Updated instance should reflect changes
        assertEquals(12, updatedStats.todayTasksCount)
        assertEquals(5, updatedStats.completedTodayCount)
        assertEquals(15, updatedStats.upcomingTasksCount)
    }
}
