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
            Image(
                painter = painterResource(id = R.drawable.background_gradient),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
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

    // Dialogs
    if (showAddDialog) {
        AddTaskDialog(
            showDialog = showAddDialog,
            navController = navController,
            onDismiss = { showAddDialog = false },
            onTaskAdded = { newTask ->
                onAddTask(newTask)
                showAddDialog = false
            },
            lifecycleScope = lifecycleOwner.lifecycleScope // Add this line
        )
    }

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

@Composable
private fun TasksForSelectedDate(
    tasks: List<Task>,
    selectedDate: LocalDate,
    onDelete: (Task) -> Unit,
    onCompleteToggle: (Task) -> Unit,
    onUpdateTask: (Task) -> Unit,
    searchText: String = ""
) {
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
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background // Use default background color
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Text(
                text = selectedDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface, // Use default onSurface color
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
                color = MaterialTheme.colorScheme.onSurface // Use default onSurface color
            )
        }
    }
}
