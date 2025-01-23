package dk.dtu.ToDoList.domain.components.task

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.NavController
import dk.dtu.ToDoList.data.mapper.*
import dk.dtu.ToDoList.domain.model.Task
import dk.dtu.ToDoList.data.mapper.events.AddTaskEvent
import dk.dtu.ToDoList.presentation.viewmodel.AddTaskViewModel
import java.time.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import dk.dtu.ToDoList.presentation.view.components.task.CategorySelector
import dk.dtu.ToDoList.presentation.view.components.task.DeadlineSelector
import dk.dtu.ToDoList.presentation.view.components.task.DialogButtons
import dk.dtu.ToDoList.presentation.view.components.task.PrioritySelector
import dk.dtu.ToDoList.presentation.view.components.task.RecurrenceSelector
import dk.dtu.ToDoList.presentation.view.components.task.TaskNameField

/**
 * Displays a dialog where the user can create a new [Task]. The dialog includes
 * fields for the task name, priority, deadline, category, and recurrence pattern.
 *
 * @param showDialog Whether the dialog should be displayed.
 * @param navController A [NavController] instance for navigation (currently unused).
 * @param onDismiss A callback triggered when the user dismisses the dialog (e.g., pressing "Cancel" or clicking outside).
 * @param onTaskAdded A callback triggered with the newly created [Task] when the user confirms.
 * @param lifecycleScope A [LifecycleCoroutineScope] used for any required coroutines within this composable (currently unused.
 */
@Composable
fun AddTaskDialog(
    showDialog: Boolean,
    viewModel: AddTaskViewModel,
    onDismiss: () -> Unit
) {
    if (!showDialog) return

    val state by viewModel.state.collectAsState()

    Dialog(onDismissRequest = {
        viewModel.onEvent(AddTaskEvent.Dismiss)
        onDismiss()
    }) {
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

                TaskNameField(
                    name = state.taskName,
                    onNameChange = { viewModel.onEvent(AddTaskEvent.UpdateTaskName(it)) }
                )

                Spacer(modifier = Modifier.height(20.dp))

                PrioritySelector(
                    selectedPriority = state.priorityLevel,
                    onPrioritySelected = { viewModel.onEvent(AddTaskEvent.UpdatePriority(it)) }
                )

                Spacer(modifier = Modifier.height(20.dp))

                DeadlineSelector(
                    selectedDate = state.selectedDate,
                    showDatePicker = state.showDatePicker,
                    onShowDatePicker = { viewModel.onEvent(AddTaskEvent.ToggleDatePicker) },
                    onDateSelected = { viewModel.onEvent(AddTaskEvent.UpdateDate(it)) }
                )

                Spacer(modifier = Modifier.height(20.dp))

                CategorySelector(
                    selectedTag = state.selectedTag,
                    onTagSelected = { viewModel.onEvent(AddTaskEvent.UpdateTag(it)) }
                )

                Spacer(modifier = Modifier.height(20.dp))

                RecurrenceSelector(
                    selectedRecurrence = state.selectedRecurrence,
                    onRecurrenceSelected = { viewModel.onEvent(AddTaskEvent.UpdateRecurrence(it)) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                DialogButtons(
                    onDismiss = {
                        viewModel.onEvent(AddTaskEvent.Dismiss)
                        onDismiss()
                    },
                    onSave = {
                        viewModel.onEvent(AddTaskEvent.SaveTask)
                        onDismiss()
                    }
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DatePickerDialog(
        selectedDate: LocalDate?,
        currentMonth: YearMonth,
        onDateSelected: (LocalDate) -> Unit,
        onMonthChanged: (YearMonth) -> Unit,
        onDismiss: () -> Unit
    ) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
        )

        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                        onDateSelected(date)
                    }
                    onDismiss()
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}