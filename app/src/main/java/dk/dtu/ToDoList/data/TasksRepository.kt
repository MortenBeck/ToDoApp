package dk.dtu.ToDoList.data


import java.text.SimpleDateFormat
import java.util.Locale

object TasksRepository {

    val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)

    val Tasks = listOf(
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
        ),
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


    // In a real app, this would be coming from a data source like a database
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
            ))



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
    val favouriteTasks = listOf(
        Task(
            name = "Walk the dog",
            deadline = simpleDateFormat.parse("17-11-2024")!!,
            priority = TaskPriority.MEDIUM,
            tag = TaskTag.PET,
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
            name = "Research christmas gifts",
            deadline = simpleDateFormat.parse("12-12-2024")!!,
            priority = TaskPriority.LOW,
            tag = TaskTag.HOME,
            completed = false
        )
    )
}