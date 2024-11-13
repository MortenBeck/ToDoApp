package dk.dtu.ToDoList.feature

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.layout.VerticalAlignmentLine
import dk.dtu.ToDoList.data.Task
import dk.dtu.ToDoList.data.TaskTag
import dk.dtu.ToDoList.data.TaskPriority
import dk.dtu.ToDoList.data.TasksRepository.simpleDateFormat
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dk.dtu.ToDoList.R
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import dk.dtu.ToDoList.ui.BottomNavBar



@Composable
fun BottomNavBar(
    items: List<BottomNavItem>,
    onItemClick: (BottomNavItem) -> Unit
) {
    BottomNavigation(
        backgroundColor = Color.White,
        contentColor = Color.Black
    ) {
        items.forEach { item ->
            BottomNavigationItem(
                selected = item.isSelected,
                onClick = { onItemClick(item) },
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.label
                    )
                },
                label = { Text(text = item.label) }
            )
        }
    }
}


data class BottomNavItem(
    val label: String,
    val icon: Int,
    val isSelected: Boolean = false
)

@Composable
fun TaskList(Tasks: List<Task>, modifier: Modifier = Modifier) {
    val scrollState = rememberLazyListState()

    LaunchedEffect(Tasks) {
        scrollState.scrollToItem(0)
    }

    LazyColumn(
        state = scrollState,
        modifier = modifier
            .fillMaxSize()
    ) {
        itemsIndexed(Tasks) { index, task ->
            TaskItem(task = task, index = index)
        }
    }
}

@Composable
fun TaskItem(task: Task, index: Int = 0) {
    val dateFormatter = SimpleDateFormat("dd-MM", Locale.US)

    // Determine color for priority based on task priority level
    val priorityColor = when (task.priority) {
        TaskPriority.HIGH -> Color.Red
        TaskPriority.MEDIUM -> Color.Yellow
        TaskPriority.LOW -> Color.Green
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Priority Icon
        Image(
            painter = painterResource(id = R.drawable.priority), // replace with your priority icon
            contentDescription = "Priority Icon",
            colorFilter = ColorFilter.tint(priorityColor),
            modifier = Modifier
                .size(24.dp)
                .padding(end = 2.dp)
        )

        // Circle for completion status
        Box(
            modifier = Modifier
                .size(24.dp)
                .border(1.dp, Color.Black, CircleShape)
                .background(if (task.completed) Color.LightGray else Color.Transparent, CircleShape)
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            if (task.completed) {
                Text(
                    text = "✓", // Unicode checkmark
                    color = Color.Black,
                    fontSize = 16.sp
                )
            }
        }

        // Task details (name and deadline) in a column
        Column(
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f)
        ) {
            // Task name
            Text(
                text = task.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.calender), // replace with your calendar icon drawable ID
                    contentDescription = "Calendar Icon",
                    modifier = Modifier
                        .size(16.dp)
                        .padding(end = 4.dp)
                )

                // Deadline date
                Text(
                    text = dateFormatter.format(task.deadline),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
        Image(
            painter = painterResource(id = R.drawable.work),
            contentDescription = "Tag Icon",
            modifier = Modifier.size(16.dp),
        )
        Spacer(modifier = Modifier.height(4.dp))

        Image(
            painter = painterResource(id = R.drawable.favorite),
            contentDescription = "Favorite Icon",
        )

    }
}


@Preview(showBackground = true)
@Composable
private fun TaskListPreview() {
    TaskList(
        Tasks = listOf(
            Task(
                name = "Homework - UX",
                deadline = simpleDateFormat.parse("17-11-2024")!!,
                priority = TaskPriority.HIGH,
                tag = TaskTag.SCHOOL,
                completed = false
            ),
            Task(
                name = "Fix project at work",
                deadline = simpleDateFormat.parse("18-11-2024")!!,
                priority = TaskPriority.MEDIUM,
                tag = TaskTag.WORK,
                completed = true
            )
        )
    )
}