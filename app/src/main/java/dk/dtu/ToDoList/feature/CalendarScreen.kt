package dk.dtu.ToDoList.feature

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dk.dtu.ToDoList.data.Task
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import androidx.compose.material3.*
import dk.dtu.ToDoList.data.TasksRepository


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(tasks: MutableList<Task>, navController: NavController) { // MutableList to allow deletion
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAddTaskDialog by remember{mutableStateOf(false)}
    var taskToDelete by remember { mutableStateOf<Task?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var tasks by remember { mutableStateOf<List<Task>>(emptyList())}



    // Fetch tasks when the screen is loaded or selected date is changed
    TasksRepository.getTasksForDate(
        selectedDate,
        onSuccess = { fetchedTasks ->
            tasks = fetchedTasks
        },
        onFailure = { exception ->
            println("Error fetching tasks: ${exception.message}")
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = "Calendar",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Calendar
        Calendar(
            selectedDate = selectedDate,
            currentMonth = currentMonth,
            onDateSelected = { selectedDate = it },
            onMonthChanged = { currentMonth = it },
            tasks = tasks
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Filtered Tasks for Selected Date
        TasksForDate(
            tasks = tasks,
            selectedDate = selectedDate,
            onDelete = { task ->
                taskToDelete = task
                showDeleteDialog = true
            },
            onFavoriteToggle = { taskToToggle ->
                // Ensure task has an ID before calling updateTask
                taskToToggle.id?.let { taskId ->
                    TasksRepository.updateTask(
                        taskId = taskId, // Pass the task ID
                        updatedTask = taskToToggle.copy(favorite = !taskToToggle.favorite),
                        onSuccess = {
                            // Handle success (optional)
                        },
                        onFailure = { exception ->
                            // Handle failure (optional)
                        }
                    )
                }
            },
            onCompleteToggle = { taskToComplete ->
                // Ensure task has an ID before calling updateTask
                taskToComplete.id?.let { taskId ->
                    TasksRepository.updateTask(
                        taskId = taskId, // Pass the task ID
                        updatedTask = taskToComplete.copy(completed = !taskToComplete.completed),
                        onSuccess = {
                            // Handle success (optional)
                        },
                        onFailure = { exception ->
                            // Handle failure (optional)
                        }
                    )
                }
            }
        )
    }

    // Floating Add Task Button - Positioned at Bottom-Right Corner
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd // Align to bottom-end
    ) {
        IconButton(
            onClick = { showDialog = true }, // Use the correct state variable
            modifier = Modifier.size(64.dp) // Adjust size as needed
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                shadowElevation = 6.dp
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Task",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .padding(12.dp)
                        .size(32.dp) // Icon size
                )
            }
        }
    }

// Add Task Dialog
    if (showDialog) {
        AddTaskDialog(
            showDialog = showDialog,
            navController = navController,
            onDismiss = { showDialog = false }, // Close dialog on dismiss
            onTaskAdded = { newTask ->
                // Add the new task to Firestore
                TasksRepository.addTask(newTask, onSuccess = {
                    tasks = tasks + newTask // Add the new task to the list
                    showDialog = false // Close the dialog after adding the task
                }, onFailure = {
                    println("Error adding task: ${it.message}")
                })
            }
        )
    }

    // Show delete confirmation dialog
    if (showDeleteDialog && taskToDelete != null) {
        DeleteConfirmation(
            task = taskToDelete!!,
            onConfirm = {
                // Delete the task from Firestore using the task's ID
                taskToDelete!!.id?.let { taskId ->
                    TasksRepository.softDeleteTask(taskId, onSuccess = {
                        tasks = tasks.filter { it != taskToDelete } // Remove from the list
                        taskToDelete = null
                        showDeleteDialog = false
                    }, onFailure = {
                        println("Error deleting task: ${it.message}")
                    })
                }
            },
            onDismiss = {
                taskToDelete = null
                showDeleteDialog = false
            }
        )
    }
}

@Composable
fun TasksForDate(
    tasks: List<Task>,
    selectedDate: LocalDate,
    onDelete: (Task) -> Unit, // Add onDelete callback
    onFavoriteToggle: (Task) -> Unit,
    onCompleteToggle: (Task) -> Unit
) {
    val tasksForDate = remember(selectedDate, tasks) {
        tasks.filter { task ->
            task.deadline.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() == selectedDate
        }
    }

    Column {
        Text(
            text = "Tasks for ${selectedDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (tasksForDate.isNotEmpty()) {
            TaskList(
                tasks = tasksForDate,
                modifier = Modifier.fillMaxWidth(),
                onDelete = onDelete,
                onFavoriteToggle = onFavoriteToggle,
                onCompleteToggle = onCompleteToggle
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