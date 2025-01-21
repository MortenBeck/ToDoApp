package dk.dtu.ToDoList.view.screens

import dk.dtu.ToDoList.view.components.AddTaskDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import dk.dtu.ToDoList.model.data.Task
import dk.dtu.ToDoList.view.components.*

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    tasks: List<Task>,
    onAddTask: (Task) -> Unit,
    onUpdateTask: (Task) -> Unit,
    onDeleteTask: (String) -> Unit,
    onDeleteRecurringGroup: (String) -> Unit,
    navController: NavController
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var showAddDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }
    var searchText by remember { mutableStateOf("") }
    var filteredTasks by remember(tasks) { mutableStateOf(tasks) }

    val searchFilteredTasks = filteredTasks.filter {
        it.name.contains(searchText, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopBar(
                searchText = searchText,
                onSearchTextChange = { searchText = it },
                navController = navController
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Task",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                FilterSection(
                    tasks = tasks,
                    onFilterChange = { newFilteredTasks ->
                        filteredTasks = newFilteredTasks
                    }
                )
            }

            TaskListScreen(
                tasks = searchFilteredTasks,
                onDelete = { task ->
                    if (task.recurringGroupId != null) {
                        taskToDelete = task
                    } else {
                        onDeleteTask(task.id)
                    }
                },
                onCompleteToggle = { task ->
                    onUpdateTask(task.copy(completed = !task.completed))
                },
                onUpdateTask = onUpdateTask,
                searchText = searchText
            )
        }
    }

    if (showAddDialog) {
        AddTaskDialog(
            showDialog = showAddDialog,
            navController = navController,
            onDismiss = { showAddDialog = false },
            onTaskAdded = { newTask ->
                onAddTask(newTask)
                showAddDialog = false
            },
            lifecycleScope = lifecycleOwner.lifecycleScope
        )
    }

    if (taskToDelete != null) {
        if (taskToDelete!!.recurringGroupId != null) {
            AlertDialog(
                onDismissRequest = { taskToDelete = null },
                title = { Text("Delete Recurring Task") },
                text = {
                    Text(
                        if (taskToDelete!!.isRecurringParent) {
                            "Do you want to delete all instances of '${taskToDelete!!.name}' or just this one?"
                        } else {
                            "This task is part of a recurring series. Do you want to delete all instances or just this one?"
                        }
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            taskToDelete!!.recurringGroupId?.let { groupId ->
                                onDeleteRecurringGroup(groupId)
                            }
                            taskToDelete = null
                        }
                    ) {
                        Text("Delete All")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            onDeleteTask(taskToDelete!!.id)
                            taskToDelete = null
                        }
                    ) {
                        Text("Delete This Only")
                    }
                }
            )
        } else {
            AlertDialog(
                onDismissRequest = { taskToDelete = null },
                title = { Text("Delete Task") },
                text = { Text("Are you sure you want to delete '${taskToDelete!!.name}'?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onDeleteTask(taskToDelete!!.id)
                            taskToDelete = null
                        }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { taskToDelete = null }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}