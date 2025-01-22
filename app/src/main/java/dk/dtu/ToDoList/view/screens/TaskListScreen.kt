package dk.dtu.ToDoList.view.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
    tasks: List<Task>,
    onDelete: (Task) -> Unit,
    onCompleteToggle: (Task) -> Unit,
    onUpdateTask: (Task) -> Unit,
    searchText: String
) {
    var taskToDelete by remember { mutableStateOf<Task?>(null) }

    // Define time boundaries for categorized tasks
    val todayStart = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time

    val tomorrowStart = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time

    // Partition tasks into relevant sections
    val todayTasks = tasks.filter { it.deadline >= todayStart && it.deadline < tomorrowStart }.sortedBy { it.deadline }
    val futureTasks = tasks.filter { it.deadline >= tomorrowStart }.sortedBy { it.deadline }
    val expiredTasks = tasks.filter { it.deadline < todayStart && !it.completed }.sortedBy { it.deadline }
    val completedTasks = tasks.filter { it.deadline < todayStart && it.completed }.sortedBy { it.deadline }

    // Track expanded/collapsed state for each section
    val isExpiredExpanded = remember { mutableStateOf(true) }
    val isTodayExpanded = remember { mutableStateOf(true) }
    val isFutureExpanded = remember { mutableStateOf(true) }
    val isCompletedExpanded = remember { mutableStateOf(false) }

    // If no tasks exist at all, show a prompt. Otherwise, show categorized sections
    if (tasks.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp),
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
            // Helper to render each section with a header and tasks
            fun renderSection(
                title: String,
                tasks: List<Task>,
                isExpanded: MutableState<Boolean>
            ) {
                if (tasks.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = title,
                            count = tasks.size,
                            isExpanded = isExpanded.value,
                            onToggle = { isExpanded.value = !isExpanded.value }
                        )
                    }
                    if (isExpanded.value) {
                        itemsIndexed(
                            items = tasks,
                            key = { _, task -> task.id }
                        ) { _, task ->
                            SwipeableTaskItem(
                                task = task,
                                searchText = searchText,
                                onDelete = onDelete,
                                onCompleteToggle = onCompleteToggle,
                                onUpdateTask = onUpdateTask,
                                onDeleteRequest = { taskToDelete = it }
                            )
                        }
                    }
                }
            }

            // Render four sections: expired, today, future and past completions
            renderSection("Expired", expiredTasks, isExpiredExpanded)
            renderSection("Today", todayTasks, isTodayExpanded)
            renderSection("Future", futureTasks, isFutureExpanded)
            renderSection("Past Completions", completedTasks, isCompletedExpanded)
        }
    }

    // Delete confirmation dialog
    if (taskToDelete != null) {
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
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
                        taskToDelete?.let { task ->
                            if (task.recurringGroupId != null) {
                                // Delete all recurring instances
                                tasks.filter { it.recurringGroupId == task.recurringGroupId }
                                    .forEach { onDelete(it) }
                            } else {
                                // Delete single task
                                onDelete(task)
                            }
                        }
                        taskToDelete = null
                    }
                ) {
                    Text(if (taskToDelete?.recurringGroupId != null) "Delete All" else "Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        if (taskToDelete?.recurringGroupId != null) {
                            // Delete only this instance of recurring task
                            taskToDelete?.let { onDelete(it) }
                        }
                        taskToDelete = null
                    }
                ) {
                    Text(if (taskToDelete?.recurringGroupId != null) "Delete This Only" else "Cancel")
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