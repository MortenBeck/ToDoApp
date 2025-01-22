package dk.dtu.ToDoList.view.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.ui.res.painterResource
import dk.dtu.ToDoList.view.components.AddTaskDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import dk.dtu.ToDoList.R
import dk.dtu.ToDoList.model.data.Task
import dk.dtu.ToDoList.view.components.*
import androidx.compose.foundation.Image



/**
 * The main "Home" screen of the app. This screen displays a list of tasks with optional
 * filtering and searching, and also provides a floating action button for adding new tasks.
 *
 * @param tasks A list of [Task] items to display and manage on this screen.
 * @param onAddTask A callback to handle the addition of a new [Task].
 * @param onUpdateTask A callback invoked when an existing [Task] has been modified.
 * @param onDeleteTask A callback invoked when a single (non-recurring) task is deleted
 * (identified by its [Task.id]).
 * @param onDeleteRecurringGroup A callback invoked when a recurring task group should be deleted
 * (identified by its [Task.recurringGroupId]).
 * @param navController A [NavController] used for navigation actions (e.g., opening a details view).
 */
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
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    var showAddDialog by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var filteredTasks by remember(tasks) { mutableStateOf(tasks) }

    // Further filter tasks by search text
    val searchFilteredTasks = filteredTasks.filter {
        it.name.contains(searchText, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopBar(
                // The top bar includes a search icon that toggles a search field
                searchText = searchText,
                onSearchTextChange = { searchText = it },
                navController = navController
            )
        },
        floatingActionButton = {
            // A FAB for adding a new task
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
        Box(
            modifier = Modifier
                .fillMaxSize()
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
                        tasks = tasks,
                        onFilterChange = { newFilteredTasks ->
                            filteredTasks = newFilteredTasks
                        }
                    )
                }

                // Displays the filtered and/or searched tasks
                TaskListScreen(
                    tasks = searchFilteredTasks,
                    onDelete = { task ->
                        // If the task has a recurring group I; delete the group, otherwise, delete a single task
                        if (task.recurringGroupId != null) {
                            onDeleteRecurringGroup(task.recurringGroupId)
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
    }

    // Dialog for adding a new task
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
}