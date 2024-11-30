package dk.dtu.ToDoList.feature

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dk.dtu.ToDoList.data.Task
import dk.dtu.ToDoList.data.TaskPriority
import dk.dtu.ToDoList.data.TaskTag
import dk.dtu.ToDoList.data.TasksRepository.simpleDateFormat
import androidx.compose.runtime.*
import dk.dtu.ToDoList.data.TasksRepository.Tasks


@Composable
fun FavouritesScreen(tasks: MutableList<Task>) {
    // State to hold the currently filtered favorite tasks
    var favouriteTasks by remember {
        mutableStateOf(tasks.filter { it.favorite })
    }

    var taskToDelete by remember { mutableStateOf<Task?>(null) }

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
            modifier = Modifier.weight(1f),
            onDelete = { task ->
                taskToDelete = task // Open confirmation dialog for this task
            }
        )
    }

    // Confirmation Dialog
    if (taskToDelete != null) {
        DeleteConfirmation(
            task = taskToDelete!!,
            onConfirm = {
                // Remove task from the original list
                tasks.remove(taskToDelete)
                // Re-filter the favourite tasks
                favouriteTasks = tasks.filter { it.favorite }
                taskToDelete = null // Close the dialog
            },
            onDismiss = {
                taskToDelete = null // Close the dialog
            }
        )
    }
}
