package dk.dtu.ToDoList.data

import java.util.Date



//Enum class handling priority
enum class TaskPriority {
    HIGH, MEDIUM, LOW
}


//Enum class handling tags for the tasks
enum class TaskTag {
    WORK, SCHOOL, SPORT, TRANSPORT, PET, HOME, PRIVATE
}

//Data class for for potential future implementation of the tag system
data class Tag(
    val id: String,
    val name: String,
    val color: String? = null
)


//Data class for linking tasks and tags, for potential future implementation
data class TaskTagg(
    val taskId: String,
    val tagId: String
)


//Data class handling the task itself
data class Task(
    //val id: String,
    val name: String,
    val deadline: Date,
    //val description: String?,
    val priority: TaskPriority,
    val tag: TaskTag,
    val completed: Boolean,
    //val tags: List<String> = emptyList()
    val favorite: Boolean = false

)

//Data class for handling future implementation for parent and children tasks
data class subTask(
    val id: String,
    val parentTaskId: String,
    val name: String,
    val completed: Boolean = false
)

//Data class for handling future implementation for users
data class User(
    val userID: String,
    val name: String,
    val email: String,
    val profilePictureUrl: String? = null
)


//Data class for potential future updates to calendar
data class CalendarEntry(
    val id: String,
    val taskId: String,
    val date: String
)