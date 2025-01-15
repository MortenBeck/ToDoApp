package dk.dtu.ToDoList.view.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dk.dtu.ToDoList.model.data.Task
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import dk.dtu.ToDoList.view.components.AddTaskDialog
import dk.dtu.ToDoList.view.components.DeleteConfirmation
import dk.dtu.ToDoList.view.components.TaskList


@Composable
fun FavouritesScreen(tasks: MutableList<Task>, navController: NavController) {
    // State to hold the currently filtered favorite tasks
    val favouriteTasks by remember {
        derivedStateOf { tasks.filter { it.favorite } }
    }


    var taskToDelete by remember { mutableStateOf<Task?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
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
                },
                onFavoriteToggle = { taskToToggle ->
                    val index = tasks.indexOfFirst { it == taskToToggle }
                    if (index != -1) {
                        tasks[index] = tasks[index].copy(favorite = !tasks[index].favorite)
                    }
                },
                onCompleteToggle = { taskToComplete ->
                    val index = tasks.indexOfFirst { it == taskToComplete }
                    if (index != -1) {
                        tasks[index] = tasks[index].copy(completed = !tasks[index].completed)
                    }
                }
            )
        }

        // Floating Add Task Button
        IconButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .padding(bottom = 16.dp, end = 16.dp) // Adjust padding for better placement
                .size(64.dp)
                .align(Alignment.BottomEnd) // Ensure alignment at bottom-right corner
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                shadowElevation = 6.dp
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Task",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .padding(12.dp)
                        .size(32.dp)
                )
            }
        }

        // Add Task Dialog
        if (showDialog) {
            AddTaskDialog(
                showDialog = showDialog,
                navController = navController,
                onDismiss = { showDialog = false },
                onTaskAdded = { newTask ->
                    tasks.add(newTask) // Add the new task to the list
                    showDialog = false
                }
            )
        }
    }

    // Confirmation Dialog
    if (taskToDelete != null) {
        DeleteConfirmation(
            task = taskToDelete!!,
            onConfirm = {
                // Remove task from the original list
                tasks.remove(taskToDelete)
                taskToDelete = null // Close the dialog
            },
            onDismiss = {
                taskToDelete = null // Close the dialog
            }
        )
    }
}
