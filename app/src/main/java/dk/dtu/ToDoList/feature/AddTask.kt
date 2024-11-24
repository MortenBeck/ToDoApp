package dk.dtu.ToDoList.feature

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dk.dtu.ToDoList.data.Task
import dk.dtu.ToDoList.data.TaskPriority
import dk.dtu.ToDoList.data.TaskTag
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import java.util.Calendar
import androidx.compose.material.icons.filled.DateRange



@Composable
fun AddTaskDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onTaskAdded: (Task) -> Unit
) {
    if (showDialog) {
        var taskName by remember { mutableStateOf("") }
        var priorityLevel by remember { mutableStateOf("Low") }
        var isFavorite by remember { mutableStateOf(false) }

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Add New Task",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Task Name
                    OutlinedTextField(
                        value = taskName,
                        onValueChange = { taskName = it },
                        label = { Text("Task Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    // Priority Level
                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        Text(
                            text = "Priority Level",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            PriorityButton("Low", priorityLevel) { priorityLevel = "Low" }
                            PriorityButton("Mid", priorityLevel) { priorityLevel = "Mid" }
                            PriorityButton("High", priorityLevel) { priorityLevel = "High" }
                        }
                    }

                    // Favorite and Add to Calendar Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Favorite Icon Button
                        IconButton(onClick = { isFavorite = !isFavorite }) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Add to Calendar Button
                        Button(
                            onClick = { /* TODO: Implement calendar functionality */ },
                            modifier = Modifier.height(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange, // Using DateRange icon
                                contentDescription = "Calendar",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add to Calendar")
                        }
                    }

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (taskName.isNotBlank()) {
                                    onTaskAdded(
                                        Task(
                                            name = taskName,
                                            priority = TaskPriority.valueOf(priorityLevel.uppercase()),
                                            isFavorite = isFavorite,
                                            deadline = today,
                                            tag = TaskTag.WORK,
                                            completed = false
                                        )
                                    )
                                }
                                onDismiss()
                            }
                        ) {
                            Text("Add Task")
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun PriorityButton(
    text: String,
    selectedPriority: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selectedPriority == text)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(
            text = text,
            color = if (selectedPriority == text)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onSurface
        )
    }
}


