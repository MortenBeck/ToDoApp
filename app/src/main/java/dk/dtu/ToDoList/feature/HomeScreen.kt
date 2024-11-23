package dk.dtu.ToDoList.feature

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dk.dtu.ToDoList.data.Task
import dk.dtu.ToDoList.data.TaskPriority
import dk.dtu.ToDoList.data.TaskTag
import dk.dtu.ToDoList.data.TasksRepository.simpleDateFormat

@Composable
fun TaskListScreen() {
    // Define today's tasks
    var searchText by remember { mutableStateOf("") }
    val todayTasks = remember {
        listOf(
            Task(
                name = "Homework - UX",
                deadline = simpleDateFormat.parse("17-11-2024")!!,
                priority = TaskPriority.HIGH,
                tag = TaskTag.SCHOOL,
                completed = false
            ),
            Task(
                name = "Fix project at work",
                deadline = simpleDateFormat.parse("18-11-2024")!!,
                priority = TaskPriority.MEDIUM,
                tag = TaskTag.WORK,
                completed = true
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
    }


    // Define future tasks
    val futureTasks = remember {
        listOf(
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopBar(
            searchText = searchText,
            onSearchTextChange = { searchText = it },
            onProfileClick = {}
        )
        // App Title
        Text(
            text = "To-Do List",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Today's Tasks Section
        Text(
            text = "Today",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TaskList(
            Tasks = todayTasks,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.padding(vertical = 12.dp))

        // Future Tasks Section
        Text(
            text = "Future",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TaskList(
            Tasks = futureTasks,
            modifier = Modifier.weight(1f)
        )
    }
}