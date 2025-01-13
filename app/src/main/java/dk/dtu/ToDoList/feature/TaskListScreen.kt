package dk.dtu.ToDoList.feature

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dk.dtu.ToDoList.data.Task
import dk.dtu.ToDoList.data.TasksRepository
import java.util.Calendar

@Composable
fun TaskListScreen(
    userId: String,
    tasks: List<Task>,
    onTaskDeleted: (Task) -> Unit,
    onFavoriteToggle: (Task) -> Unit,
    onCompleteToggle: (Task) -> Unit
) {
    val isLoading = remember { mutableStateOf(true) }

    // Fetch tasks from Firebase
    LaunchedEffect(userId) {
        TasksRepository.getTasks(userId, onSuccess = { fetchedTasks ->
            isLoading.value = false
        }, onFailure = { error ->
            isLoading.value = false
        })
    }

    if (isLoading.value) {
        CircularProgressIndicator(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.primary)
    } else {
        // Filter tasks based on date categories
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


        // States for each section's expanded/collapsed status
        val isExpiredExpanded = remember { mutableStateOf(true) }
        val isTodayExpanded = remember { mutableStateOf(true) }
        val isFutureExpanded = remember { mutableStateOf(true) }
        val isCompletedExpanded = remember { mutableStateOf(false) }

        val isEmpty = expiredTasks.isEmpty() && todayTasks.isEmpty() && futureTasks.isEmpty() && completedTasks.isEmpty()

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
                // "Expired Tasks" Section
                if (expiredTasks.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Expired",
                            count = expiredTasks.size,
                            isExpanded = isExpiredExpanded.value,
                            onToggle = { isExpiredExpanded.value = !isExpiredExpanded.value }
                        )
                    }
                    if (isExpiredExpanded.value) {
                        itemsIndexed(expiredTasks) { _, task ->
                            TaskItem(
                                task = task,
                                onDelete = { onTaskDeleted(task) },
                                onFavoriteToggle = onFavoriteToggle,
                                onCompleteToggle = onCompleteToggle
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }

                // "Today" Section
                if (todayTasks.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Today",
                            count = todayTasks.size,
                            isExpanded = isTodayExpanded.value,
                            onToggle = { isTodayExpanded.value = !isTodayExpanded.value }
                        )
                    }
                    if (isTodayExpanded.value) {
                        itemsIndexed(todayTasks) { _, task ->
                            TaskItem(
                                task = task,
                                onDelete = { onTaskDeleted(task) },
                                onFavoriteToggle = onFavoriteToggle,
                                onCompleteToggle = onCompleteToggle
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }

                // "Future" Section
                if (futureTasks.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Future",
                            count = futureTasks.size,
                            isExpanded = isFutureExpanded.value,
                            onToggle = { isFutureExpanded.value = !isFutureExpanded.value }
                        )
                    }
                    if (isFutureExpanded.value) {
                        itemsIndexed(futureTasks) { _, task ->
                            TaskItem(
                                task = task,
                                onDelete = { onTaskDeleted(task) },
                                onFavoriteToggle = onFavoriteToggle,
                                onCompleteToggle = onCompleteToggle
                            )
                        }
                    }
                }

                // "Completed" Section
                if (completedTasks.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Past Completions",
                            count = completedTasks.size,
                            isExpanded = isCompletedExpanded.value,
                            onToggle = { isCompletedExpanded.value = !isCompletedExpanded.value }
                        )
                    }
                    if (isCompletedExpanded.value) {
                        itemsIndexed(completedTasks) { _, task ->
                            TaskItem(
                                task = task,
                                onDelete = { onTaskDeleted(task) },
                                onFavoriteToggle = onFavoriteToggle,
                                onCompleteToggle = onCompleteToggle
                            )
                        }
                    }
                }
            }
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
    Divider(
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        thickness = 1.dp
    )
}
