package dk.dtu.ToDoList.feature

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dk.dtu.ToDoList.data.Task
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import androidx.compose.material3.*


@Composable
fun PlannedScreen(tasks: MutableList<Task>, navController: NavController) { // MutableList to allow deletion
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }
    var showAddTaskDialog by remember { mutableStateOf(false) } // State to show/hide AddTaskDialog

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Title
            Text(
                text = "Planned",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Calendar
            Calendar(
                selectedDate = selectedDate,
                currentMonth = currentMonth,
                onDateSelected = { selectedDate = it },
                onMonthChanged = { currentMonth = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Filtered Tasks for Selected Date
            TasksForDate(
                tasks = tasks,
                selectedDate = selectedDate,
                onDelete = { task ->
                    taskToDelete = task
                    showDeleteDialog = true
                }
            )
        }

        // Floating Add Task Button - Positioned at Bottom-Right Corner
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd // Align to bottom-end
        ) {
            IconButton(
                onClick = { showAddTaskDialog = true },
                modifier = Modifier.size(64.dp) // Adjust size as needed
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
                            .size(32.dp) // Icon size
                    )
                }
            }
        }
    }

    // Show delete confirmation dialog
    if (showDeleteDialog && taskToDelete != null) {
        DeleteConfirmation(
            task = taskToDelete!!,
            onConfirm = {
                tasks.remove(taskToDelete) // Remove the task from the list
                taskToDelete = null
                showDeleteDialog = false
            },
            onDismiss = {
                taskToDelete = null
                showDeleteDialog = false
            }
        )
    }

    // Add Task Dialog
    if (showAddTaskDialog) {
        AddTaskDialog(
            showDialog = showAddTaskDialog,
            navController = navController,
            onDismiss = { showAddTaskDialog = false },
            onTaskAdded = { newTask ->
                tasks.add(newTask) // Add the new task to the list
                showAddTaskDialog = false
            }
        )
    }
}

@Composable
fun TasksForDate(
    tasks: List<Task>,
    selectedDate: LocalDate,
    onDelete: (Task) -> Unit // Add onDelete callback
) {
    val tasksForDate = remember(selectedDate, tasks) {
        tasks.filter { task ->
            task.deadline.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() == selectedDate
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
                onDelete = onDelete // Pass the delete callback
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
