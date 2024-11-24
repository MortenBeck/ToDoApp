package dk.dtu.ToDoList.feature

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import dk.dtu.ToDoList.data.Task
import dk.dtu.ToDoList.data.TasksRepository

@Composable
fun PlannedScreen() {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = "Planned",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Calendar
        Calendar(
            selectedDate = selectedDate,
            currentMonth = currentMonth,
            onDateSelected = { selectedDate = it },
            onMonthChanged = { currentMonth = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tasks for Selected Date
        TasksForDate(selectedDate = selectedDate)
    }
}

@Composable
fun TasksForDate(selectedDate: LocalDate) {
    // Log selected date for debugging
    Log.d("PlannedScreen", "Selected Date: $selectedDate")

    // Safely filter tasks for the selected date
    val tasksForDate = remember(selectedDate) {
        TasksRepository.Tasks.filter { task ->
            try {
                val taskDate = task.deadline?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
                Log.d("PlannedScreen", "Task: ${task.name}, Deadline: $taskDate")
                taskDate == selectedDate
            } catch (e: Exception) {
                Log.e("PlannedScreen", "Error parsing task deadline: ${task.name}", e)
                false
            }
        }
    }

    Column {
        Text(
            text = "Tasks for ${selectedDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Display filtered tasks or fallback message
        if (tasksForDate.isNotEmpty()) {
            TaskList(
                Tasks = tasksForDate,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text(
                text = "No tasks for this day.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
