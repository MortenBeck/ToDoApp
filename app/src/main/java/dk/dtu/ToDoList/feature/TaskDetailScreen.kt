package dk.dtu.ToDoList.feature

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dk.dtu.ToDoList.data.Task
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.text.style.TextOverflow
import dk.dtu.ToDoList.data.TaskPriority
import androidx.compose.material.TopAppBar
import androidx.compose.material.IconButton
import androidx.compose.material.Icon



@Composable
fun TaskDetailScreen(
    task: Task,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMarkCompleted: (Boolean) -> Unit,
    onToggleFavorite: (Boolean) -> Unit,
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onEdit() }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Task")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Task Title and Priority
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Priority Icon",
                    tint = when (task.priority) {
                        TaskPriority.HIGH -> Color.Red
                        TaskPriority.MEDIUM -> Color.Yellow
                        TaskPriority.LOW -> Color.Green
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = task.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
            }

            // Due Date
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Due Date Icon"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Due: ${task.deadline}")
            }

            // Task Description
//            Column {
//                Text(
//                    "Description:",
//                    style = MaterialTheme.typography.titleMedium,
//                    fontWeight = FontWeight.SemiBold
//                )
//                Text(task.description, style = MaterialTheme.typography.bodyMedium)
//            }

            // Mark as Completed
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = task.completed,
                    onCheckedChange = { isChecked -> onMarkCompleted(isChecked) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Mark as completed", style = MaterialTheme.typography.bodyMedium)
            }

            // Favorite Toggle
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (task.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite Icon",
                    tint = Color.Red
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = { onToggleFavorite(!task.isFavorite) }) {
                    Text(if (task.isFavorite) "Unfavorite Task" else "Favorite Task")
                }
            }

            // Spacer to push the delete button to the bottom
            Spacer(modifier = Modifier.weight(1f))

            // Delete Button
            Button(
                onClick = onDelete,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Delete Task", color = Color.White)
            }
        }
    }
}
