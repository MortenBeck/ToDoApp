package dk.dtu.ToDoList.feature

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dk.dtu.ToDoList.data.Task
import java.util.Calendar
import androidx.compose.material3.*
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment



@Composable
fun TaskListScreen(
    tasks: MutableList<Task>,
    onDelete: (Task) -> Unit,
    onFavoriteToggle: (Task) -> Unit,
    onCompleteToggle: (Task) -> Unit
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

    val todayTasks = tasks.filter { it.deadline >= todayStart && it.deadline < tomorrowStart }
    val futureTasks = tasks.filter { it.deadline >= tomorrowStart }
    val expiredTasks = tasks.filter { it.deadline < todayStart && !it.completed }
    val completedTasks = tasks.filter { it.deadline < todayStart && it.completed }

    val isExpiredExpanded = remember { mutableStateOf(true) }
    val isTodayExpanded = remember { mutableStateOf(true) }
    val isFutureExpanded = remember { mutableStateOf(true) }
    val isCompletedExpanded = remember { mutableStateOf(false) }

    val isEmpty =
        expiredTasks.isEmpty() && todayTasks.isEmpty() && futureTasks.isEmpty() && completedTasks.isEmpty()

    if (isEmpty) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
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
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
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
                            key = { _, task -> "${task.name}_${task.deadline.time}" } // Composite key
                        ) { _, task ->
                            SwipeableTaskItem(
                                task = task,
                                onDelete = {
                                    tasks.toMutableList().remove(task) // Remove from original list
                                    onDelete(task) // Trigger external delete logic
                                },
                                onFavoriteToggle = onFavoriteToggle,
                                onCompleteToggle = onCompleteToggle
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
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

// Creating an area for the "Today" and "Future" headline
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
            .padding(vertical = 8.dp)
            .clickable { onToggle() }, // Handle clicks to toggle visibility
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$title ($count)",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            color = Color.Black
        )
        Icon(
            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = if (isExpanded) "Collapse" else "Expand"
        )
    }
    HorizontalDivider(
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        thickness = 1.dp
    )
}
