package dk.dtu.ToDoList.feature

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi
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
import dk.dtu.ToDoList.data.TasksRepository
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.google.android.gms.tasks.Tasks


@Composable
fun TaskList(
    tasks: List<Task>,
    modifier: Modifier = Modifier,
    onDelete: (Task) -> Unit, // Pass a callback to handle deletion
    onFavoriteToggle: (Task) -> Unit,
    onCompleteToggle: (Task) -> Unit
) {
    val scrollState = rememberLazyListState()

    LaunchedEffect(tasks) {
        scrollState.scrollToItem(0)
    }

    LazyColumn(
        state = scrollState,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 300.dp) // Add a maximum height
    ) {
        itemsIndexed(tasks) { _, task ->
            TaskItem(
                task = task,
                onDelete = onDelete,
                onFavoriteToggle = onFavoriteToggle,
                onCompleteToggle = onCompleteToggle
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskItem(
    task: Task,
    onDelete: (Task) -> Unit,
    onFavoriteToggle: (Task) -> Unit,
    onCompleteToggle: (Task) -> Unit
) {
    val showDeleteDialog = remember { mutableStateOf(false) }
    val dateFormatter = SimpleDateFormat("dd-MM", Locale.US)
    val context = LocalContext.current // Get context
    val vibrator = context.getSystemService(Vibrator::class.java)

    val taskColor = if(task.completed) Color.Gray else Color.Black
    val taskDecor = if(task.completed) TextDecoration.LineThrough else TextDecoration.None

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .background(if(task.completed) Color.LightGray.copy(alpha = 0.2f) else Color.Transparent),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Priority Icon
        val priorityColor = when (task.priority) {
            TaskPriority.HIGH -> Color.Red
            TaskPriority.MEDIUM -> Color.Yellow
            TaskPriority.LOW -> Color.Green
        }
        Image(
            painter = painterResource(id = R.drawable.priority),
            contentDescription = "Priority Icon",
            colorFilter = ColorFilter.tint(priorityColor),
            modifier = Modifier
                .size(24.dp)
                .padding(end = 8.dp)
        )
        // Completion Button
        IconButton(
            onClick = {
                // Trigger vibration on task completion
                vibrator?.vibrate(
                    VibrationEffect.createOneShot(
                        200, // Duration in milliseconds
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
                onCompleteToggle(task)
            },
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Icon(
                imageVector = if (task.completed) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = if (task.completed) "Mark as Incomplete" else "Mark as Complete",
                tint = if (task.completed) Color.Green else Color.Gray
            )
        }

        // Task details in a column
        Column(
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f)
        ) {
            Text(
                text = task.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = taskColor,
                style = TextStyle(textDecoration = taskDecor)
            )
            if (task.deadline.time != 0L) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.calender_black),
                        contentDescription = "Calendar Icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = dateFormatter.format(task.deadline),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
        val tagIcon = when (task.tag) {
            TaskTag.WORK -> R.drawable.work
            TaskTag.SCHOOL -> R.drawable.school
            TaskTag.PET -> R.drawable.pet
            TaskTag.HOME -> R.drawable.home_black
            TaskTag.TRANSPORT -> R.drawable.transport
            TaskTag.PRIVATE -> R.drawable.lock
            else -> R.drawable.folder // Fallback icon if needed
        }
        Image(
            painter = painterResource(id = tagIcon),
            contentDescription = "${task.tag} Icon",
            modifier = Modifier
                .size(16.dp)
                .padding(end = 4.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))

        IconButton(onClick = { onFavoriteToggle(task) }) {
            Icon(
                imageVector = if (task.favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (task.favorite) "Unfavorite Task" else "Favorite Task",
                tint = if (task.favorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
        // Delete Button
        IconButton(onClick = { showDeleteDialog.value = true }) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Task")
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog.value) {
        DeleteConfirmation(
            task = task,
            onConfirm = {
                onDelete(task)
                showDeleteDialog.value = false
            },
            onDismiss = { showDeleteDialog.value = false }
        )
    }
}

