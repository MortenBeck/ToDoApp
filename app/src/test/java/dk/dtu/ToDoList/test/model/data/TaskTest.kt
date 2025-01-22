package dk.dtu.ToDoList.model.data

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.util.*

class TaskTest {

    @Test
    fun `Task initializes with default values`() {
        // Act
        val task = Task()

        // Assert
        assertEquals("", task.id)
        assertEquals("", task.name)
        assertEquals("", task.description)
        assertEquals(TaskPriority.LOW, task.priority)
        assertEquals(TaskTag.WORK, task.tag)
        assertFalse(task.completed)
        assertTrue(task.subtasks.isEmpty())
        assertNull(task.reminderTime)
        assertNull(task.recurrence)
        assertEquals("", task.userId)
        assertNull(task.recurringGroupId)
        assertFalse(task.isRecurringParent)
        assertEquals(0, task.recurringIndex)
    }

    @Test
    fun `Task handles custom values`() {
        // Arrange
        val now = Date()
        val subtasks = listOf(SubTask("Subtask 1", true), SubTask("Subtask 2", false))

        // Act
        val task = Task(
            id = "123",
            name = "Test Task",
            description = "A task for testing",
            deadline = now,
            priority = TaskPriority.HIGH,
            tag = TaskTag.SCHOOL,
            completed = true,
            createdAt = now,
            modifiedAt = now,
            subtasks = subtasks,
            reminderTime = now,
            recurrence = RecurrencePattern.DAILY,
            userId = "user_001",
            recurringGroupId = "group_001",
            isRecurringParent = true,
            recurringIndex = 1
        )

        // Assert
        assertEquals("123", task.id)
        assertEquals("Test Task", task.name)
        assertEquals("A task for testing", task.description)
        assertEquals(now, task.deadline)
        assertEquals(TaskPriority.HIGH, task.priority)
        assertEquals(TaskTag.SCHOOL, task.tag)
        assertTrue(task.completed)
        assertEquals(2, task.subtasks.size)
        assertEquals(now, task.reminderTime)
        assertEquals(RecurrencePattern.DAILY, task.recurrence)
        assertEquals("user_001", task.userId)
        assertEquals("group_001", task.recurringGroupId)
        assertTrue(task.isRecurringParent)
        assertEquals(1, task.recurringIndex)
    }

    @Test
    fun `Task calculates dueDate correctly`() {
        // Arrange
        val now = Date()

        // Act
        val task = Task(deadline = now)

        // Assert
        assertEquals(now, task.dueDate)
    }

    @Test
    fun `SubTask initializes with default values`() {
        // Act
        val subTask = SubTask()

        // Assert
        assertEquals("", subTask.name)
        assertFalse(subTask.completed)
    }

    @Test
    fun `SubTask handles custom values`() {
        // Act
        val subTask = SubTask(name = "Test Subtask", completed = true)

        // Assert
        assertEquals("Test Subtask", subTask.name)
        assertTrue(subTask.completed)
    }

    @Test
    fun `RecurrencePattern enum has correct values`() {
        // Assert
        assertEquals(4, RecurrencePattern.values().size)
        assertTrue(RecurrencePattern.values().contains(RecurrencePattern.DAILY))
        assertTrue(RecurrencePattern.values().contains(RecurrencePattern.WEEKLY))
        assertTrue(RecurrencePattern.values().contains(RecurrencePattern.MONTHLY))
        assertTrue(RecurrencePattern.values().contains(RecurrencePattern.YEARLY))
    }

    @Test
    fun `Task with no subtasks has empty list`() {
        // Act
        val task = Task(subtasks = emptyList())

        // Assert
        assertTrue(task.subtasks.isEmpty())
    }

    @Test
    fun `Task with subtasks returns correct list`() {
        // Arrange
        val subtasks = listOf(
            SubTask("Subtask 1", true),
            SubTask("Subtask 2", false)
        )

        // Act
        val task = Task(subtasks = subtasks)

        // Assert
        assertEquals(2, task.subtasks.size)
        assertEquals("Subtask 1", task.subtasks[0].name)
        assertTrue(task.subtasks[0].completed)
        assertEquals("Subtask 2", task.subtasks[1].name)
        assertFalse(task.subtasks[1].completed)
    }
}
