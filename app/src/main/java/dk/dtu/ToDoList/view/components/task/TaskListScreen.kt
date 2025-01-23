package dk.dtu.ToDoList.view.components.task

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dk.dtu.ToDoList.model.data.task.Task
import dk.dtu.ToDoList.viewmodel.TaskListViewModel

@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel,
    searchText: String, // Add searchText parameter
    onCompleteToggle: (Task) -> Unit,
    onUpdateTask: (Task) -> Unit
) {
    val categorizedTasks by viewModel.categorizedTasks.collectAsState()
    val taskToDelete by viewModel.taskToDelete.collectAsState()
    val sectionStates = remember {
        mutableStateMapOf(
            "Expired" to true,
            "Today" to true,
            "Future" to true,
            "Completed" to false
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        categorizedTasks.forEach { (title, taskList) ->
            // Filter tasks based on searchText
            val filteredTaskList = taskList.filter { it.name.contains(searchText, ignoreCase = true) }
            if (filteredTaskList.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = title,
                        count = filteredTaskList.size,
                        isExpanded = sectionStates[title] ?: false,
                        onToggle = { sectionStates[title] = !(sectionStates[title] ?: true) }
                    )
                }
                if (sectionStates[title] == true) {
                    items(filteredTaskList, key = { it.id }) { task ->
                        SwipeableTaskItem(
                            task = task,
                            searchText = searchText, // Pass searchText to highlight matches
                            onDelete = { viewModel.requestDelete(task) },
                            onCompleteToggle = onCompleteToggle,
                            onUpdateTask = onUpdateTask,
                            onDeleteRequest = { viewModel.requestDelete(task) },
                            taskListViewModel = viewModel
                        )
                    }
                }
            }
        }
    }

    taskToDelete?.let { task ->
        DeleteTaskDialog(
            task = task,
            onDismiss = { viewModel.cancelDelete() },
            onConfirmDelete = { deleteAll ->
                viewModel.confirmDelete(task, deleteAll)
            }
        )
    }
}


@Composable
fun EmptyTasksMessage() {
    Box(
        modifier = Modifier.fillMaxSize().padding(30.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Text(
                text = "It seems you haven't added any tasks yet!",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Click the \"+\"-button in the bottom-right to get started!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    count: Int,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (isExpanded) "Collapse section" else "Expand section",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}