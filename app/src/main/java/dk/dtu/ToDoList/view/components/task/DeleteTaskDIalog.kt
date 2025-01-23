package dk.dtu.ToDoList.view.components.task

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dk.dtu.ToDoList.model.data.task.Task

@Composable
fun DeleteTaskDialog(
    task: Task,
    onDismiss: () -> Unit,
    onConfirmDelete: (Boolean) -> Unit
) {
    AlertDialog(
        modifier = Modifier.widthIn(min = 400.dp),
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
            Row {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                if (task.recurringGroupId != null) {
                    TextButton(onClick = { onConfirmDelete(true) }) {
                        Text("Delete All")
                    }
                    TextButton(onClick = { onConfirmDelete(false) }) {
                        Text("Delete This Only")
                    }
                } else {
                    TextButton(onClick = { onConfirmDelete(false) }) {
                        Text("Delete")
                    }
                }
            }
        },
        dismissButton = null
    )
}