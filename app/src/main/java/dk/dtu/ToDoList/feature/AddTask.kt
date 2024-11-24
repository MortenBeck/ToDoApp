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
import androidx.compose.material.icons.filled.DateRange
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.navigation.NavController

@Composable
fun AddTaskDialog(
    showDialog: Boolean,
    navController: NavController,
    onDismiss: () -> Unit,
    onTaskAdded: (Task) -> Unit
) {
    if (showDialog) {
        var taskName by remember { mutableStateOf("") }
        var priorityLevel by remember { mutableStateOf("Low") }
        var isFavorite by remember { mutableStateOf(false) }
        var selectedDate by remember { mutableStateOf<Date?>(null) }

        val context = LocalContext.current // Use context for Toast messages

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
                    // Task Name Input
                    Text(
                        text = "Add New Task",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    OutlinedTextField(
                        value = taskName,
                        onValueChange = { taskName = it },
                        label = { Text("Task Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    // Priority Selection
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

                    // Date Selection and Favorite Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Favorite Button
                        IconButton(onClick = { isFavorite = !isFavorite }) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Navigate to Add to Calendar Page
                        Button(
                            onClick = {
                                if (taskName.isNotBlank()) {
                                    navController.navigate("AddToCalendar/$taskName") // Pass the taskName to the next screen
                                }
                            },
                            modifier = Modifier.height(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Calendar",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add to Calendar")
                        }
                    }

                    // Display Selected Date
                    selectedDate?.let {
                        Text(
                            text = "Selected Date: ${
                                SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(it)
                            }",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
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
                                if (taskName.isNotBlank() && selectedDate != null) {
                                    onTaskAdded(
                                        Task(
                                            name = taskName,
                                            priority = TaskPriority.valueOf(priorityLevel.uppercase()),
                                            isFavorite = isFavorite,
                                            deadline = selectedDate!!,
                                            tag = TaskTag.WORK,
                                            completed = false
                                        )
                                    )
                                    onDismiss()
                                } else {
                                    Toast.makeText(context, "Please pick a date", Toast.LENGTH_SHORT).show()
                                }
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
