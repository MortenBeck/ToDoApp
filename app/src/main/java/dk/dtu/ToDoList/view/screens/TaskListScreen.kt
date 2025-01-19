package dk.dtu.ToDoList.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dk.dtu.ToDoList.model.data.Task
import dk.dtu.ToDoList.view.components.SwipeableTaskItem
import java.util.Calendar

@Composable
fun TaskListScreen(
    tasks: List<Task>,
    onDelete: (Task) -> Unit,
    onCompleteToggle: (Task) -> Unit,
    onUpdateTask: (Task) -> Unit
) {
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

    val isEmpty = tasks.isEmpty()

    if (isEmpty) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "It seems you haven't added any tasks yet!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Click the \"+\"-button in the bottom-right to get started!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 55.dp)
        ) {
            // Function to handle rendering of sections
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
                            key = { _, task -> "${task.name}_${task.deadline.time}" }
                        ) { _, task ->
                            SwipeableTaskItem(
                                task = task,
                                onDelete = { onDelete(task) },
                                onCompleteToggle = { onCompleteToggle(task) },
                                onUpdateTask = onUpdateTask
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
}

@Composable
fun SectionHeader(
    title: String,
    count: Int,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable { onToggle() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Box(
            modifier = Modifier
                .background(
                    color = Color(0xFFE2EFF5),
                    shape = MaterialTheme.shapes.small
                )
                .border(
                    width = 0.5.dp,
                    color = Color(0xFF2A4174),
                    shape = MaterialTheme.shapes.small
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = count.toString(),
                fontSize = 14.sp,
                color = Color(0xFF2A4174)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = if (isExpanded) "Collapse" else "Expand",
            tint = Color.Black
        )
    }
}