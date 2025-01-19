package dk.dtu.ToDoList.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dk.dtu.ToDoList.model.data.Task
import dk.dtu.ToDoList.model.data.TaskPriority
import java.util.Date
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Switch
import androidx.compose.ui.graphics.Color
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@Composable
fun TaskDetails(
    task: Task,
    onDismiss: () -> Unit,
    onUpdateTask: (Task) -> Unit
) {
    // State for editable fields
    var taskName by remember { mutableStateOf(task.name) }
    var selectedPriority by remember { mutableStateOf(task.priority.name) }
    var selectedTag by remember { mutableStateOf(task.tag) }
    var isCompleted by remember { mutableStateOf(task.completed) }
    var deadline by remember { mutableStateOf(task.deadline.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Edit Task",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Task Name Input
                OutlinedTextField(
                    value = taskName,
                    onValueChange = { taskName = it },
                    label = { Text("Task Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Priority Selector
                Text("Priority", style = MaterialTheme.typography.bodyMedium)
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

                // Tag Selector
                ModernDropdownTagSelector(
                    selectedTag = selectedTag,
                    onTagSelected = { selectedTag = it },
                    color = Color(0xFFE2EFF5)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Deadline Selector
                Text(
                    text = "Deadline",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(deadline.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")))
                }

                if (showDatePicker) {
                    Dialog(onDismissRequest = { showDatePicker = false }) {
                        Surface(
                            shape = MaterialTheme.shapes.large,
                            color = MaterialTheme.colorScheme.surface,
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

                // Completion Status Toggle
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Completed", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.width(16.dp))
                    Switch(
                        checked = isCompleted,
                        onCheckedChange = { isCompleted = it }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
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
                                modifiedAt = Date() // Save the timestamp
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

