package dk.dtu.ToDoList.feature

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dk.dtu.ToDoList.data.Task
import dk.dtu.ToDoList.data.TaskPriority
import dk.dtu.ToDoList.data.TaskTag
import java.util.Date

@Composable
fun HomeScreen(tasks: MutableList<Task>) {
    var searchText by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var taskName by remember { mutableStateOf("") }
    var priorityLevel by remember { mutableStateOf("Low") }
    var isFavorite by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(
                searchText = searchText,
                onSearchTextChange = { searchText = it },
                onProfileClick = {}
            )
            TaskListScreen(tasks)
        }

        // Add button
        IconButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 16.dp)
                .size(48.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colors.primary,
                elevation = 6.dp
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Task",
                    tint = MaterialTheme.colors.onPrimary,
                    modifier = Modifier
                        .padding(12.dp)
                        .size(24.dp)
                )
            }
        }

        // Add Task Dialog
        if (showDialog) {
            Dialog(onDismissRequest = { showDialog = false }) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colors.surface,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Add New Task",
                            style = MaterialTheme.typography.h6,
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
                                style = MaterialTheme.typography.subtitle1,
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

                        // Favorite and Calendar Row
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
                                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (isFavorite) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface
                                )
                            }

                            // Add to Calendar Button
                            Button(
                                onClick = { /* TODO: Implement calendar functionality */ }
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
                            TextButton(onClick = {
                                showDialog = false
                                taskName = ""
                                priorityLevel = "Low"
                                isFavorite = false
                            }) {
                                Text("Cancel")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    // Add Task Logic
                                    if (taskName.isNotBlank()) {
                                        tasks.add(
                                            Task(
                                                name = taskName,
                                                priority = TaskPriority.valueOf(priorityLevel.uppercase()), // Convert "Low"/"Mid"/"High" to enum
                                                isFavorite = isFavorite,
                                                deadline = Date(), // Set the current date as the deadline
                                                tag = TaskTag.WORK, // You can change this based on user input
                                                completed = false // Task is initially not completed
                                            )
                                        )
                                    }
                                    // Reset dialog fields
                                    showDialog = false
                                    taskName = ""
                                    priorityLevel = "Low"
                                    isFavorite = false
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
}

@Composable
private fun PriorityButton(
    text: String,
    selectedPriority: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (selectedPriority == text)
                MaterialTheme.colors.primary
            else
                MaterialTheme.colors.surface
        ),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(
            text = text,
            color = if (selectedPriority == text)
                MaterialTheme.colors.onPrimary
            else
                MaterialTheme.colors.onSurface
        )
    }
}

