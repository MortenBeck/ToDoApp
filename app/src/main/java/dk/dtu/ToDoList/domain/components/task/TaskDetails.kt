package dk.dtu.ToDoList.domain.components.task

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import dk.dtu.ToDoList.domain.model.Task
import dk.dtu.ToDoList.domain.repository.TaskRepository
import dk.dtu.ToDoList.presentation.TaskListViewModel
import dk.dtu.ToDoList.presentation.view.components.task.CategorySelector
import dk.dtu.ToDoList.presentation.view.components.task.PrioritySelector
import dk.dtu.ToDoList.presentation.view.components.task.TaskNameField
import dk.dtu.ToDoList.presentation.viewmodel.TaskDetailsUiState
import dk.dtu.ToDoList.presentation.viewmodel.TaskDetailsViewModel
import dk.dtu.ToDoList.presentation.viewmodel.TaskDetailsViewModelFactory

@Composable
fun TaskDetails(
    task: Task,
    onDismiss: () -> Unit,
    taskListViewModel: TaskListViewModel
) {
    val viewModel: TaskDetailsViewModel = viewModel(
        factory = TaskDetailsViewModelFactory(taskListViewModel.taskRepository, task)
    )

    val uiState by viewModel.uiState.collectAsState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            TaskDetailsContent(
                uiState = uiState,
                viewModel = viewModel,
                task = task,
                onDismiss = onDismiss,
                taskListViewModel = taskListViewModel
            )
        }
    }
}

@Composable
private fun TaskDetailsContent(
    uiState: TaskDetailsUiState,
    viewModel: TaskDetailsViewModel,
    task: Task,
    onDismiss: () -> Unit,
    taskListViewModel: TaskListViewModel
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

        TaskNameField(
            name = uiState.taskName,
            onNameChange = viewModel::updateTaskName
        )

        PrioritySelector(
            selectedPriority = uiState.priority,
            onPrioritySelected = viewModel::updatePriority
        )

        CategorySelector(
            selectedTag = uiState.tag,
            onTagSelected = viewModel::updateTag
        )

        DeadlinePicker(
            deadline = uiState.deadline,
            onDeadlineChange = viewModel::updateDeadline
        )

        DeleteButton(
            onDelete = {
                viewModel.requestDeleteTask(task)
                taskListViewModel.deleteTask(task)
            }
        )

        ActionButtons(
            onDismiss = onDismiss,
            onSave = {
                viewModel.saveTask(task)
                taskListViewModel.updateTask(task)
                onDismiss()
            }
        )
    }

    DeleteConfirmationDialog(
        taskToDelete = uiState.taskToDelete,
        onDismiss = viewModel::cancelDelete,
        onConfirm = { deleteAll ->
            viewModel.confirmDelete(deleteAll)
            taskListViewModel.deleteTask(task)
            onDismiss()
        }
    )
}