package dk.dtu.ToDoList.view.components

import android.annotation.SuppressLint
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.Alignment
import dk.dtu.ToDoList.model.data.Task
import dk.dtu.ToDoList.model.data.TaskTag
import dk.dtu.ToDoList.model.data.TaskPriority
import java.text.SimpleDateFormat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import java.util.Locale
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import dk.dtu.ToDoList.R
import androidx.compose.material3.Icon
import androidx.compose.material3.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import java.util.Calendar
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.runtime.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.animation.core.animateFloatAsState

@Composable
fun TaskList(
    Tasks: List<Task>,
    modifier: Modifier = Modifier,
    onDelete: (Task) -> Unit,
    onFavoriteToggle: (Task) -> Unit,
    onCompleteToggle: (Task) -> Unit
) {
    val scrollState = rememberLazyListState()

    LaunchedEffect(Tasks) {
        scrollState.scrollToItem(0)
    }

    LazyColumn(
        state = scrollState,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 300.dp)
    ) {
        itemsIndexed(Tasks) { _, task ->
            SwipeableTaskItem(
                task = task,
                onDelete = onDelete,
                onFavoriteToggle = onFavoriteToggle,
                onCompleteToggle = onCompleteToggle
            )
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onDelete: (Task) -> Unit,
    onFavoriteToggle: (Task) -> Unit,
    onCompleteToggle: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    val taskColor = if (task.completed) Color.Gray else Color.Black
    val taskDecor = if (task.completed) TextDecoration.LineThrough else TextDecoration.None

    val context = LocalContext.current
    val vibrator = context.getSystemService(Vibrator::class.java)

    val isToday = isTaskToday(task)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
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
            },
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = if (task.completed) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = if (task.completed) "Mark as Incomplete" else "Mark as Complete",
                tint = if (task.completed) Color.Green else Color.Gray
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = when (task.priority) {
                                TaskPriority.HIGH -> Color.Red
                                TaskPriority.MEDIUM -> Color.Yellow
                                TaskPriority.LOW -> Color.Blue
                            },
                            shape = CircleShape
                        )
                        .border(
                            width = 1.dp,
                            color = Color.Black,
                            shape = CircleShape
                        )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = task.name,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = taskColor,
                    textDecoration = taskDecor,
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
                    badgeText = if (isToday) "Today" else SimpleDateFormat(
                        "dd-MM-yyyy",
                        Locale.US
                    ).format(task.deadline),
                    badgeColor = if (isToday) Color.Red else Color.White,
                    badgeTextColor = if (isToday) Color.White else Color.Black,
                    badgeIcon = R.drawable.calender_black
                )

                BadgeItem(
                    badgeText = task.tag.name,
                    badgeColor = when (task.tag) {
                        TaskTag.WORK -> Color(0xFF6d8FFF)
                        TaskTag.SCHOOL -> Color(0xFFFF9c6d)
                        TaskTag.PET -> Color(0xFF6dFF6d)
                        TaskTag.SPORT -> Color(0xFFd631bb)
                        TaskTag.HOME -> Color(0xFFd16dFF)
                        TaskTag.TRANSPORT -> Color(0xFFFFF86d)
                        TaskTag.PRIVATE -> Color(0xFFff6D6D)
                        else -> Color.Gray
                    },
                    badgeTextColor = Color.White,
                    badgeIcon = when (task.tag) {
                        TaskTag.WORK -> R.drawable.work
                        TaskTag.SCHOOL -> R.drawable.school
                        TaskTag.PET -> R.drawable.pet
                        TaskTag.SPORT -> R.drawable.sport
                        TaskTag.HOME -> R.drawable.home_black
                        TaskTag.TRANSPORT -> R.drawable.transport
                        TaskTag.PRIVATE -> R.drawable.lock
                        else -> R.drawable.folder
                    }
                )
            }
        }
    }
}

@Composable
fun BadgeItem(
    badgeText: String,
    badgeColor: Color,
    badgeTextColor: Color = Color.White,
    badgeIcon: Int
) {
    Row(
        modifier = Modifier
            .background(color = badgeColor, shape = MaterialTheme.shapes.medium)
            .border(width = 1.dp, color = Color.Black, shape = MaterialTheme.shapes.medium)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = badgeIcon),
            contentDescription = null,
            tint = badgeTextColor,
            modifier = Modifier
                .size(16.dp)
                .padding(end = 4.dp)
        )

        Text(
            text = badgeText,
            color = badgeTextColor,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun isTaskToday(task: Task): Boolean {
    val todayCalendar = Calendar.getInstance()
    val taskCalendar = Calendar.getInstance()
    taskCalendar.time = task.deadline

    return todayCalendar.get(Calendar.YEAR) == taskCalendar.get(Calendar.YEAR) &&
            todayCalendar.get(Calendar.DAY_OF_YEAR) == taskCalendar.get(Calendar.DAY_OF_YEAR)
}

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun SwipeableTaskItem(
    task: Task,
    onDelete: (Task) -> Unit,
    onFavoriteToggle: (Task) -> Unit,
    onCompleteToggle: (Task) -> Unit
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
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { change, dragAmount ->
                        offsetX = (offsetX + dragAmount * dragScaleFactor).coerceAtLeast(0f)
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
            onFavoriteToggle = onFavoriteToggle,
            onCompleteToggle = onCompleteToggle,
            modifier = Modifier.offset(x = animatedOffsetX.dp)
        )
    }
}