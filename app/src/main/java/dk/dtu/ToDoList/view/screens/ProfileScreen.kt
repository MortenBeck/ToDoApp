package dk.dtu.ToDoList.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dk.dtu.ToDoList.R
import dk.dtu.ToDoList.model.repository.TaskCRUD
import dk.dtu.ToDoList.util.UserIdManager
import dk.dtu.ToDoList.view.components.SettingsItem
import java.util.*
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext

@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val taskCRUD = remember { TaskCRUD(context) }
    val scope = rememberCoroutineScope()

    // State for task statistics
    var todayTasksCount by remember { mutableStateOf(0) }
    var completedTodayCount by remember { mutableStateOf(0) }
    var daysCompleted by remember { mutableStateOf(24) } // Placeholder for now

    // Get user email
    val userEmail = "Username1@gmail.com" // Hardcoded for now, replace with actual user email later

    // Load task statistics
    LaunchedEffect(key1 = true) {
        scope.launch {
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
            val tomorrow = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }

            val todayTasks = taskCRUD.getTasksByDeadlineRange(today.time, tomorrow.time)
            todayTasksCount = todayTasks.size
            completedTodayCount = todayTasks.count { it.completed }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painterResource(id = R.drawable.background_gradient),
                contentScale = ContentScale.FillBounds
            )
    ) {
        // Profile section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(color = Color.White, shape = CircleShape)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = Color.Black,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Username",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = userEmail,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Stats card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatColumn("Tasks\nToday", todayTasksCount.toString())
                VerticalDivider()
                StatColumn("Tasks Completed\nToday", completedTodayCount.toString())
                VerticalDivider()
                StatColumn("Days\nCompleted", daysCompleted.toString())
            }
        }

        // Settings
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp
            ) {
                Column {
                    SettingsItem(
                        icon = Icons.Default.Person,
                        text = "Account settings",
                        onClick = { navController.navigate("account_settings") }
                    )
                    HorizontalDivider()
                    SettingsItem(
                        icon = Icons.Default.Settings,
                        text = "App settings",
                        onClick = { navController.navigate("app_settings") }
                    )
                }
            }
        }
    }
}

@Composable
private fun StatColumn(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun VerticalDivider() {
    HorizontalDivider(
        modifier = Modifier
            .height(40.dp)
            .width(1.dp),
        color = MaterialTheme.colorScheme.outlineVariant
    )
}