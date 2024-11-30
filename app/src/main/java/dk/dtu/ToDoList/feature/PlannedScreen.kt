package dk.dtu.ToDoList.feature

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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


@Composable
fun PlannedScreen(tasks: MutableList<Task>) { // MutableList to allow deletion
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }

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
            },
            onFavoriteToggle = { taskToToggle ->
                val index = tasks.indexOfFirst { it == taskToToggle }
                if (index != -1) {
                    tasks[index] = tasks[index].copy(favorite = !tasks[index].favorite)
                }
            }
        )
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
}

@Composable
fun TasksForDate(
    tasks: List<Task>,
    selectedDate: LocalDate,
    onDelete: (Task) -> Unit, // Add onDelete callback
    onFavoriteToggle: (Task) -> Unit
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
                onDelete = onDelete, // Pass the delete callback
                onFavoriteToggle = onFavoriteToggle
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