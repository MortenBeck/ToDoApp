package dk.dtu.ToDoList.model.data

import java.util.Date

enum class TaskPriority {
    HIGH, MEDIUM, LOW
}

enum class TaskTag {
    WORK, SCHOOL, SPORT, TRANSPORT, PET, HOME, PRIVATE, SOCIAL
}

data class Task(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val deadline: Date = Date(),
    val priority: TaskPriority = TaskPriority.LOW,
    val tag: TaskTag = TaskTag.WORK,
    val completed: Boolean = false,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val subtasks: List<SubTask> = emptyList(),
    val reminderTime: Date? = null,
    val recurrence: RecurrencePattern? = null,
    val userId: String = ""
) {
    // Required empty constructor for Firestore
    constructor() : this(
        name = "",
        deadline = Date()
    )

    val dueDate: Date get() = deadline
}

// Maybe subtasks at some point?
data class SubTask(
    val name: String = "",
    val completed: Boolean = false
) {
    // Required empty constructor for Firestore
    constructor() : this("")
}

enum class RecurrencePattern {
    DAILY, WEEKLY, MONTHLY, YEARLY
}