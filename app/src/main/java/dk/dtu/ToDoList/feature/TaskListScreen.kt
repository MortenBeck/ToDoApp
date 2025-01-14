package dk.dtu.ToDoList.feature

import android.os.Build
import androidx.annotation.RequiresApi
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
    userId: String?,
    onTaskDeleted: (Task) -> Unit,
    onFavoriteToggle: (Task) -> Unit,
    onCompleteToggle: (Task) -> Unit
) {
    val isLoading = remember { mutableStateOf(true) }
    val tasksState = remember { mutableStateOf<List<Task>>(emptyList()) }

    val effectiveUserId = userId.takeIf {!it.isNullOrBlank() } ?: "testuser123"

    // Fetch tasks from Firebase
    LaunchedEffect(userId) {
        TasksRepository.getTasks(
            effectiveUserId,
            onSuccess = { fetchedTasks ->
                tasksState.value = fetchedTasks
                isLoading.value = false
            },
            onFailure = { error ->
                isLoading.value = false
            }
        )
    }

    if (isLoading.value) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    } else {
        val tasks = tasksState.value
        if (tasks.isEmpty()) {
            // Show empty state message
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "You have no tasks. Add one now!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        } else {
            // Categorize and display tasks
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

            TaskListContent(
                expiredTasks = expiredTasks,
                todayTasks = todayTasks,
                futureTasks = futureTasks,
                completedTasks = completedTasks,
                onTaskDeleted = onTaskDeleted,
                onFavoriteToggle = onFavoriteToggle,
                onCompleteToggle = onCompleteToggle
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskListContent(
    expiredTasks: List<Task>,
    todayTasks: List<Task>,
    futureTasks: List<Task>,
    completedTasks: List<Task>,
    onTaskDeleted: (Task) -> Unit,
    onFavoriteToggle: (Task) -> Unit,
    onCompleteToggle: (Task) -> Unit
) {
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
                    isExpanded = true,
                    onToggle = {}
                )
            }
            itemsIndexed(expiredTasks) { _, task ->
                TaskItem(
                    task = task,
                    onDelete = { onTaskDeleted(task) },
                    onFavoriteToggle = onFavoriteToggle,
                    onCompleteToggle = onCompleteToggle
                )
            }
        }

        // "Today" Section
        if (todayTasks.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Today",
                    count = todayTasks.size,
                    isExpanded = true,
                    onToggle = {}
                )
            }
            itemsIndexed(todayTasks) { _, task ->
                TaskItem(
                    task = task,
                    onDelete = { onTaskDeleted(task) },
                    onFavoriteToggle = onFavoriteToggle,
                    onCompleteToggle = onCompleteToggle
                )
            }
        }

        // "Future" Section
        if (futureTasks.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Future",
                    count = futureTasks.size,
                    isExpanded = true,
                    onToggle = {}
                )
            }
            itemsIndexed(futureTasks) { _, task ->
                TaskItem(
                    task = task,
                    onDelete = { onTaskDeleted(task) },
                    onFavoriteToggle = onFavoriteToggle,
                    onCompleteToggle = onCompleteToggle
                )
            }
        }

        // "Completed" Section
        if (completedTasks.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Past Completions",
                    count = completedTasks.size,
                    isExpanded = true,
                    onToggle = {}
                )
            }
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
