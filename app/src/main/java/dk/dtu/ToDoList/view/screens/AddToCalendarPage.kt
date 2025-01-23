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
import dk.dtu.ToDoList.model.data.task.Task
import dk.dtu.ToDoList.model.data.task.TaskPriority
import dk.dtu.ToDoList.model.data.task.TaskTag
import dk.dtu.ToDoList.view.components.Calendar
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.util.Date



/**
 * A composable screen that allows the user to select a date from a calendar and then create a [Task]
 * with the chosen date as its deadline. The newly created task is passed back via [onTaskAdded].
 *
 * @param navController A [NavController] for handling navigation actions (e.g., returning to the previous screen).
 * @param taskName The name of the task to be added.
 * @param onTaskAdded A callback invoked with the newly created [Task] once the user confirms.
 */
@Composable
fun AddToCalendarPage(
    navController: NavController,
    taskName: String,
    onTaskAdded: (Task) -> Unit
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

        // A calender composable allowing the user to pick a date for the task's deadline.
        Calendar(
            selectedDate = selectedDate,
            currentMonth = currentMonth,
            onDateSelected = { selectedDate = it },
            onMonthChanged = { currentMonth = it },
            tasks = emptyList() // No tasks to display here; purely selecting a date
        )

        Spacer(modifier = Modifier.height(16.dp))

        // A button that creates a new task with the selected date and navigates back.
        Button(
            onClick = {
                val newTask = Task(
                    name = taskName,
                    priority = TaskPriority.LOW,
                    deadline = Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                    tag = TaskTag.WORK,
                    completed = false
                )
                onTaskAdded(newTask)
                navController.popBackStack()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Add Task")
        }
    }
}

