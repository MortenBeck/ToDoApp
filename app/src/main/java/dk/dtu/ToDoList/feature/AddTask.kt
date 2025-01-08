package dk.dtu.ToDoList.feature

import android.util.Log
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
import java.util.Date
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AddTaskDialog(
    showDialog: Boolean,
    navController: NavController,
    onDismiss: () -> Unit,
    onTaskAdded: (Task) -> Unit // Allows task addition
) {
    if (showDialog) {
        var taskName by remember { mutableStateOf("") }
        var priorityLevel by remember { mutableStateOf("Low") } // Default priority
        var isFavorite by remember { mutableStateOf(false) }
        var description by remember { mutableStateOf("") }
        var selectedTag by remember { mutableStateOf(TaskTag.WORK) } // Default tag

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
                        onValueChange = { taskName = it },
                        label = { Text("Task Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    // Description Input
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    // Priority Selector
                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        Text(
                            text = "Priority Level",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            PriorityButton("Low", priorityLevel) { priorityLevel = "Low" }
                            PriorityButton("Mid", priorityLevel) { priorityLevel = "Mid" }
                            PriorityButton("High", priorityLevel) { priorityLevel = "High" }
                        }
                    }

                    // Tag Selector (could be expanded in future)
                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        Text(
                            text = "Tag",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Add tag selection buttons or dropdown here if needed
                            Button(onClick = { selectedTag = TaskTag.WORK }) { Text("Work") }
                            Button(onClick = { selectedTag = TaskTag.SCHOOL }) { Text("School") }
                            Button(onClick = { selectedTag = TaskTag.SPORT }) { Text("Sport") }
                        }
                    }

                    // Favorite Toggle & Add to Calendar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Favorite Toggle
                        IconButton(onClick = { isFavorite = !isFavorite }) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Toggle Favorite",
                                tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Add to Calendar
                        Button(
                            onClick = {
                                navController.navigate("addToCalendar?taskName=$taskName&priorityLevel=$priorityLevel")
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
                                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "testUser123"
                                    val newTask = Task(
                                        id = "", // Firestore will auto-generate the ID
                                        name = taskName,
                                        description = description,
                                        priority = TaskPriority.valueOf(priorityLevel.uppercase()),
                                        tag = selectedTag,
                                        completed = false,
                                        favorite = isFavorite,
                                        userId = userId, // Dynamically set userId
                                        deadline = Date() // Default to current date if no calendar selected
                                    )

                                    // Add the task to Firestore
                                    addTaskToFirestore(newTask) { taskId ->
                                        // Task added successfully
                                        onTaskAdded(newTask) // Notify parent component
                                        onDismiss() // Close the dialog
                                    }
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

// Function to add the task to Firestore
private fun addTaskToFirestore(task: Task, onSuccess: (String) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.collection("tasks")
        .add(task)  // Firestore will auto-generate an ID for the task
        .addOnSuccessListener { documentReference ->
            onSuccess(documentReference.id) // Return taskId after successful addition
        }
        .addOnFailureListener { exception ->
            Log.e("AddTaskDialog", "Error adding task to Firestore: ${exception.message}")
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
