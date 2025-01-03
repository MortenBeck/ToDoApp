package dk.dtu.ToDoList.feature

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dk.dtu.ToDoList.data.Task
import java.util.Calendar

@Composable
fun TaskListScreen(tasks: List<Task>) {
    // Get today's start-of-day and tomorrow's start-of-day
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

    // Filter tasks based on deadline
    val todayTasks = tasks.filter { it.deadline >= todayStart && it.deadline < tomorrowStart }
    val futureTasks = tasks.filter { it.deadline >= tomorrowStart }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // "Today" Section
        if (todayTasks.isNotEmpty()) {
            item {
                SectionHeader(title = "Today")
            }
            itemsIndexed(todayTasks) { _, task ->
                TaskItem(task = task)
            }
            item {
                Spacer(modifier = Modifier.height(16.dp)) // Space between sections
            }
        }

        // "Future" Section
        if (futureTasks.isNotEmpty()) {
            item {
                SectionHeader(title = "Future")
            }
            itemsIndexed(futureTasks) { _, task ->
                TaskItem(task = task)
            }
        }
    }
}

// Creating an area for the "Today" and "Future" headline
@Composable
fun SectionHeader(title: String) {
    Column {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            color = Color.Black
        )
        Divider(color = Color.Gray, thickness = 1.dp)
    }
}