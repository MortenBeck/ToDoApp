package dk.dtu.ToDoList.feature

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.DateRange // Substitute for CalendarToday
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dk.dtu.ToDoList.data.Task

@Composable
fun HomeScreen(tasks: List<Task>) {
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
                    modifier = Modifier.padding(12.dp).size(24.dp)
                )
            }
        }

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

                        OutlinedTextField(
                            value = taskName,
                            onValueChange = { taskName = it },
                            label = { Text("Task Name") },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                        )

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

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { isFavorite = !isFavorite }) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (isFavorite) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface
                                )
                            }

                            Button(onClick = { /* TODO: Calendar functionality */ }) {
                                Icon(
                                    imageVector = Icons.Default.DateRange, // Substitute icon
                                    contentDescription = "Calendar",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add to Calendar")
                            }
                        }

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
                            Button(onClick = {
                                // TODO: Add task logic
                                showDialog = false
                                taskName = ""
                                priorityLevel = "Low"
                                isFavorite = false
                            }) {
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
