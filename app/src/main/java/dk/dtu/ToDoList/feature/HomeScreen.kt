package dk.dtu.ToDoList.feature

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dk.dtu.ToDoList.data.Task
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.Icons
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(tasks: MutableList<Task>, navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) } // State to manage the delete confirmation dialog

    var searchText by remember { mutableStateOf("")}

    val filteredTasks = tasks.filter {
        it.name.contains(searchText, ignoreCase = true)}.toMutableList()

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            // Top Bar for search/profile
            TopBar(searchText = searchText, onSearchTextChange = {searchText=it}, onProfileClick = {})

            // Display the list of tasks
            TaskListScreen(
                tasks = filteredTasks,
                onDelete = { task ->
                    taskToDelete = task // Trigger the delete confirmation dialog
                },
                onFavoriteToggle = { taskToToggle ->
                    // Toggle the favorite status of the task
                    val index = tasks.indexOfFirst { it == taskToToggle }
                    if (index != -1) {
                        tasks[index] = tasks[index].copy(favorite = !tasks[index].favorite)
                    }
                }
            )
        }

        // Floating Add Task Button
        IconButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 16.dp)
                .size(48.dp)
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
                        .size(24.dp)
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

    // Delete Confirmation Dialog
    if (taskToDelete != null) {
        DeleteConfirmation(
            task = taskToDelete!!,
            onConfirm = {
                tasks.remove(taskToDelete) // Remove the task from the list
                taskToDelete = null // Close the dialog
            },
            onDismiss = {
                taskToDelete = null // Close the dialog without deleting
            }
        )
    }
}
