package dk.dtu.ToDoList.presentation.view.components.task

import androidx.compose.runtime.Composable

@Composable
fun TaskNameField(
    name: String,
    onNameChange: (String) -> Unit
) {
    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text("Task Name") },
        modifier = Modifier.fillMaxWidth()
    )
}