package dk.dtu.ToDoList.view.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dk.dtu.ToDoList.model.data.Task
import dk.dtu.ToDoList.model.data.TaskPriority
import dk.dtu.ToDoList.viewmodel.TaskListViewModel
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

@Composable
fun TaskDetails(
    task: Task,
    onDismiss: () -> Unit,
    onUpdateTask: (Task) -> Unit,
    taskListViewModel: TaskListViewModel
) {
    var taskName by remember { mutableStateOf(task.name) }
    var priorityLevel by remember { mutableStateOf(task.priority.name.lowercase().replaceFirstChar { it.uppercase() }) }
    var selectedTag by remember { mutableStateOf(task.tag) }
    val isCompleted by remember { mutableStateOf(task.completed) }
    var deadline by remember { mutableStateOf(task.deadline.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(24.dp).fillMaxWidth()) {
                Text(text = "Edit Task", style = MaterialTheme.typography.headlineSmall)

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
                Text(text = "Priority", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PriorityChip(text = "Low", selectedPriority = priorityLevel) { priorityLevel = "Low" }
                    PriorityChip(text = "Medium", selectedPriority = priorityLevel) { priorityLevel = "Medium" }
                    PriorityChip(text = "High", selectedPriority = priorityLevel) { priorityLevel = "High" }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Category", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                ModernDropdownTagSelector(selectedTag = selectedTag) { selectedTag = it }

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Deadline", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(deadline.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")))
                }

                if (showDatePicker) {
                    Dialog(onDismissRequest = { showDatePicker = false }) {
                        Calendar(
                            selectedDate = deadline,
                            currentMonth = currentMonth,
                            onDateSelected = { deadline = it; showDatePicker = false },
                            onMonthChanged = { currentMonth = it },
                            tasks = emptyList()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                OutlinedButton(
                    onClick = { taskListViewModel.requestDelete(task) },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Delete Task")
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
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

    val taskToDelete by taskListViewModel.taskToDelete.collectAsState()
    taskToDelete?.let { deleteTask ->
        DeleteTaskDialog(
            task = deleteTask,
            onDismiss = { taskListViewModel.cancelDelete() },
            onConfirmDelete = { deleteAll ->
                taskListViewModel.confirmDelete(deleteTask, deleteAll)
                onDismiss()
            }
        )
    }
}