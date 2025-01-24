package dk.dtu.ToDoList.view.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dk.dtu.ToDoList.R
import dk.dtu.ToDoList.model.data.task.Task
import dk.dtu.ToDoList.view.components.miscellaneous.FilterSection
import dk.dtu.ToDoList.view.components.miscellaneous.TopBar
import dk.dtu.ToDoList.view.components.task.AddTaskDialog
import dk.dtu.ToDoList.view.components.task.TaskListScreen
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
    val searchText by homeScreenViewModel.searchText.collectAsState()
    val showAddDialog by homeScreenViewModel.showAddDialog.collectAsState()
    val tasks by taskListViewModel.tasks.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val visibleTasks = tasks.filter {
        it.name.contains(searchText, ignoreCase = true)
    }

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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    FilterSection(
                        tasks = tasks,
                        taskListViewModel = taskListViewModel,
                        onFilterChange = { filtered ->
                            taskListViewModel.filterTasks(filtered)
                        }
                    )
                }

                if (visibleTasks.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (tasks.isEmpty()) {
                                "No tasks match the selected filter criteria."
                            } else {
                                "No tasks available. Please add a task by clicking the '+' button to get started."
                            },
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                } else {
                    TaskListScreen(
                        viewModel = taskListViewModel,
                        onCompleteToggle = { task ->
                            val updatedTask = task.copy(completed = !task.completed)
                            onUpdateTask(updatedTask)
                            taskListViewModel.updateTask(updatedTask)
                        },
                        onUpdateTask = onUpdateTask,
                        searchText = searchText
                    )
                }
            }
        }
    }

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