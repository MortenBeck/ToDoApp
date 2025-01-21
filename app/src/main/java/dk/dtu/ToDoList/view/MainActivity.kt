package dk.dtu.ToDoList.view

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.ConnectionResult
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import dk.dtu.ToDoList.R
import dk.dtu.ToDoList.model.repository.TaskCRUD
import dk.dtu.ToDoList.util.UserIdManager
import dk.dtu.ToDoList.view.screens.*
import dk.dtu.ToDoList.view.theme.ToDoListTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val RC_PLAY_SERVICES = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        // Enable edge-to-edge
        enableEdgeToEdge()

        // Check Google Play Services
        checkGooglePlayServices()
    }

    private fun checkGooglePlayServices() {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this)

        if (resultCode != ConnectionResult.SUCCESS) {
            Log.e("MainActivity", "Google Play Services check failed: ${googleApiAvailability.getErrorString(resultCode)}")
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(this, resultCode, RC_PLAY_SERVICES)?.show()
            }
        } else {
            Log.d("MainActivity", "Google Play Services check passed")
            initializeApp()
        }
    }

    private fun initializeApp() {
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            lifecycleScope.launch {
                try {
                    UserIdManager.signInAnonymously()
                    Log.d("MainActivity", "Anonymous sign-in successful")
                    startApp()
                } catch (e: Exception) {
                    Log.e("MainActivity", "Authentication failed", e)
                    e.printStackTrace()
                }
            }
        } else {
            Log.d("MainActivity", "User already signed in: ${auth.currentUser?.uid}")
            startApp()
        }
    }

    private fun startApp() {
        setContent {
            ToDoListTheme {
                ToDoApp()
            }
        }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_PLAY_SERVICES) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d("MainActivity", "Google Play Services resolved")
                initializeApp()
            } else {
                Log.e("MainActivity", "Google Play Services resolution failed")
            }
        }
    }
}

@Composable
fun ToDoApp() {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val taskCRUD = remember { TaskCRUD(context) }
    val tasks = taskCRUD.getTasksFlow().collectAsState(initial = emptyList())

    // Navigation state
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: "Tasks"

    // Scaffold state for snackbar
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                tonalElevation = 3.dp
            ) {
                val items = listOf(
                    Triple("Tasks", R.drawable.home_grey, R.drawable.home_black),
                    Triple("Calendar", R.drawable.calender_grey, R.drawable.calender_black),
                    Triple("Profile", R.drawable.profile_grey, R.drawable.profile_black)
                )

                items.forEach { (route, unselectedIcon, selectedIcon) ->
                    NavigationBarItem(
                        selected = currentRoute == route,
                        onClick = {
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(
                                    if (currentRoute == route) selectedIcon else unselectedIcon
                                ),
                                contentDescription = route,
                                tint = if (currentRoute == route)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        label = {
                            Text(
                                text = route,
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "Tasks",
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            composable("Tasks") {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    HomeScreen(
                        tasks = tasks.value,
                        onAddTask = { task ->
                            scope.launch {
                                Log.d("MainActivity", "Starting task addition from HomeScreen")
                                if (task.recurrence != null) {
                                    taskCRUD.addTaskWithRecurrence(task)
                                } else {
                                    taskCRUD.addTask(task)
                                }
                                snackbarHostState.showSnackbar("Task added successfully")
                            }
                        },
                        onUpdateTask = { task ->
                            scope.launch {
                                if (task.recurringGroupId != null) {
                                    taskCRUD.updateRecurringGroup(task)
                                } else {
                                    taskCRUD.updateTask(task)
                                }
                                snackbarHostState.showSnackbar("Task updated successfully")
                            }
                        },
                        onDeleteTask = { taskId ->
                            scope.launch {
                                taskCRUD.deleteTask(taskId)
                                snackbarHostState.showSnackbar(
                                    message = "Task deleted",
                                    actionLabel = "Undo",
                                    duration = SnackbarDuration.Long
                                )
                            }
                        },
                        onDeleteRecurringGroup = { groupId ->
                            scope.launch {
                                taskCRUD.deleteRecurringGroup(groupId)
                                snackbarHostState.showSnackbar(
                                    message = "Recurring task series deleted",
                                    actionLabel = "Undo",
                                    duration = SnackbarDuration.Long
                                )
                            }
                        },
                        navController = navController
                    )
                }
            }

            composable("Calendar") {
                CalendarScreen(
                    tasks = tasks.value,
                    navController = navController,
                    onAddTask = { task ->
                        scope.launch {
                            if (task.recurrence != null) {
                                taskCRUD.addTaskWithRecurrence(task)
                            } else {
                                taskCRUD.addTask(task)
                            }
                            snackbarHostState.showSnackbar("Event added to calendar")
                        }
                    },
                    onUpdateTask = { task ->
                        scope.launch {
                            if (task.recurringGroupId != null) {
                                taskCRUD.updateRecurringGroup(task)
                            } else {
                                taskCRUD.updateTask(task)
                            }
                            snackbarHostState.showSnackbar("Event updated")
                        }
                    },
                    onDeleteTask = { taskId ->
                        scope.launch {
                            taskCRUD.deleteTask(taskId)
                            snackbarHostState.showSnackbar("Event deleted")
                        }
                    }
                )
            }
            composable("Profile") {
                ProfileScreen(
                    navController = navController
                )
            }
            composable("account_settings") {
                AccountSettingsScreen(
                    navController = navController
                )
            }
            composable("app_settings") {
                AppSettingsScreen(
                    navController = navController
                )
            }
            composable("addToCalendar?taskName={taskName}") { backStackEntry ->
                val taskName = backStackEntry.arguments?.getString("taskName") ?: "New Task"
                AddToCalendarPage(
                    navController = navController,
                    taskName = taskName,
                    onTaskAdded = { newTask ->
                        scope.launch {
                            taskCRUD.addTask(newTask)
                            snackbarHostState.showSnackbar("Task added to calendar")
                        }
                    }
                )
            }
        }
    }
}