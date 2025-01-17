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
    val name: String,
    val description: String = "",
    val deadline: Date,
    val priority: TaskPriority,
    val tag: TaskTag,
    val completed: Boolean = false,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val subtasks: List<SubTask> = emptyList(),
    val reminderTime: Date? = null,
    val recurrence: RecurrencePattern? = null,
    val userId: String = ""
) {
    val dueDate: Date get() = deadline
}

//Possibility for future SubTask element
data class SubTask(
    val name: String,
    val completed: Boolean = false
)

enum class RecurrencePattern {
    DAILY, WEEKLY, MONTHLY, YEARLY
}