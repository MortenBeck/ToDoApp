package dk.dtu.ToDoList.domain.components.task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import dk.dtu.ToDoList.domain.model.Task
import dk.dtu.ToDoList.domain.components.miscellaneous.Calendar

@Composable
fun DeadlinePicker(
    deadline: LocalDate,
    onDeadlineChange: (LocalDate) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    Text("Deadline", style = MaterialTheme.typography.titleMedium)
    Spacer(modifier = Modifier.height(8.dp))

    OutlinedButton(
        onClick = { showDatePicker = true },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(deadline.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")))
    }

    if (showDatePicker) {
        Dialog(onDismissRequest = { showDatePicker = false }) {
            Calendar(
                selectedDate = deadline,
                currentMonth = currentMonth,
                onDateSelected = {
                    onDeadlineChange(it)
                    showDatePicker = false
                },
                onMonthChanged = { currentMonth = it },
                tasks = emptyList()
            )
        }
    }
}

@Composable
fun DeleteButton(onDelete: () -> Unit) {
    OutlinedButton(
        onClick = onDelete,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.error
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Delete Task")
    }
}

@Composable
fun ActionButtons(
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.weight(1f)
        ) {
            Text("Cancel")
        }
        Button(
            onClick = onSave,
            modifier = Modifier.weight(1f)
        ) {
            Text("Save")
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    taskToDelete: Task?,
    onDismiss: () -> Unit,
    onConfirm: (Boolean) -> Unit
) {
    taskToDelete?.let { task ->
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Delete Task") },
            text = { Text("Are you sure you want to delete '${task.name}'?") },
            confirmButton = {
                TextButton(onClick = { onConfirm(false) }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}