package dk.dtu.ToDoList.view.screens

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dk.dtu.ToDoList.model.data.Task
import dk.dtu.ToDoList.view.components.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun CalendarScreen(
    tasks: List<Task>,
    navController: NavController,
    onAddTask: (Task) -> Unit,
    onUpdateTask: (Task) -> Unit,
    onDeleteTask: (String) -> Unit
) {
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
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Calendar(
            selectedDate = selectedDate,
            currentMonth = currentMonth,
            onDateSelected = { selectedDate = it },
            onMonthChanged = { currentMonth = it },
            tasks = tasks
        )

        Spacer(modifier = Modifier.height(16.dp))

        TasksForDate(
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

    if (showDeleteDialog && taskToDelete != null) {
        DeleteConfirmation(
            task = taskToDelete!!,
            onConfirm = {
                onDeleteTask(taskToDelete!!.id)
                taskToDelete = null
                showDeleteDialog = false
            },
            onDismiss = {
                taskToDelete = null
                showDeleteDialog = false
            }
        )
    }
}

@Composable
fun TasksForDate(
    tasks: List<Task>,
    selectedDate: LocalDate,
    onDelete: (Task) -> Unit,
    onCompleteToggle: (Task) -> Unit,
    onUpdateTask: (Task) -> Unit
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

    Column {
        Text(
            text = "Tasks for ${selectedDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (tasksForDate.isNotEmpty()) {
            TaskList(
                Tasks = tasksForDate,
                modifier = Modifier.fillMaxWidth(),
                onDelete = onDelete,
                onCompleteToggle = onCompleteToggle,
                onUpdateTask = onUpdateTask
            )
        } else {
            Text(
                text = "No tasks for this day.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}