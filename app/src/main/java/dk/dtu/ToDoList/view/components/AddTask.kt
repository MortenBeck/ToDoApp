package dk.dtu.ToDoList.view.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dk.dtu.ToDoList.model.data.Task
import dk.dtu.ToDoList.model.data.TaskPriority
import dk.dtu.ToDoList.model.data.TaskTag
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import java.util.Date
import androidx.navigation.NavController
import dk.dtu.ToDoList.model.data.RecurrencePattern
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


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
        var selectedTag by remember { mutableStateOf(TaskTag.WORK) }
        var selectedRecurrence by remember { mutableStateOf<RecurrencePattern?>(null) }
        var showError by remember { mutableStateOf(false) }

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
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Task Name Input
                    OutlinedTextField(
                        value = taskName,
                        onValueChange = {
                            taskName = it
                            showError = it.isBlank()
                        },
                        label = { Text("Task Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        isError = showError
                    )
                    if (showError) {
                        Text(
                            text = "Task name cannot be empty.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // Priority Selector
                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        Text(
                            text = "Priority Level",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            PriorityButton("Low", priorityLevel) { priorityLevel = "Low" }
                            PriorityButton("Medium", priorityLevel) { priorityLevel = "Medium" }
                            PriorityButton("High", priorityLevel) { priorityLevel = "High" }
                        }
                    }

                    // Tag Selector
                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        Text(
                            text = "Task Tag",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        DropdownTagSelector(
                            selectedTag = selectedTag,
                            onTagSelected = { selectedTag = it }
                        )
                    }

                    // Recurrence Selector
                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        Text(
                            text = "Repeat",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        DropdownRecurrenceSelector(
                            selectedRecurrence = selectedRecurrence,
                            onRecurrenceSelected = { selectedRecurrence = it }
                        )
                    }

                    // Favorite Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { isFavorite = !isFavorite }) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Toggle Favorite",
                                tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Button(
                            onClick = {
                                navController.navigate("addToCalendar?taskName=${taskName}&priorityLevel=$priorityLevel")
                            }
                        ) {
                            Text("Add to Calendar")
                        }
                    }

                    // Cancel & Add Task Buttons
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
                                    val newTask = Task(
                                        name = taskName,
                                        priority = TaskPriority.valueOf(priorityLevel.uppercase()),
                                        favorite = isFavorite,
                                        deadline = Date(),
                                        tag = selectedTag,
                                        completed = false,
                                        recurrence = selectedRecurrence
                                    )

                                    addTaskToFirebase(
                                        task = newTask,
                                        onSuccess = {
                                            onDismiss()
                                        },
                                        onFailure = { exception ->
                                            // Handle Firebase error (e.g., show a Snackbar)
                                            println("Failed to add task: ${exception.message}")
                                        }
                                    )
                                } else {
                                    showError = true
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

fun addTaskToFirebase(
    task: Task,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    // Get Firestore instance
    val db = Firebase.firestore

    // Reference to the tasks collection (replace "tasks" with your collection name)
    val tasksCollection = db.collection("tasks")

    // Add the task to Firestore
    tasksCollection.add(task)
        .addOnSuccessListener {
            onSuccess() // Notify the caller that the task was added successfully
        }
        .addOnFailureListener { exception ->
            onFailure(exception) // Pass the exception to the caller for error handling
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
        modifier = Modifier
            .wrapContentWidth()
            .padding(horizontal = 2.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        Text(
            text = text,
            color = if (selectedPriority == text)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun DropdownTagSelector(
    selectedTag: TaskTag,
    onTagSelected: (TaskTag) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = selectedTag.name)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            TaskTag.entries.forEach { tag -> // Ensure this iterates over all TaskTag values
                DropdownMenuItem(
                    onClick = {
                        onTagSelected(tag) // Correctly updates the tag
                        expanded = false
                    },
                    text = { Text(text = tag.name) }
                )
            }
        }
    }
}

@Composable
fun DropdownRecurrenceSelector(
    selectedRecurrence: RecurrencePattern?,
    onRecurrenceSelected: (RecurrencePattern?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = selectedRecurrence?.name ?: "Don't Repeat")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                onClick = {
                    onRecurrenceSelected(null)
                    expanded = false
                },
                text = { Text("Don't Repeat") }
            )
            RecurrencePattern.values().forEach { pattern ->
                DropdownMenuItem(
                    onClick = {
                        onRecurrenceSelected(pattern)
                        expanded = false
                    },
                    text = { Text(pattern.name) }
                )
            }
        }
    }
}