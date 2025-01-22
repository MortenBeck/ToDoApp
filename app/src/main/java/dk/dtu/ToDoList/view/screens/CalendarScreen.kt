package dk.dtu.ToDoList.view.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import dk.dtu.ToDoList.R
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import dk.dtu.ToDoList.model.data.Task
import dk.dtu.ToDoList.view.components.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.lifecycle.lifecycleScope



/**
 * A composable screen that displays a calendar for the user to browse through tasks by date.
 * It also provides a floating action button for adding new tasks, and supports deleting
 * or updating existing tasks.
 *
 * @param tasks The list of [Task] objects to be displayed in the calendar and the list below it.
 * @param navController A [NavController] used for navigation (e.g., returning to previous screens).
 * @param onAddTask A callback to add a new [Task] to the data source.
 * @param onUpdateTask A callback to update an existing [Task].
 * @param onDeleteTask A callback to delete a task by its ID.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    tasks: List<Task>,
    navController: NavController,
    onAddTask: (Task) -> Unit,
    onUpdateTask: (Task) -> Unit,
    onDeleteTask: (String) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current // Add this line

    // States for date selection, dialogs and task actions
    var selectedDate by remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mutableStateOf(LocalDate.now())
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
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

            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                // Calender Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Calendar(
                        selectedDate = selectedDate,
                        currentMonth = currentMonth,
                        onDateSelected = { selectedDate = it },
                        onMonthChanged = { currentMonth = it },
                        tasks = tasks
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // List of tasks filtered for the selected date
                TasksForSelectedDate(
                    tasks = tasks,
                    selectedDate = selectedDate,
                    onDelete = { task ->
                        taskToDelete = task
                        showDeleteDialog = true
                    },
                    onCompleteToggle = { task ->
                        onUpdateTask(task.copy(completed = !task.completed))
                    },
                    onUpdateTask = onUpdateTask
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
            }
        )
    }

    // Dialog for deleting a task
    if (showDeleteDialog && taskToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                taskToDelete = null
                showDeleteDialog = false
            },
            title = { Text("Delete Task") },
            text = { Text("Are you sure you want to delete this task?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteTask(taskToDelete!!.id)
                        taskToDelete = null
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        taskToDelete = null
                        showDeleteDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}


/**
 * A private composable that displays tasks scheduled for a given [selectedDate]. If there are no tasks
 * on that date, a message is displayed.
 *
 * @param tasks The original (unfiltered) list of tasks.
 * @param selectedDate The currently chosen [LocalDate] in the calendar.
 * @param onDelete Callback invoked when a task is targeted for deletion.
 * @param onCompleteToggle Callback invoked when a task’s completion status is toggled.
 * @param onUpdateTask Callback invoked when a task is updated.
 * @param searchText An optional search string, if filtering by name is desired (defaults to empty).
 */
@Composable
private fun TasksForSelectedDate(
    tasks: List<Task>,
    selectedDate: LocalDate,
    onDelete: (Task) -> Unit,
    onCompleteToggle: (Task) -> Unit,
    onUpdateTask: (Task) -> Unit,
    searchText: String = ""
) {
    // User rememeber to avoid recomputing filtering on each recomposition when date or task change.
    val tasksForDate = remember(selectedDate, tasks) {
        tasks.filter { task ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                task.deadline.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() == selectedDate
            } else {
                TODO("VERSION.SDK_INT < O")
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors( containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Text(
                text = selectedDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(8.dp)
            )
        }

        if (tasksForDate.isNotEmpty()) {
            TaskList(
                Tasks = tasksForDate,
                modifier = Modifier.fillMaxWidth(),
                onDelete = onDelete,
                onCompleteToggle = onCompleteToggle,
                onUpdateTask = onUpdateTask,
                searchText = searchText,
                onDeleteRequest = onDelete
            )
        } else {
            Text(
                text = "No tasks scheduled for this day",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
