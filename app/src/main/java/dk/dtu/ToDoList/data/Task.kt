package dk.dtu.ToDoList.data

import java.util.Date

enum class TaskPriority {
    HIGH, MEDIUM, LOW
}

enum class TaskTag {
    WORK, SCHOOL, SPORT, TRANSPORT, PET, HOME, PRIVATE
}

data class Task(
    val name: String,
    val deadline: Date,
    val priority: TaskPriority,
    val completed: Boolean = false
)