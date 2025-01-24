package dk.dtu.ToDoList.view.components.task

import android.annotation.SuppressLint
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.KeyboardDoubleArrowUp
import androidx.compose.material.icons.outlined.KeyboardDoubleArrowDown
import androidx.compose.material.icons.outlined.DragHandle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dk.dtu.ToDoList.R
import dk.dtu.ToDoList.model.data.task.Task
import dk.dtu.ToDoList.model.data.task.TaskPriority
import dk.dtu.ToDoList.model.data.task.TaskTag
import dk.dtu.ToDoList.view.core.theme.getPrioColor
import dk.dtu.ToDoList.view.core.theme.getTaskColor
import dk.dtu.ToDoList.viewmodel.TaskListViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TaskList(
    Tasks: List<Task>,
    searchText: String,
    modifier: Modifier = Modifier,
    onDelete: (Task) -> Unit,
    onCompleteToggle: (Task) -> Unit,
    onUpdateTask: (Task) -> Unit,
    onDeleteRequest: (Task) -> Unit,
    taskListViewModel: TaskListViewModel
) {
    val filteredTasks = if (searchText.isNotEmpty()) {
        Tasks.filter { it.name.contains(searchText, ignoreCase = true) }
    } else {
        Tasks
    }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        itemsIndexed(filteredTasks) { _, task ->
            SwipeableTaskItem(
                task = task,
                searchText = searchText,
                onDelete = onDelete,
                onCompleteToggle = onCompleteToggle,
                onUpdateTask = onUpdateTask,
                onDeleteRequest = onDeleteRequest,
                taskListViewModel = taskListViewModel
            )
        }
    }
}

@Composable
private fun BadgeItem(
    badgeText: String,
    badgeColor: Color,
    badgeIcon: Int
) {
    Surface(
        color = badgeColor,
        shape = MaterialTheme.shapes.small,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                painter = painterResource(id = badgeIcon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = badgeText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    searchText: String,
    onDelete: (Task) -> Unit,
    onCompleteToggle: (Task) -> Unit,
    onUpdateTask: (Task) -> Unit,
    taskListViewModel: TaskListViewModel,
    modifier: Modifier = Modifier
) {
    var showDetails by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val vibrator = context.getSystemService(Vibrator::class.java)

    // Highlight text helped by chatGPT
    fun buildHighlightedText(text: String, query: String): AnnotatedString {
        val builder = AnnotatedString.Builder()
        val startIndex = text.indexOf(query, ignoreCase = true)
        if (startIndex != -1) {
            builder.append(text.substring(0, startIndex))
            builder.pushStyle(SpanStyle(background = Color.LightGray))
            builder.append(text.substring(startIndex, startIndex + query.length))
            builder.pop()
            builder.append(text.substring(startIndex + query.length))
        } else {
            builder.append(text)
        }
        return builder.toAnnotatedString()
    }

    val highlightedName = buildHighlightedText(task.name, searchText)

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { showDetails = true },
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {  if (vibrator.hasVibrator()) {
                val effect = VibrationEffect.createOneShot(
                    250, // Duration in milliseconds
                    VibrationEffect.DEFAULT_AMPLITUDE // Default vibration amplitude
                )
                vibrator.vibrate(effect)}
            onCompleteToggle(task) }) {

                Icon(
                    imageVector = if (task.completed) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (task.completed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = when (task.priority) {
                            TaskPriority.HIGH -> Icons.Outlined.KeyboardDoubleArrowUp
                            TaskPriority.MEDIUM -> Icons.Outlined.DragHandle
                            TaskPriority.LOW -> Icons.Outlined.KeyboardDoubleArrowDown
                        },
                        contentDescription = "Priority ${task.priority}",
                        tint = getPrioColor(task.priority),
                        modifier = Modifier.size(20.dp)
                    )

                    Text(
                        text = highlightedName,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (task.completed) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        else MaterialTheme.colorScheme.onSurface,
                        textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BadgeItem(
                        badgeText = SimpleDateFormat("dd-MM-yyyy", Locale.US).format(task.deadline),
                        badgeColor = when {
                            isTaskToday(task) -> MaterialTheme.colorScheme.errorContainer
                            isTaskTomorrow(task) -> MaterialTheme.colorScheme.primaryContainer
                            isTaskExpired(task) -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        },
                        badgeIcon = R.drawable.calender_black
                    )

                    BadgeItem(
                        badgeText = task.tag.name,
                        badgeColor = getTaskColor(task.tag),
                        badgeIcon = when (task.tag) {
                            TaskTag.WORK -> R.drawable.work
                            TaskTag.SCHOOL -> R.drawable.school
                            TaskTag.PET -> R.drawable.pet
                            TaskTag.SPORT -> R.drawable.sport
                            TaskTag.HOME -> R.drawable.home_black
                            TaskTag.TRANSPORT -> R.drawable.transport
                            TaskTag.PRIVATE -> R.drawable.lock
                            TaskTag.SOCIAL -> R.drawable.social
                        }
                    )
                }
            }
        }
    }

    if (showDetails) {
        TaskDetails(
            task = task,
            onDismiss = { showDetails = false },
            onUpdateTask = onUpdateTask,
            taskListViewModel = taskListViewModel
        )
    }
}

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun SwipeableTaskItem(
    task: Task,
    searchText: String,
    onDelete: (Task) -> Unit,
    onCompleteToggle: (Task) -> Unit,
    onUpdateTask: (Task) -> Unit,
    onDeleteRequest: (Task) -> Unit = {},
    taskListViewModel: TaskListViewModel
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val swipeThreshold = 200f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { change, dragAmount ->
                        offsetX = (offsetX + dragAmount).coerceAtLeast(0f)
                        change.consume()
                    },
                    onDragEnd = {
                        if (offsetX > swipeThreshold) {
                            onDeleteRequest(task)
                        }
                        offsetX = 0f
                    }
                )
            }
    ) {
        TaskItem(
            task = task,
            searchText = searchText,
            onDelete = onDeleteRequest,
            onCompleteToggle = onCompleteToggle,
            onUpdateTask = onUpdateTask,
            taskListViewModel = taskListViewModel,
            modifier = Modifier.offset(x = offsetX.dp)
        )
    }
}

@Composable
fun isTaskToday(task: Task): Boolean {
    val todayCalendar = Calendar.getInstance()
    val taskCalendar = Calendar.getInstance().apply {
        time = task.deadline
    }
    return todayCalendar.get(Calendar.YEAR) == taskCalendar.get(Calendar.YEAR) &&
            todayCalendar.get(Calendar.DAY_OF_YEAR) == taskCalendar.get(Calendar.DAY_OF_YEAR)
}

@Composable
fun isTaskExpired(task: Task): Boolean {
    val todayCalendar = Calendar.getInstance()
    val taskCalendar = Calendar.getInstance().apply {
        time = task.deadline
    }
    return taskCalendar.before(todayCalendar) && !isTaskToday(task)
}

@Composable
fun isTaskTomorrow(task: Task): Boolean {
    val todayCalendar = Calendar.getInstance()
    val taskCalendar = Calendar.getInstance().apply {
        time = task.deadline
    }
    todayCalendar.add(Calendar.DAY_OF_YEAR, 1)
    return todayCalendar.get(Calendar.YEAR) == taskCalendar.get(Calendar.YEAR) &&
            todayCalendar.get(Calendar.DAY_OF_YEAR) == taskCalendar.get(Calendar.DAY_OF_YEAR)
}