package dk.dtu.ToDoList.view.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import dk.dtu.ToDoList.model.data.Task
import dk.dtu.ToDoList.model.data.TaskPriority
import dk.dtu.ToDoList.model.data.TaskTag
import dk.dtu.ToDoList.model.data.RecurrencePattern
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

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
        var selectedTag by remember { mutableStateOf(TaskTag.WORK) }
        var selectedRecurrence by remember { mutableStateOf<RecurrencePattern?>(null) }
        var showDatePicker by remember { mutableStateOf(false) }
        var selectedDate by remember { mutableStateOf(LocalDate.now()) }
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
                        text = "New Task",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = taskName,
                        onValueChange = { taskName = it },
                        label = { Text("Task name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Priority",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        PriorityChip("Low", priorityLevel) { priorityLevel = "Low" }
                        PriorityChip("Medium", priorityLevel) { priorityLevel = "Medium" }
                        PriorityChip("High", priorityLevel) { priorityLevel = "High" }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Deadline",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")))
                    }

                    if (showDatePicker) {
                        Dialog(onDismissRequest = { showDatePicker = false }) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Calendar(
                                    selectedDate = selectedDate,
                                    currentMonth = currentMonth,
                                    onDateSelected = { date ->
                                        selectedDate = date
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

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Category",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ModernDropdownTagSelector(
                        selectedTag = selectedTag,
                        onTagSelected = { selectedTag = it }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Repeat",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ModernDropdownRecurrenceSelector(
                        selectedRecurrence = selectedRecurrence,
                        onRecurrenceSelected = { selectedRecurrence = it }
                    )

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
                                if (taskName.isNotBlank()) {
                                    val newTask = Task(
                                        name = taskName,
                                        priority = TaskPriority.valueOf(priorityLevel.uppercase()),
                                        deadline = Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                                        tag = selectedTag,
                                        completed = false,
                                        recurrence = selectedRecurrence
                                    )
                                    onTaskAdded(newTask)
                                    onDismiss()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Create")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PriorityChip(
    text: String,
    selectedPriority: String,
    onClick: () -> Unit
) {
    val isSelected = text == selectedPriority
    val backgroundColor = when {
        isSelected -> when(text) {
            "Low" -> MaterialTheme.colorScheme.primaryContainer
            "Medium" -> MaterialTheme.colorScheme.secondaryContainer
            else -> MaterialTheme.colorScheme.errorContainer
        }
        else -> MaterialTheme.colorScheme.surface
    }
    val textColor = when {
        isSelected -> when(text) {
            "Low" -> MaterialTheme.colorScheme.onPrimaryContainer
            "Medium" -> MaterialTheme.colorScheme.onSecondaryContainer
            else -> MaterialTheme.colorScheme.onErrorContainer
        }
        else -> MaterialTheme.colorScheme.onSurface
    }

    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = backgroundColor,
            contentColor = textColor
        ),
        modifier = Modifier.widthIn(min = 70.dp)
    ) {
        Text(text)
    }
}

@Composable
fun ModernDropdownTagSelector(
    selectedTag: TaskTag,
    onTagSelected: (TaskTag) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(selectedTag.name)
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            TaskTag.values().forEach { tag ->
                DropdownMenuItem(
                    text = { Text(tag.name) },
                    onClick = {
                        onTagSelected(tag)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ModernDropdownRecurrenceSelector(
    selectedRecurrence: RecurrencePattern?,
    onRecurrenceSelected: (RecurrencePattern?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(selectedRecurrence?.name ?: "Don't repeat")
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            DropdownMenuItem(
                text = { Text("Don't repeat") },
                onClick = {
                    onRecurrenceSelected(null)
                    expanded = false
                }
            )

            RecurrencePattern.values().forEach { pattern ->
                DropdownMenuItem(
                    text = { Text(pattern.name) },
                    onClick = {
                        onRecurrenceSelected(pattern)
                        expanded = false
                    }
                )
            }
        }
    }
}