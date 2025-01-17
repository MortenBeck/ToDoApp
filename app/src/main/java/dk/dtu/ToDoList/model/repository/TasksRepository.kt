package dk.dtu.ToDoList.model.repository


import dk.dtu.ToDoList.model.data.Task
import dk.dtu.ToDoList.model.data.TaskPriority
import dk.dtu.ToDoList.model.data.TaskTag
import java.text.SimpleDateFormat
import java.util.Locale

object TasksRepository {

    val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)

    val Tasks = listOf(
        Task(
            name = "Homework - UX",
            deadline = simpleDateFormat.parse("30-11-2024")!!,
            priority = TaskPriority.HIGH,
            tag = TaskTag.SCHOOL,
            completed = false
        ),
        Task(
            name = "Fix project at work",
            deadline = simpleDateFormat.parse("02-12-2024")!!,
            priority = TaskPriority.HIGH,
            tag = TaskTag.WORK,
            completed = false
        ),
        Task(
            name = "Walk the dog",
            deadline = simpleDateFormat.parse("02-12-2024")!!,
            priority = TaskPriority.MEDIUM,
            tag = TaskTag.PET,
            completed = false
        ),
        Task(
            name = "Cancel Netflix subscription",
            deadline = simpleDateFormat.parse("03-12-2024")!!,
            priority = TaskPriority.LOW,
            tag = TaskTag.HOME,
            completed = false
        ),
        Task(
            name = "Call mechanic",
            deadline = simpleDateFormat.parse("05-12-2024")!!,
            priority = TaskPriority.HIGH,
            tag = TaskTag.TRANSPORT,
            completed = false
        ),
        Task(
            name = "Grocery Shopping",
            deadline = simpleDateFormat.parse("05-12-2024")!!,
            priority = TaskPriority.MEDIUM,
            tag = TaskTag.HOME,
            completed = false
        ),
        Task(
            name = "Reorganize desk at work",
            deadline = simpleDateFormat.parse("05-12-2024")!!,
            priority = TaskPriority.LOW,
            tag = TaskTag.WORK,
            completed = false
        ),
        Task(
            name = "Clean bathroom",
            deadline = simpleDateFormat.parse("06-12-2024")!!,
            priority = TaskPriority.MEDIUM,
            tag = TaskTag.HOME,
            completed = false
        ),
        Task(
            name = "Get ready for album drop",
            deadline = simpleDateFormat.parse("07-12-2024")!!,
            priority = TaskPriority.LOW,
            tag = TaskTag.HOME,
            completed = false
        ),
        Task(
            name = "Homework - Math",
            deadline = simpleDateFormat.parse("07-01-2025")!!,
            priority = TaskPriority.HIGH,
            tag = TaskTag.SCHOOL,
            completed = false
        ),
        Task(
            name = "Find passport",
            deadline = simpleDateFormat.parse("10-01-2025")!!,
            priority = TaskPriority.MEDIUM,
            tag = TaskTag.HOME,
            completed = false
        ),
        Task(
            name = "Research christmas gifts",
            deadline = simpleDateFormat.parse("12-12-2024")!!,
            priority = TaskPriority.LOW,
            tag = TaskTag.HOME,
            completed = false
        )
    )


    // Might not be important with firestore...
    val todayTasks = listOf(
            Task(
                name = "Homework - UX",
                deadline = simpleDateFormat.parse("17-11-2024")!!,
                priority = TaskPriority.HIGH,
                tag = TaskTag.SCHOOL,
                completed = false
            ),
            Task(
                name = "Fix project at work",
                deadline = simpleDateFormat.parse("17-11-2024")!!,
                priority = TaskPriority.HIGH,
                tag = TaskTag.WORK,
                completed = false
            ),
            Task(
                name = "Walk the dog",
                deadline = simpleDateFormat.parse("17-11-2024")!!,
                priority = TaskPriority.MEDIUM,
                tag = TaskTag.PET,
                completed = false
            ),
            Task(
                name = "Cancel Netflix subscription",
                deadline = simpleDateFormat.parse("17-11-2024")!!,
                priority = TaskPriority.LOW,
                tag = TaskTag.HOME,
                completed = false
            )
    )



    val futureTasks = listOf(
            Task(
                name = "Call mechanic",
                deadline = simpleDateFormat.parse("18-11-2024")!!,
                priority = TaskPriority.HIGH,
                tag = TaskTag.TRANSPORT,
                completed = false
            ),
            Task(
                name = "Grocery Shopping",
                deadline = simpleDateFormat.parse("18-11-2024")!!,
                priority = TaskPriority.MEDIUM,
                tag = TaskTag.HOME,
                completed = false
            ),
            Task(
                name = "Reorganize desk at work",
                deadline = simpleDateFormat.parse("18-11-2024")!!,
                priority = TaskPriority.LOW,
                tag = TaskTag.WORK,
                completed = false
            ),
            Task(
                name = "Clean bathroom",
                deadline = simpleDateFormat.parse("19-11-2024")!!,
                priority = TaskPriority.MEDIUM,
                tag = TaskTag.HOME,
                completed = false
            ),
            Task(
                name = "Get ready for album drop",
                deadline = simpleDateFormat.parse("21-11-2024")!!,
                priority = TaskPriority.LOW,
                tag = TaskTag.HOME,
                completed = false
            ),
            Task(
                name = "Homework - Math",
                deadline = simpleDateFormat.parse("22-11-2024")!!,
                priority = TaskPriority.HIGH,
                tag = TaskTag.SCHOOL,
                completed = false
            ),
            Task(
                name = "Find passport",
                deadline = simpleDateFormat.parse("31-11-2024")!!,
                priority = TaskPriority.MEDIUM,
                tag = TaskTag.HOME,
                completed = false
            ),
            Task(
                name = "Research christmas gifts",
                deadline = simpleDateFormat.parse("12-12-2024")!!,
                priority = TaskPriority.LOW,
                tag = TaskTag.HOME,
                completed = false
            )
        )
}