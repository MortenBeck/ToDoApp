package dk.dtu.ToDoList.view.screens

import android.os.Build
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import dk.dtu.ToDoList.model.data.Task
import dk.dtu.ToDoList.model.data.TaskPriority
import dk.dtu.ToDoList.model.data.TaskTag
import dk.dtu.ToDoList.view.components.Calendar
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.util.Date

@Composable
fun AddToCalendarPage(
    navController: NavController,
    taskName: String,
    onTaskAdded: (Task) -> Unit // Ensures we can add the task from this page
) {
    var selectedDate by remember { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        mutableStateOf(LocalDate.now())
    } else {
        TODO("VERSION.SDK_INT < O")
    }
    }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Add to Calendar for $taskName",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Calendar(
            selectedDate = selectedDate,
            currentMonth = currentMonth,
            onDateSelected = { selectedDate = it },
            onMonthChanged = { currentMonth = it },
            tasks = emptyList()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val newTask = Task(
                    name = taskName,
                    priority = TaskPriority.LOW, // Default priority
                    favorite = false,
                    deadline = Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                    tag = TaskTag.WORK,
                    completed = false
                )
                onTaskAdded(newTask) // Add the task
                navController.popBackStack() // Go back to the previous screen
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Add Task")
        }
    }
}

