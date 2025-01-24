package dk.dtu.ToDoList.view.components.task

import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.NavController
import dk.dtu.ToDoList.model.data.*
import dk.dtu.ToDoList.model.data.task.RecurrencePattern
import dk.dtu.ToDoList.model.data.task.Task
import dk.dtu.ToDoList.model.data.task.TaskPriority
import dk.dtu.ToDoList.model.data.task.TaskTag
import dk.dtu.ToDoList.model.repository.TaskCRUD
import dk.dtu.ToDoList.view.components.miscellaneous.Calendar
import dk.dtu.ToDoList.view.core.theme.getPrioColor
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date



/**
 * Displays a dialog where the user can create a new [Task]. The dialog includes
 * fields for the task name, priority, deadline, category, and recurrence pattern.
 *
 * @param showDialog Whether the dialog should be displayed.
 * @param navController A [NavController] instance for navigation (currently unused).
 * @param onDismiss A callback triggered when the user dismisses the dialog (e.g., pressing "Cancel" or clicking outside).
 * @param onTaskAdded A callback triggered with the newly created [Task] when the user confirms.
 */
@Composable
fun AddTaskDialog(
    showDialog: Boolean,
    navController: NavController,
    onDismiss: () -> Unit,
    onTaskAdded: (Task, Boolean) -> Unit,
) {
    val context = LocalContext.current
    val taskCRUD = remember { TaskCRUD(context) }

    if (showDialog) {
        var taskName by remember { mutableStateOf("") }
        var priorityLevel by remember { mutableStateOf("Low") }
        var selectedTag by remember { mutableStateOf(TaskTag.WORK) }
        var selectedRecurrence by remember { mutableStateOf<RecurrencePattern?>(null) }
        var showDatePicker by remember { mutableStateOf(false) }
        var selectedDate by remember {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mutableStateOf(LocalDate.now())
            } else {
                TODO("VERSION.SDK_INT < O")
            }
        }
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

                    // Task Name
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

                    // Priority Section
                    Text(
                        text = "Priority",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PriorityChip("Low", priorityLevel) { priorityLevel = "Low" }
                        PriorityChip("Medium", priorityLevel) { priorityLevel = "Medium" }
                        PriorityChip("High", priorityLevel) { priorityLevel = "High" }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Deadline Section
                    Text(
                        text = "Deadline",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            selectedDate.format(
                                DateTimeFormatter.ofPattern("MMM dd, yyyy")
                            )
                        )
                    }

                    // Date Picker Dialog
                    if (showDatePicker) {
                        Dialog(onDismissRequest = { showDatePicker = false }) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                // Calendar
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

                    // Category (Tag) Section
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

                    // Recurrence Section
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

                    // Buttons
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
                                        deadline = Date.from(
                                            selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
                                        ),
                                        tag = selectedTag,
                                        completed = false,
                                        recurrence = selectedRecurrence
                                    )
                                    onTaskAdded(newTask, selectedRecurrence != null)
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


/**
 * A composable that renders a chip-like button for selecting the [TaskPriority].
 *
 * @param text The label displayed on the chip (e.g., "Low", "Medium", "High").
 * @param selectedPriority The currently selected priority.
 * @param onClick A callback invoked when this chip is clicked.
 */
@Composable
fun PriorityChip(
    text: String,
    selectedPriority: String,
    onClick: () -> Unit,
) {
    val isSelected = text == selectedPriority
    val backgroundColor = if (isSelected) {
        getPrioColor(TaskPriority.valueOf(text.uppercase()))
    } else {
        MaterialTheme.colorScheme.surface
    }
    val textColor = if (isSelected) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    }

    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = backgroundColor,
            contentColor = textColor
        ),
        modifier = Modifier.width(80.dp),
        contentPadding = PaddingValues(horizontal = 1.dp)
    ) {
        Text(text = text, maxLines = 1)
    }
}


/**
 * A composable that provides a dropdown menu for selecting a [TaskTag].
 *
 * @param selectedTag The currently selected [TaskTag].
 * @param onTagSelected A callback invoked with the newly selected [TaskTag].
 */
@Composable
fun ModernDropdownTagSelector(
    selectedTag: TaskTag,
    onTagSelected: (TaskTag) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
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
            TaskTag.entries.forEach { tag ->
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


/**
 * A composable that provides a dropdown menu for selecting a [RecurrencePattern].
 * Includes an option to select "Don't repeat."
 *
 * @param selectedRecurrence The currently selected [RecurrencePattern], or `null` if none.
 * @param onRecurrenceSelected A callback invoked with the newly selected [RecurrencePattern] or `null`.
 */
@Composable
fun ModernDropdownRecurrenceSelector(
    selectedRecurrence: RecurrencePattern?,
    onRecurrenceSelected: (RecurrencePattern?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
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
            // Option for no recurrence
            DropdownMenuItem(
                text = { Text("Don't repeat") },
                onClick = {
                    onRecurrenceSelected(null)
                    expanded = false
                }
            )
            // Enum entries for recurrence patterns
            RecurrencePattern.entries.forEach { pattern ->
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
