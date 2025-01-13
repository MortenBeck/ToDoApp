package dk.dtu.ToDoList.data
import java.util.Date

// Enum class handling priority
enum class TaskPriority {
    HIGH, MEDIUM, LOW
}

// Enum class handling tags for the tasks
enum class TaskTag {
    WORK, SCHOOL, SPORT, TRANSPORT, PET, HOME, PRIVATE
}

// Data class handling the task itself
data class Task(
    val id: String? = null,  // Optional ID, for Firestore
    val name: String,
    val deadline: Date,
    val description: String,
    val priority: TaskPriority,  // Default priority is LOW
    val tag: TaskTag,  // Default tag is WORK
    val completed: Boolean = false,
    val favorite: Boolean = false,
    val userId: String,  // User ID for linking tasks to specific users
    val isDeleted: Boolean = false
)

// Data class for handling future implementation for parent and children tasks
data class SubTask(
    val id: String,
    val parentTaskId: String,
    val name: String,
    val completed: Boolean = false
)

// Data class for handling future implementation for users
data class User(
    val userID: String,
    val name: String,
    val email: String,
    val profilePictureUrl: String? = null,
    val notificationEnabled: Boolean = true
)

// Data class for potential future updates to calendar
data class CalendarEntry(
    val id: String,
    val taskId: String,
    val date: String
)