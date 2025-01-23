package dk.dtu.ToDoList.model.data.task

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.Date

class TaskTest {

    @Test
    @DisplayName("Should initialize Task with default values")
    fun `test Task default initialization`() {
        val task = Task()

        assertEquals("", task.id)
        assertEquals("", task.name)
        assertEquals("", task.description)
        assertNotNull(task.deadline)
        assertEquals(TaskPriority.LOW, task.priority)
        assertEquals(TaskTag.WORK, task.tag)
        assertFalse(task.completed)
        assertNotNull(task.createdAt)
        assertNotNull(task.modifiedAt)
        assertTrue(task.subtasks.isEmpty())
        assertNull(task.reminderTime)
        assertNull(task.recurrence)
        assertEquals("", task.userId)
        assertNull(task.recurringGroupId)
        assertFalse(task.isRecurringParent)
        assertEquals(0, task.recurringIndex)
    }

    @Test
    @DisplayName("Should verify Task equality for identical instances")
    fun `test Task equality`() {
        val fixedDate = Date(1672531200000L) // Fixed timestamp for consistency

        val task1 = Task(
            id = "1",
            name = "Test Task",
            priority = TaskPriority.HIGH,
            deadline = fixedDate,
            createdAt = fixedDate,
            modifiedAt = fixedDate
        )
        val task2 = Task(
            id = "1",
            name = "Test Task",
            priority = TaskPriority.HIGH,
            deadline = fixedDate,
            createdAt = fixedDate,
            modifiedAt = fixedDate
        )

        // Assert equality
        assertEquals(task1, task2, "Task objects should be equal")

        // Assert hashCode
        assertEquals(task1.hashCode(), task2.hashCode(), "Hash codes should be equal")
    }


    @Test
    @DisplayName("Should verify Task inequality for different instances")
    fun `test Task inequality`() {
        val task1 = Task(id = "1", name = "Task A")
        val task2 = Task(id = "2", name = "Task B")

        assertNotEquals(task1, task2)
    }

    @Test
    @DisplayName("Should update Task properties immutably")
    fun `test Task immutability`() {
        val originalTask = Task(name = "Original Task")
        val updatedTask = originalTask.copy(name = "Updated Task", priority = TaskPriority.HIGH)

        assertEquals("Original Task", originalTask.name)
        assertEquals(TaskPriority.LOW, originalTask.priority)

        assertEquals("Updated Task", updatedTask.name)
        assertEquals(TaskPriority.HIGH, updatedTask.priority)
    }

    @Test
    @DisplayName("Should verify default SubTask initialization")
    fun `test SubTask default initialization`() {
        val subTask = SubTask()

        assertEquals("", subTask.name)
        assertFalse(subTask.completed)
    }

    @Test
    @DisplayName("Should verify SubTask equality")
    fun `test SubTask equality`() {
        val subTask1 = SubTask(name = "SubTask A", completed = true)
        val subTask2 = SubTask(name = "SubTask A", completed = true)

        assertEquals(subTask1, subTask2)
    }

    @Test
    @DisplayName("Should verify default RecurrencePattern")
    fun `test RecurrencePattern`() {
        val daily = RecurrencePattern.DAILY
        val weekly = RecurrencePattern.WEEKLY

        assertNotNull(daily)
        assertNotNull(weekly)
        assertNotEquals(daily, weekly)
    }
}
