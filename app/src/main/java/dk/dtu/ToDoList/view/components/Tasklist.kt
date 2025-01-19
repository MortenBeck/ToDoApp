package dk.dtu.ToDoList.view.components

import android.annotation.SuppressLint
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dk.dtu.ToDoList.R
import dk.dtu.ToDoList.model.data.Task
import dk.dtu.ToDoList.model.data.TaskPriority
import dk.dtu.ToDoList.model.data.TaskTag
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TaskList(
    Tasks: List<Task>,
    modifier: Modifier = Modifier,
    onDelete: (Task) -> Unit,
    onCompleteToggle: (Task) -> Unit,
    onUpdateTask: (Task) -> Unit
) {
    val scrollState = rememberLazyListState()

    LaunchedEffect(Tasks) {
        scrollState.scrollToItem(0)
    }

    LazyColumn(
        state = scrollState,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 300.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        itemsIndexed(Tasks) { _, task ->
            SwipeableTaskItem(
                task = task,
                onDelete = onDelete,
                onCompleteToggle = onCompleteToggle,
                onUpdateTask = onUpdateTask
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskItem(
    task: Task,
    onDelete: (Task) -> Unit,
    onCompleteToggle: (Task) -> Unit,
    onUpdateTask: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val vibrator = context.getSystemService(Vibrator::class.java)
    var showDetails by remember { mutableStateOf(false) }

    val isToday = isTaskToday(task)
    val isExpired = isTaskExpired(task)
    val isTomorrow = isTaskTomorrow(task)

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { showDetails = true },
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator?.vibrate(
                            VibrationEffect.createOneShot(
                                200,
                                VibrationEffect.DEFAULT_AMPLITUDE
                            )
                        )
                    }
                    onCompleteToggle(task)
                }
            ) {
                Icon(
                    imageVector = if (task.completed) Icons.Default.CheckCircle
                    else Icons.Default.RadioButtonUnchecked,
                    contentDescription = if (task.completed) "Mark as Incomplete"
                    else "Mark as Complete",
                    tint = if (task.completed) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outline
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
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(
                                when (task.priority) {
                                    TaskPriority.HIGH -> MaterialTheme.colorScheme.error
                                    TaskPriority.MEDIUM -> MaterialTheme.colorScheme.tertiary
                                    TaskPriority.LOW -> MaterialTheme.colorScheme.primary
                                }
                            )
                    )

                    Text(
                        text = task.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (task.completed)
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        else MaterialTheme.colorScheme.onSurface,
                        textDecoration = if (task.completed)
                            TextDecoration.LineThrough
                        else TextDecoration.None,
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
                        badgeText = when {
                            isToday -> "Today"
                            isTomorrow -> "Tomorrow"
                            else -> SimpleDateFormat("dd-MM-yyyy", Locale.US)
                                .format(task.deadline)
                        },
                        badgeColor = when {
                            isToday -> MaterialTheme.colorScheme.errorContainer
                            isTomorrow -> MaterialTheme.colorScheme.primaryContainer
                            isExpired -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        },
                        badgeIcon = R.drawable.calender_black
                    )

                    BadgeItem(
                        badgeText = task.tag.name,
                        badgeColor = when (task.tag) {
                            TaskTag.WORK -> MaterialTheme.colorScheme.primaryContainer
                            TaskTag.SCHOOL -> MaterialTheme.colorScheme.secondaryContainer
                            TaskTag.PET -> MaterialTheme.colorScheme.tertiaryContainer
                            TaskTag.SPORT -> MaterialTheme.colorScheme.errorContainer
                            TaskTag.HOME -> MaterialTheme.colorScheme.surfaceVariant
                            TaskTag.TRANSPORT -> MaterialTheme.colorScheme.primaryContainer
                            TaskTag.PRIVATE -> MaterialTheme.colorScheme.secondaryContainer
                            TaskTag.SOCIAL -> MaterialTheme.colorScheme.tertiaryContainer
                        },
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
            onUpdateTask = onUpdateTask
        )
    }
}

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun SwipeableTaskItem(
    task: Task,
    onDelete: (Task) -> Unit,
    onCompleteToggle: (Task) -> Unit,
    onUpdateTask: (Task) -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    val swipeThreshold = 200f
    val dragScaleFactor = 0.5f

    val animatedOffsetX by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { change, dragAmount ->
                        offsetX = (offsetX + dragAmount * dragScaleFactor)
                            .coerceAtLeast(0f)
                        change.consume()
                    },
                    onDragEnd = {
                        if (offsetX > swipeThreshold) {
                            onDelete(task)
                        }
                        offsetX = 0f
                    }
                )
            }
    ) {
        TaskItem(
            task = task,
            onDelete = onDelete,
            onCompleteToggle = onCompleteToggle,
            onUpdateTask = onUpdateTask,
            modifier = Modifier.offset(x = animatedOffsetX.dp)
        )
    }
}

@Composable
private fun isTaskToday(task: Task): Boolean {
    val todayCalendar = Calendar.getInstance()
    val taskCalendar = Calendar.getInstance()
    taskCalendar.time = task.deadline

    return todayCalendar.get(Calendar.YEAR) == taskCalendar.get(Calendar.YEAR) &&
            todayCalendar.get(Calendar.DAY_OF_YEAR) == taskCalendar.get(Calendar.DAY_OF_YEAR)
}

@Composable
private fun isTaskExpired(task: Task): Boolean {
    val todayCalendar = Calendar.getInstance()
    val taskCalendar = Calendar.getInstance()
    taskCalendar.time = task.deadline

    return taskCalendar.before(todayCalendar) && !isTaskToday(task)
}

@Composable
private fun isTaskTomorrow(task: Task): Boolean {
    val todayCalendar = Calendar.getInstance()
    val taskCalendar = Calendar.getInstance()
    taskCalendar.time = task.deadline

    todayCalendar.add(Calendar.DAY_OF_YEAR, 1)
    return todayCalendar.get(Calendar.YEAR) == taskCalendar.get(Calendar.YEAR) &&
            todayCalendar.get(Calendar.DAY_OF_YEAR) == taskCalendar.get(Calendar.DAY_OF_YEAR)
}