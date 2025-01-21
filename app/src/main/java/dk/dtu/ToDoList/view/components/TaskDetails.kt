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
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

@Composable
fun TaskDetails(
    task: Task,
    onDismiss: () -> Unit,
    onUpdateTask: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onDeleteRecurringGroup: (String) -> Unit
) {
    // Initialize state variables based on the task's properties
    var taskName by remember { mutableStateOf(task.name) }
    var priorityLevel by remember { mutableStateOf(task.priority.name.lowercase().replaceFirstChar { it.uppercase() }) }
    var selectedTag by remember { mutableStateOf(task.tag) }
    var isCompleted by remember { mutableStateOf(task.completed) }
    var deadline by remember {
        mutableStateOf(task.deadline.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
    }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Recurring Task") },
            text = {
                Text(
                    if (task.isRecurringParent) {
                        "This is the first task in a recurring series. Would you like to delete just this instance or all recurring instances of this task?"
                    } else {
                        "This is part of a recurring series. Would you like to delete just this instance or all recurring instances of this task?"
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        task.recurringGroupId?.let { groupId ->
                            onDeleteRecurringGroup(groupId)
                        }
                        onDismiss()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete All")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDeleteTask(task)
                        onDismiss()
                    }
                ) {
                    Text("Delete This Only")
                }
            }
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Edit Task",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = taskName,
                    onValueChange = { taskName = it },
                    label = { Text("Task Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Priority",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PriorityChip(
                        text = "Low",
                        selectedPriority = priorityLevel,
                        onClick = { priorityLevel = "Low" },
                    )
                    PriorityChip(
                        text = "Medium",
                        selectedPriority = priorityLevel,
                        onClick = { priorityLevel = "Medium" },
                    )
                    PriorityChip(
                        text = "High",
                        selectedPriority = priorityLevel,
                        onClick = { priorityLevel = "High" },
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Category",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                ModernDropdownTagSelector(
                    selectedTag = selectedTag,
                    onTagSelected = { selectedTag = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Deadline",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedButton(
                    onClick = {
                        if (task.recurringGroupId != null) {
                            showDeleteDialog = true
                        } else {
                            onDeleteTask(task)
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Delete Task")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            val updatedTask = task.copy(
                                name = taskName,
                                priority = TaskPriority.valueOf(priorityLevel.uppercase()),
                                tag = selectedTag,
                                completed = isCompleted,
                                deadline = Date.from(deadline.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                                modifiedAt = Date()
                            )
                            onUpdateTask(updatedTask)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteRecurringTaskDialog(
    task: Task,
    onDismiss: () -> Unit,
    onDeleteSingle: () -> Unit,
    onDeleteGroup: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Recurring Task") },
        text = {
            Text(
                if (task.isRecurringParent) {
                    "This is the first task in a recurring series. Would you like to delete just this instance or all recurring instances of this task?"
                } else {
                    "This is part of a recurring series. Would you like to delete just this instance or all recurring instances of this task?"
                }
            )
        },
        confirmButton = {
            TextButton(
                onClick = onDeleteGroup,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete All")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDeleteSingle
            ) {
                Text("Delete This Only")
            }
        }
    )
}