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

@Composable
fun TaskListScreen(
    tasks: List<Task>,
    onDelete: (Task) -> Unit,
    onCompleteToggle: (Task) -> Unit,
    onUpdateTask: (Task) -> Unit,
    searchText: String
) {
    var taskToDelete by remember { mutableStateOf<Task?>(null) }

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

    val todayTasks = tasks.filter { it.deadline >= todayStart && it.deadline < tomorrowStart }.sortedBy { it.deadline }
    val futureTasks = tasks.filter { it.deadline >= tomorrowStart }.sortedBy { it.deadline }
    val expiredTasks = tasks.filter { it.deadline < todayStart && !it.completed }.sortedBy { it.deadline }
    val completedTasks = tasks.filter { it.deadline < todayStart && it.completed }.sortedBy { it.deadline }

    // State for section expansion
    val isExpiredExpanded = remember { mutableStateOf(true) }
    val isTodayExpanded = remember { mutableStateOf(true) }
    val isFutureExpanded = remember { mutableStateOf(true) }
    val isCompletedExpanded = remember { mutableStateOf(false) }

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

@OptIn(ExperimentalMaterial3Api::class)
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