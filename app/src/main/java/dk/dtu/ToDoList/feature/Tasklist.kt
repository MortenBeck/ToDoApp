package dk.dtu.ToDoList.feature

import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.Alignment
import dk.dtu.ToDoList.data.Task
import dk.dtu.ToDoList.data.TaskTag
import dk.dtu.ToDoList.data.TaskPriority
import dk.dtu.ToDoList.data.TasksRepository.simpleDateFormat
import java.text.SimpleDateFormat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import java.util.Locale
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import dk.dtu.ToDoList.R
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import java.util.Calendar
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.animateFloatAsState







@Composable
fun TaskList(
    Tasks: List<Task>,
    modifier: Modifier = Modifier,
    onDelete: (Task) -> Unit, // Pass a callback to handle deletion
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
            .heightIn(max = 300.dp) // Add a maximum height
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
                vibrator?.vibrate(
                    VibrationEffect.createOneShot(
                        200,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
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

                Badge(
                    text = if (isToday) "Today" else SimpleDateFormat(
                        "dd-MM-yyyy",
                        Locale.US
                    ).format(task.deadline),
                    color = if (isToday) Color.Red else Color.White,
                    textColor = if (isToday) Color.White else Color.Black,
                    icon = R.drawable.calender_black // Replace with your calendar icon resource
                )


                Badge(
                    text = task.tag.name, // Converts enum tag to string
                    color = when (task.tag) {
                        TaskTag.WORK -> Color(0xFF6d8FFF)
                        TaskTag.SCHOOL -> Color(0xFFFF9c6d)
                        TaskTag.PET -> Color(0xFF6dFF6d)
                        TaskTag.SPORT -> Color(0xFFd631bb)
                        TaskTag.HOME -> Color(0xFFd16dFF)
                        TaskTag.TRANSPORT -> Color(0xFFFFF86d)
                        TaskTag.PRIVATE -> Color(0xFFff6D6D)
                        else -> Color.Gray // Fallback color
                    },
                    icon = when (task.tag) {
                        TaskTag.WORK -> R.drawable.work
                        TaskTag.SCHOOL -> R.drawable.school
                        TaskTag.PET -> R.drawable.pet
                        TaskTag.SPORT -> R.drawable.sport
                        TaskTag.HOME -> R.drawable.home_black
                        TaskTag.TRANSPORT -> R.drawable.transport
                        TaskTag.PRIVATE -> R.drawable.lock
                        else -> R.drawable.folder // Fallback icon
                    }
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp), // Adjust the spacing between icons
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Favorite
            IconButton(onClick = { onFavoriteToggle(task) }) {
                Icon(
                    imageVector = if (task.favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (task.favorite) "Unfavorite Task" else "Favorite Task",
                    tint = if (task.favorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }

            // Delete
            IconButton(onClick = { onDelete(task) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Task")
            }
        }
    }
}

@Composable
fun Badge(text: String, color: Color, textColor: Color = Color.White, icon: Int) {
    Row(
        modifier = Modifier
            .background(color = color, shape = MaterialTheme.shapes.medium)
            .border(width = 1.dp, color = Color.Black, shape = MaterialTheme.shapes.medium)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = textColor,
            modifier = Modifier
                .size(16.dp)
                .padding(end = 4.dp)
        )

        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis // Add ellipsis for overflow
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

@Composable
fun SwipeableTaskItem(
    task: Task,
    onDelete: (Task) -> Unit,
    onFavoriteToggle: (Task) -> Unit,
    onCompleteToggle: (Task) -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) } // Offset for drag
    val swipeThreshold = 200f
    val dragScaleFactor = 0.5f // Adjust this to control drag speed (0.5f = slower, 1.0f = normal)

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
                        // Apply the dragScaleFactor to slow down the drag movement
                        offsetX = (offsetX + dragAmount * dragScaleFactor).coerceAtLeast(0f)
                        change.consume()
                    },
                    onDragEnd = {
                        if (offsetX > swipeThreshold) {
                            onDelete(task) // Trigger delete if swipe exceeds threshold
                        }
                        offsetX = 0f // Reset offset after drag ends
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

