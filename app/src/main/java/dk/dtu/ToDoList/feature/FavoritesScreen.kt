package dk.dtu.ToDoList.feature

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dk.dtu.ToDoList.data.Task
import dk.dtu.ToDoList.data.TaskPriority
import dk.dtu.ToDoList.data.TaskTag
import dk.dtu.ToDoList.data.TasksRepository.simpleDateFormat

@Composable
fun FavouritesScreen() {
    // List of favorite tasks
    val favouriteTasks = remember {
        listOf(
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Screen Title
        Text(
            text = "Favourites",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Favourites Task List
        TaskList(
            Tasks = favouriteTasks,
            modifier = Modifier.weight(1f)
        )
    }
}