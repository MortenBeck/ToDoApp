package dk.dtu.ToDoList.view.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dk.dtu.ToDoList.model.data.Task
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.Icons
import androidx.navigation.NavController
import dk.dtu.ToDoList.view.components.AddTaskDialog
import dk.dtu.ToDoList.view.components.DeleteConfirmation
import dk.dtu.ToDoList.view.components.FilterSection
import dk.dtu.ToDoList.view.components.TopBar

@Composable
fun HomeScreen(tasks: MutableList<Task>, navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }
    var searchText by remember { mutableStateOf("") }

    // Create a mutable state for filtered tasks
    var filteredTasks by remember { mutableStateOf(tasks.toList()) }

    // Apply search filter
    val searchFilteredTasks = filteredTasks.filter {
        it.name.contains(searchText, ignoreCase = true)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            // Top Bar for search/profile
            TopBar(
                searchText = searchText,
                onSearchTextChange = { searchText = it },
                navController = navController
            )

            // Filter Section
            FilterSection(
                tasks = tasks,
                onFilterChange = { newFilteredTasks ->
                    filteredTasks = newFilteredTasks
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Display the list of tasks
            TaskListScreen(
                tasks = searchFilteredTasks.toMutableList(),
                onDelete = { task ->
                    taskToDelete = task
                },
                onFavoriteToggle = { taskToToggle ->
                    val index = tasks.indexOfFirst { it == taskToToggle }
                    if (index != -1) {
                        tasks[index] = tasks[index].copy(favorite = !tasks[index].favorite)
                        // Update filtered tasks to reflect changes
                        filteredTasks = tasks.toList()
                    }
                },
                onCompleteToggle = { taskToComplete ->
                    val index = tasks.indexOfFirst { it == taskToComplete }
                    if (index != -1) {
                        tasks[index] = tasks[index].copy(completed = !tasks[index].completed)
                        // Update filtered tasks to reflect changes
                        filteredTasks = tasks.toList()
                    }
                }
            )
        }

        // Floating Add Task Button
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            IconButton(
                onClick = { showDialog = true },
                modifier = Modifier.size(64.dp)
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
        }
    }

    // Dialogs
    if (showDialog) {
        AddTaskDialog(
            showDialog = showDialog,
            navController = navController,
            onDismiss = { showDialog = false },
            onTaskAdded = { newTask ->
                tasks.add(newTask)
                filteredTasks = tasks.toList() // Update filtered tasks
                showDialog = false
            }
        )
    }

    if (taskToDelete != null) {
        DeleteConfirmation(
            task = taskToDelete!!,
            onConfirm = {
                tasks.remove(taskToDelete)
                filteredTasks = tasks.toList() // Update filtered tasks
                taskToDelete = null
            },
            onDismiss = {
                taskToDelete = null
            }
        )
    }
}