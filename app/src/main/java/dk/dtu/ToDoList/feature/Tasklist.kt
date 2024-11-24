package dk.dtu.ToDoList.feature

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
import java.util.Locale
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import dk.dtu.ToDoList.R
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem



@Composable
fun BottomNavBar(
    items: List<BottomNavItem>,
    onItemClick: (BottomNavItem) -> Unit
) {
    NavigationBar(
        containerColor  = Color.White,
        contentColor = Color.Black
    ) {
        items.forEach { item ->
            NavigationBarItem(
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
            .fillMaxWidth() // Changed from fillMaxSize
            .heightIn(max = 300.dp) // Add a maximum height
    ) {
        itemsIndexed(Tasks) { index, task ->
            TaskItem(task = task, index = index)
        }
    }
}


@Composable
fun TaskItem(task: Task, index: Int = 0) {
    val dateFormatter = SimpleDateFormat("dd-MM", Locale.US)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
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

        // Task details in a column
        Column(
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f)
        ) {
            // Display only the task title
            Text(
                text = task.name, // Ensure this is always the title
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            if (task.deadline.time != 0L) { // Check if a deadline exists
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.calender_black),
                        contentDescription = "Calendar Icon",
                        modifier = Modifier.size(24.dp)
                    )

                    // Deadline date
                    Text(
                        text = dateFormatter.format(task.deadline),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        // Optional icons for task actions (e.g., favorite, tag)
        Image(
            painter = painterResource(id = R.drawable.favorite_black),
            contentDescription = "Favorite Icon",
            modifier = Modifier.size(24.dp)
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
            ),
            Task(
                name = "Buy groceries",
                deadline = simpleDateFormat.parse("20-11-2024")!!,
                priority = TaskPriority.LOW,
                tag = TaskTag.PRIVATE,
                completed = false
            ),
            Task(
                name = "Prepare presentation",
                deadline = simpleDateFormat.parse("19-11-2024")!!,
                priority = TaskPriority.HIGH,
                tag = TaskTag.WORK,
                completed = false
            ),
            Task(
                name = "Morning run",
                deadline = simpleDateFormat.parse("17-11-2024")!!,
                priority = TaskPriority.LOW,
                tag = TaskTag.SPORT,
                completed = true
            )
        )
    )
}
