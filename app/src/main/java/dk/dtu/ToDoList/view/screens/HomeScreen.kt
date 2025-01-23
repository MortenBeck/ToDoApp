package dk.dtu.ToDoList.view.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dk.dtu.ToDoList.R
import dk.dtu.ToDoList.model.data.Task
import dk.dtu.ToDoList.view.components.*
import dk.dtu.ToDoList.viewmodel.HomeScreenViewModel
import dk.dtu.ToDoList.viewmodel.TaskListViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    taskListViewModel: TaskListViewModel,
    homeScreenViewModel: HomeScreenViewModel,
    navController: NavController,
    onAddTask: (Task) -> Unit,
    onUpdateTask: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onDeleteRecurringGroup: (Task) -> Unit
) {
    // Observe UI-related states
    val searchText by homeScreenViewModel.searchText.collectAsState()
    val showAddDialog by homeScreenViewModel.showAddDialog.collectAsState()

    // Observe tasks from TaskListViewModel
    val categorizedTasks by taskListViewModel.categorizedTasks.collectAsState()

    // Filter tasks based on searchText
    val filteredTasks = categorizedTasks.values.flatten().filter {
        it.name.contains(searchText, ignoreCase = true)
    }

    // Remember coroutine scope for launching suspend functions
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopBar(
                searchText = searchText,
                onSearchTextChange = { homeScreenViewModel.updateSearchText(it) },
                navController = navController
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { homeScreenViewModel.toggleAddDialog(true) },
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
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background image
            Image(
                painter = painterResource(id = R.drawable.background_gradient),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding(), bottom = 22.dp)
            ) {
                // Filter section card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    FilterSection(
                        tasks = filteredTasks,
                        onFilterChange = { filtered -> taskListViewModel.setTasks(filtered) }
                    )
                }

                // Displays the filtered and/or searched tasks
                TaskListScreen(
                    viewModel = taskListViewModel,
                    onCompleteToggle = { task ->
                        val updatedTask = task.copy(completed = !task.completed)
                        onUpdateTask(updatedTask)
                        taskListViewModel.setTasks(taskListViewModel.tasks.value.map {
                            if (it.id == task.id) updatedTask else it
                        })
                    },
                    onUpdateTask = onUpdateTask,
                    searchText = searchText
                )
            }
        }
    }

    // Dialog for adding a new task
    if (showAddDialog) {
        AddTaskDialog(
            showDialog = showAddDialog,
            navController = navController,
            onDismiss = { homeScreenViewModel.toggleAddDialog(false) },
            onTaskAdded = { newTask, isRecurring ->
                coroutineScope.launch {
                    if (isRecurring) {
                        taskListViewModel.addTaskWithRecurrence(newTask)
                    } else {
                        taskListViewModel.addTask(newTask)
                    }
                    homeScreenViewModel.toggleAddDialog(false)
                }
            }
        )
    }
}