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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dk.dtu.ToDoList.data.Task
import dk.dtu.ToDoList.data.TaskPriority
import dk.dtu.ToDoList.data.TaskTag
import dk.dtu.ToDoList.data.TasksRepository.simpleDateFormat

@Composable
fun TaskListScreen(tasks: List<Task>) {
    // Getting todays date with calendar
    val today = java.util.Calendar.getInstance().apply {
        set(java.util.Calendar.HOUR_OF_DAY, 0)
        set(java.util.Calendar.MINUTE, 0)
        set(java.util.Calendar.SECOND, 0)
        set(java.util.Calendar.MILLISECOND, 0)
    }.time

    // Making a difference between today and future deadlines!
    val todayTasks = tasks.filter { it.deadline <= today }
    val futureTasks = tasks.filter { it.deadline > today }

    LazyColumn (modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        // "Today" Section
        if (todayTasks.isNotEmpty()) {
            item {
                SectionHeader(title = "Today")
            }
            itemsIndexed(todayTasks){index, task ->
                TaskItem(task = task)
            }
            item{
                Spacer(modifier = Modifier.height(16.dp)) // Space between sections
            }
        }

        // "Future" Section
        if (futureTasks.isNotEmpty()) {
            item{
                SectionHeader(title = "Future")
            }
            itemsIndexed(futureTasks){index, task ->
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