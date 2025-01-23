package dk.dtu.ToDoList.view.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import dk.dtu.ToDoList.model.data.Task

@Composable
fun DeleteTaskDialog(
    task: Task,
    onDismiss: () -> Unit,
    onConfirmDelete: (Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Task") },
        text = {
            Text(
                if (task.recurringGroupId != null)
                    "This task is part of a recurring series. Do you want to delete all instances or just this one?"
                else
                    "Are you sure you want to delete this task?"
            )
        },
        confirmButton = {
            if (task.recurringGroupId != null) {
                TextButton(onClick = { onConfirmDelete(true) }) {
                    Text("Delete All")
                }
            } else {
                TextButton(onClick = { onConfirmDelete(false) }) {
                    Text("Delete")
                }
            }
        },
        dismissButton = {
            if (task.recurringGroupId != null) {
                Row {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    TextButton(onClick = { onConfirmDelete(false) }) {
                        Text("Delete This Only")
                    }
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}