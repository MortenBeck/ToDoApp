package dk.dtu.ToDoList.view.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dk.dtu.ToDoList.R
import dk.dtu.ToDoList.model.repository.TaskCRUD
import dk.dtu.ToDoList.view.components.SettingsItem
import dk.dtu.ToDoList.model.data.state.TaskStats
import dk.dtu.ToDoList.viewmodel.SettingsViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import dk.dtu.ToDoList.data.repository.FirebaseAuthRepository
import dk.dtu.ToDoList.viewmodel.SettingsViewModelFactory


/**
 * A screen presenting the user's profile information, including username and email,
 * as well as some basic task statistics (today's tasks, completed tasks, and upcoming tasks).
 *
 * It also includes navigable settings items that lead to [AccountSettingsScreen] and [AppSettingsScreen].
 *
 * @param navController Used for navigating to other screens, such as account or app settings.
 */
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(
            authRepository = FirebaseAuthRepository(),
            taskRepository = TaskCRUD(LocalContext.current)
        )
    )
) {
    val profileState by viewModel.profileState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painterResource(id = R.drawable.background_gradient),
                contentScale = ContentScale.FillBounds
            )
            .systemBarsPadding()
    ) {
        ProfileHeader(
            username = profileState.username,
            email = profileState.userEmail
        )

        StatsCard(profileState.taskStats)

        SettingsSection(navController)
    }
}

@Composable
private fun ProfileHeader(
    username: String,
    email: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(80.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = username,
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = email,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StatColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge)
        Text(label, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
    }
}

@Composable
private fun StatDivider() {
    VerticalDivider(
        modifier = Modifier.height(64.dp),
        color = MaterialTheme.colorScheme.outlineVariant
    )
}

@Composable
private fun StatsCard(stats: TaskStats) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatColumn("Tasks\nToday", stats.todayTasksCount.toString())
            StatDivider()
            StatColumn("Completed\nToday", stats.completedTodayCount.toString())
            StatDivider()
            StatColumn("Upcoming\nTasks", stats.upcomingTasksCount.toString())
        }
    }
}

@Composable
private fun SettingsSection(navController: NavController) {
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

        Card(modifier = Modifier.fillMaxWidth()) {
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