package dk.dtu.ToDoList.view.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dk.dtu.ToDoList.model.data.Task
import dk.dtu.ToDoList.view.components.SwipeableTaskItem
import java.util.Calendar
import dk.dtu.ToDoList.viewmodel.TaskListViewModel



/**
 * Displays a list of tasks organized into four sections: expired tasks, today's tasks,
 * future tasks, and past completions. Each section can be expanded or collapsed.
 * If there are no tasks at all, a prompt is shown to the user instead.
 *
 * The function also manages a confirmation dialog for deleting tasks,
 * including special handling for recurring tasks.
 *
 * @param tasks The complete list of [Task] objects to be displayed.
 * @param onDelete A callback invoked when a [Task] is deleted. For recurring tasks,
 *   all instances or a single instance can be deleted depending on user choice.
 * @param onCompleteToggle A callback invoked when a user toggles the completion status of a [Task].
 * @param onUpdateTask A callback invoked when a [Task] is updated (e.g., from editing details).
 * @param searchText An optional search term for highlighting or filtering tasks (though not used directly here).
 */
@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel,
    onCompleteToggle: (Task) -> Unit,
    onUpdateTask: (Task) -> Unit
) {
    val categorizedTasks by viewModel.categorizedTasks.collectAsState()
    val taskToDelete by viewModel.taskToDelete.collectAsState()

    // Track expanded/collapsed states for sections
    val sectionStates = remember {
        mutableStateMapOf(
            "Expired" to true,
            "Today" to true,
            "Future" to true,
            "Completed" to false
        )
    }

    if (categorizedTasks.isEmpty()) {
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
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            categorizedTasks.forEach { (title, taskList) ->
                if (taskList.isNotEmpty()) { // Render only if the section has tasks
                    item {
                        SectionHeader(
                            title = title,
                            count = taskList.size,
                            isExpanded = sectionStates[title] ?: false,
                            onToggle = { sectionStates[title] = !(sectionStates[title] ?: true) }
                        )
                    }
                    if (sectionStates[title] == true) {
                        items(taskList, key = { it.id }) { task ->
                            SwipeableTaskItem(
                                task = task,
                                searchText = "", // Pass appropriate searchText if applicable
                                onDelete = { viewModel.confirmDelete(it, deleteAll = false) },
                                onCompleteToggle = onCompleteToggle,
                                onUpdateTask = onUpdateTask,
                                onDeleteRequest = { viewModel.requestDelete(task) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (taskToDelete != null) {
        AlertDialog(
            onDismissRequest = { viewModel.cancelDelete() },
            title = { Text("Delete Task") },
            text = {
                Text(
                    if (taskToDelete?.recurringGroupId != null)
                        "This task is part of a recurring series. Do you want to delete all instances or just this one?"
                    else
                        "Are you sure you want to delete this task?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        taskToDelete?.let {
                            viewModel.confirmDelete(it, deleteAll = it.recurringGroupId != null)
                        }
                    }
                ) {
                    Text(if (taskToDelete?.recurringGroupId != null) "Delete All" else "Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.cancelDelete() // Cancel deletion without any action
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}


    /**
 * A composable that serves as a header for different task sections.
 * It displays a title, the count of tasks in that section, and an icon to
 * expand or collapse the list below it.
 *
 * @param title The label for the section (e.g., "Today", "Expired").
 * @param count The number of tasks in this section.
 * @param isExpanded Indicates whether the section is currently expanded.
 * @param onToggle A callback to be invoked when the user taps on the header,
 * toggling expansion/collapse.
 */
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


/**
 * A private composable (not currently used in this example, but preserved for reference)
 * that can render the header content. It serves as a secondary helper if customization is needed.
 */
@Composable
private fun SectionHeaderContent(
    title: String,
    count: Int,
    isExpanded: Boolean
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