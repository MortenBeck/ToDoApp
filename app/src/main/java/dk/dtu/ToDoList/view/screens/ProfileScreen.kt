package dk.dtu.ToDoList.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dk.dtu.ToDoList.model.data.Task
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import dk.dtu.ToDoList.R
import dk.dtu.ToDoList.model.repository.TasksRepository


@Composable
fun ProfileScreen(navController: NavController) {
    val todayTasks = TasksRepository.todayTasks
    val allTasks = TasksRepository.Tasks
    val completedTodayCount = todayTasks.count { it.completed }

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
                    .background(color = MaterialTheme.colorScheme.surface, shape = CircleShape)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Username",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "Username1@gmail.com",
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
                StatColumn("Tasks\nToday", todayTasks.size.toString())
                VerticalDivider()
                StatColumn("Tasks Completed\nToday", completedTodayCount.toString())
                VerticalDivider()
                StatColumn("Upcoming\nTasks", TasksRepository.futureTasks.size.toString())
            }
        }

        // Settings section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 16.dp)
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
                        onClick = { navController.navigate("account_settings") }  // Update navigation
                    )
                    HorizontalDivider()
                    SettingsItem(
                        icon = Icons.Default.Settings,
                        text = "App settings",
                        onClick = { navController.navigate("app_settings") }  // Update navigation
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

@Composable
private fun SettingsItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text)
    }
}