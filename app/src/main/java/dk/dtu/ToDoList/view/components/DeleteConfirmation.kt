package dk.dtu.ToDoList.view.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import dk.dtu.ToDoList.model.data.Task

@Composable
fun DeleteConfirmation(
    task: Task,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(

        onDismissRequest = onDismiss,
        title = {
            Text(text = "Delete Task")
        },
        text = {
            Text(text = "Are you sure you want to delete the task \"${task.name}\"?")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Yes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "No")
            }
        }
    )
}