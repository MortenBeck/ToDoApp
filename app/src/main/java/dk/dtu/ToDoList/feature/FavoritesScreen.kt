package dk.dtu.ToDoList.feature

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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dk.dtu.ToDoList.data.Task
import dk.dtu.ToDoList.data.TaskPriority
import dk.dtu.ToDoList.data.TaskTag
import dk.dtu.ToDoList.data.TasksRepository
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.navigation.NavController


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
                tasks = favouriteTasks,
                modifier = Modifier.weight(1f),
                onDelete = { task ->
                    taskToDelete = task // Open confirmation dialog for this task
                },
                onFavoriteToggle = { taskToToggle ->
                    taskToToggle.id?.let { taskId ->
                        TasksRepository.updateTask(
                            taskId = taskId,
                            updatedTask = taskToToggle.copy(favorite = !taskToToggle.favorite),
                            onSuccess = {
                                // Handle success
                            },
                            onFailure = { exception ->
                                // Handle failure
                            }
                        )
                    }
                },
                onCompleteToggle = { taskToComplete ->
                    taskToComplete.id?.let { taskId ->
                        TasksRepository.updateTask(
                            taskId = taskId,
                            updatedTask = taskToComplete.copy(completed = !taskToComplete.completed),
                            onSuccess = {
                                // Handle success
                            },
                            onFailure = { exception ->
                                // Handle failure
                            }
                        )
                    }
                }
            )
        }

        // Floating Add Task Button
        IconButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .padding(bottom = 16.dp, end = 16.dp)
                .size(64.dp)
                .align(Alignment.BottomEnd)
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
                    TasksRepository.addTask(newTask, onSuccess = {
                        tasks.add(newTask) // Add the new task to the list
                        showDialog = false
                    }, onFailure = {
                        println("Error adding task: ${it.message}")
                    })
                }
            )
        }
    }

    // Delete Confirmation Dialog
    if (taskToDelete != null && taskToDelete!!.id != null) {
        DeleteConfirmation(
            task = taskToDelete!!,
            onConfirm = {
                val taskId = taskToDelete!!.id
                if (taskId != null) {
                    TasksRepository.softDeleteTask(taskId, onSuccess = {
                        tasks.remove(taskToDelete) // Remove task from the local list
                        taskToDelete = null // Close the dialog
                    }, onFailure = { exception ->
                        println("Error deleting task: ${exception.message}")
                    })
                } else {
                    println("Task ID is null. Cannot delete task.")
                }
            },
            onDismiss = {
                taskToDelete = null // Close the dialog without deleting the task
            }
        )
    }
}

