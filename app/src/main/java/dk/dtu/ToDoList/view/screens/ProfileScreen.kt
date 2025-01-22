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
import com.google.firebase.auth.FirebaseAuth
import java.util.*



/**
 * A screen presenting the user's profile information, including username and email,
 * as well as some basic task statistics (today's tasks, completed tasks, and upcoming tasks).
 *
 * It also includes navigable settings items that lead to [AccountSettingsScreen] and [AppSettingsScreen].
 *
 * @param navController Used for navigating to other screens, such as account or app settings.
 */
@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val taskCRUD = remember { TaskCRUD(context) }
    val scope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()

    // Handle user information
    val isAnonymous = remember { auth.currentUser?.isAnonymous ?: true }
    val userEmail = remember {
        if (isAnonymous) {
            "anonymous@email.com"
        } else {
            auth.currentUser?.email ?: "anonymous@email.com"
        }
    }
    val username = remember {
        if (isAnonymous) {
            "Anonymous"
        } else {
            auth.currentUser?.email?.substringBefore('@') ?: "Anonymous"
        }
    }

    // State for task statistics
    var todayTasksCount by remember { mutableIntStateOf(0) }
    var completedTodayCount by remember { mutableIntStateOf(0) }
    var upcomingTasksCount by remember { mutableIntStateOf(0) }

    // Use Flow to observe tasks
    LaunchedEffect(key1 = true) {
        // Create a flow to observe all tasks
        taskCRUD.observeTasks()
            .collect { tasks ->
                // Get today's start and end timestamps
                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val tomorrow = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                // Filter tasks for today
                val todayTasks = tasks.filter { task ->
                    task.deadline >= today.time && task.deadline < tomorrow.time
                }

                // Update statistics
                todayTasksCount = todayTasks.size
                completedTodayCount = todayTasks.count { it.completed }

                // Calculate upcoming tasks (excluding today's tasks and completed ones)
                upcomingTasksCount = tasks.count { task ->
                    !task.completed && task.deadline >= tomorrow.time
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painterResource(id = R.drawable.background_gradient),
                contentScale = ContentScale.FillBounds
            )
            .systemBarsPadding()
    ) {
        // Profile Header
        ProfileHeader(
            username = username,
            email = userEmail
        )

        // Stats Card displaying user task statistics
        StatsCard(
            todayTasksCount = todayTasksCount,
            completedTodayCount = completedTodayCount,
            upcomingTasksCount = upcomingTasksCount
        )

        // Settings Section
        SettingsSection(navController)
    }
}


/**
 * A private composable showing a user's avatar placeholder (a [Person] icon),
 * username, and email address.
 *
 * @param username The display name of the user.
 * @param email The user's email address.
 */
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
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = email,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


/**
 * A private composable showing a card containing three basic task statistics:
 * tasks scheduled for today, tasks completed today, and upcoming tasks.
 *
 * @param todayTasksCount The number of tasks scheduled for the current day.
 * @param completedTodayCount The number of tasks completed on the current day.
 * @param upcomingTasksCount The number of tasks scheduled for future days (excluding today).
 */
@Composable
private fun StatsCard(
    todayTasksCount: Int,
    completedTodayCount: Int,
    upcomingTasksCount: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatColumn("Tasks\nToday", todayTasksCount.toString())
            StatDivider()
            StatColumn("Completed\nToday", completedTodayCount.toString())
            StatDivider()
            StatColumn("Upcoming\nTasks", upcomingTasksCount.toString())
        }
    }
}

/**
 * A private composable that shows a single statistic column with a label and a value.
 *
 * @param label A short title describing the statistic (e.g., "Tasks Today").
 * @param value A string representing the numeric value of that statistic.
 */
@Composable
private fun StatColumn(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}


/**
 * A private composable acting as a vertical divider between statistics columns.
 */
@Composable
private fun StatDivider() {
    HorizontalDivider(
        modifier = Modifier
            .height(40.dp)
            .width(1.dp),
        color = MaterialTheme.colorScheme.outlineVariant
    )
}


/**
 * A private composable section listing settings options, each represented by a [SettingsItem].
 * Navigates to "account_settings" and "app_settings" when corresponding items are clicked.
 *
 * @param navController A [NavController] used to navigate to the respective settings screens.
 */
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
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Column {
                SettingsItem(
                    icon = Icons.Default.Person,
                    text = "Account settings",
                    onClick = { navController.navigate("account_settings") }
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                SettingsItem(
                    icon = Icons.Default.Settings,
                    text = "App settings",
                    onClick = { navController.navigate("app_settings") }
                )
            }
        }
    }
}