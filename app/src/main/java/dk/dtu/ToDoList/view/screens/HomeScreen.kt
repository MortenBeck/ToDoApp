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
import dk.dtu.ToDoList.view.screens.TaskListScreen
import dk.dtu.ToDoList.view.components.TaskList

@Composable
fun HomeScreen(
    tasks: List<Task>,
    onAddTask: (Task) -> Unit,
    onUpdateTask: (Task) -> Unit,
    onDeleteTask: (String) -> Unit,
    navController: NavController
) {
    var showDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }
    var searchText by remember { mutableStateOf("") }
    var filteredTasks by remember(tasks) { mutableStateOf(tasks) }

    // Search filter
    val searchFilteredTasks = filteredTasks.filter {
        it.name.contains(searchText, ignoreCase = true)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            // Top Bar for search
            TopBar(
                searchText = searchText,
                onSearchTextChange = { searchText = it },
                navController = navController
            )

            // Filter
            FilterSection(
                tasks = tasks,
                onFilterChange = { newFilteredTasks ->
                    filteredTasks = newFilteredTasks
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tasklist display
            TaskListScreen(
                tasks = searchFilteredTasks,
                onDelete = { task ->
                    taskToDelete = task
                },
                onCompleteToggle = { task ->
                    onUpdateTask(task.copy(completed = !task.completed))
                }
            )
        }

        // Add Task Button
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

    if (showDialog) {
        AddTaskDialog(
            showDialog = showDialog,
            navController = navController,
            onDismiss = { showDialog = false },
            onTaskAdded = { newTask ->
                onAddTask(newTask)
                showDialog = false
            }
        )
    }

    if (taskToDelete != null) {
        DeleteConfirmation(
            task = taskToDelete!!,
            onConfirm = {
                onDeleteTask(taskToDelete!!.id)
                taskToDelete = null
            },
            onDismiss = {
                taskToDelete = null
            }
        )
    }
}