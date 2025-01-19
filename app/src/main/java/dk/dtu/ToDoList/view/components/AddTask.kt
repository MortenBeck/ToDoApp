package dk.dtu.ToDoList.view.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowDropDown
import java.util.Date
import androidx.navigation.NavController
import dk.dtu.ToDoList.model.data.RecurrencePattern
import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults

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
            Surface(
                shape = MaterialTheme.shapes.large,
                color = Color(0xFFE2EFF5),
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "New Task",
                        style = MaterialTheme.typography.headlineMedium
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
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Priority",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White
                        )
                    ) {
                        Text(selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")))
                    }

                    if (showDatePicker) {
                        Dialog(onDismissRequest = { showDatePicker = false }) {
                            Surface(
                                shape = MaterialTheme.shapes.large,
                                color = Color(0xFFE9F2F5),
                                tonalElevation = 3.dp
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
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ModernDropdownTagSelector(
                        color = Color.White,
                        selectedTag = selectedTag,
                        onTagSelected = { selectedTag = it }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Repeat",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.widthIn(min = 70.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = when(text) {
                "Low" -> if (selectedPriority == text) Color(0xFF6D8FFF) else Color.White
                "Medium" -> if (selectedPriority == text) Color(0xFFFFD16D) else Color.White
                else -> if (selectedPriority == text) Color(0xFFFF6D6D) else Color.White
            }
        ),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Text(
            text = text,
            color = if (selectedPriority == text) Color.Black else Color(0xFF616161),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun ModernDropdownTagSelector(
    selectedTag: TaskTag,
    onTagSelected: (TaskTag) -> Unit,
    color: Color
) {
    var expanded by remember { mutableStateOf(false) }

    OutlinedButton(
        onClick = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = color
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(selectedTag.name)
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .background(
                color = Color.White,
                shape = MaterialTheme.shapes.medium
            )
    ) {
        TaskTag.values().forEach { tag ->
            DropdownMenuItem(
                onClick = {
                    onTagSelected(tag)
                    expanded = false
                },
                text = {
                    Text(
                        text = tag.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(8.dp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun ModernDropdownRecurrenceSelector(
    selectedRecurrence: RecurrencePattern?,
    onRecurrenceSelected: (RecurrencePattern?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    OutlinedButton(
        onClick = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(selectedRecurrence?.name ?: "Don't repeat")
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .background(
                color = Color.White,
                shape = MaterialTheme.shapes.medium
            )
    ) {
        DropdownMenuItem(
            onClick = {
                onRecurrenceSelected(null)
                expanded = false
            },
            text = {
                Text(
                    text = "Don't repeat",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(8.dp)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        )

        RecurrencePattern.values().forEach { pattern ->
            DropdownMenuItem(
                onClick = {
                    onRecurrenceSelected(pattern)
                    expanded = false
                },
                text = {
                    Text(
                        text = pattern.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(8.dp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
        }
    }
}