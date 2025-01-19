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
    onUpdateTask: (Task) -> Unit
) {
    var taskName by remember { mutableStateOf(task.name) }
    var selectedPriority by remember { mutableStateOf(task.priority.name) }
    var selectedTag by remember { mutableStateOf(task.tag) }
    var isCompleted by remember { mutableStateOf(task.completed) }
    var deadline by remember { mutableStateOf(task.deadline.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

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
                    PriorityChip("Low", selectedPriority) { selectedPriority = "Low" }
                    PriorityChip("Medium", selectedPriority) { selectedPriority = "Medium" }
                    PriorityChip("High", selectedPriority) { selectedPriority = "High" }
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

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(deadline.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")))
                }

                if (showDatePicker) {
                    Dialog(onDismissRequest = { showDatePicker = false }) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Calendar(
                                selectedDate = deadline,
                                currentMonth = currentMonth,
                                onDateSelected = { date ->
                                    deadline = date
                                    showDatePicker = false
                                },
                                onMonthChanged = { month ->
                                    currentMonth = month
                                },
                                tasks = emptyList()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Completed",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Switch(
                        checked = isCompleted,
                        onCheckedChange = { isCompleted = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                            uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

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
                                priority = TaskPriority.valueOf(selectedPriority.uppercase()),
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