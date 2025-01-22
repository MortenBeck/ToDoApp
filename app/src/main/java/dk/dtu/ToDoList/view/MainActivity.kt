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
import dk.dtu.ToDoList.viewmodel.HomeScreenViewModel
import dk.dtu.ToDoList.viewmodel.TaskListViewModel



/**
 * The main [Activity] for the application. Responsible for:
 * 1. Initializing Firebase,
 * 2. Checking Google Play Services availability,
 * 3. Handling user authentication (anonymous sign-in if necessary),
 * 4. Setting up the [ToDoApp] composable once everything is ready.
 */
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


    /**
     * Checks for Google Play Services availability. If an error is resolvable,
     * shows a dialog for the user to resolve it. Otherwise, logs the failure.
     */
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


    /**
     * Called after Google Play Services check passes. Ensures Firebase Authentication
     * is set up. If no user is logged in, signs in anonymously. Then starts the app UI.
     */
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


    /**
     * Sets the content view with the [ToDoApp] composable, effectively launching
     * the main UI of the application.
     */
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


/**
 * The main composable function for the ToDoList application. Sets up:
 * - A [NavHost] with multiple destinations: Tasks, Calendar, Profile, etc.
 * - A bottom navigation bar ([NavigationBar]) for switching between routes.
 * - Observes tasks from [TaskCRUD] as a [State], passing updated lists to relevant screens.
 * - A [SnackbarHostState] for displaying temporary messages (e.g., onAddTask success).
 */
@Composable
fun ToDoApp() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    // Get context and instantiate TaskCRUD
    val context = LocalContext.current
    val taskCRUD = remember { TaskCRUD(context) }

    // Instantiate ViewModels
    val taskListViewModel = remember { TaskListViewModel(taskCRUD) }
    val homeScreenViewModel = remember { HomeScreenViewModel() }

    // CoroutineScope for calling suspend functions
    val coroutineScope = rememberCoroutineScope()

    // Navigation state
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: "Tasks"

    // Collect tasks as a flow
    val tasks by taskListViewModel.tasks.collectAsState(emptyList())

    // Start Scaffold
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
                        taskListViewModel = taskListViewModel,
                        homeScreenViewModel = homeScreenViewModel,
                        navController = navController,
                        onAddTask = { task ->
                            coroutineScope.launch {
                                taskListViewModel.addTask(task)
                                snackbarHostState.showSnackbar("Task added successfully")
                            }
                        },
                        onUpdateTask = { task ->
                            coroutineScope.launch {
                                taskListViewModel.updateTask(task)
                                snackbarHostState.showSnackbar("Task updated successfully")
                            }
                        },
                        onDeleteTask = { taskId ->
                            coroutineScope.launch {
                                taskListViewModel.deleteTask(taskId)
                                snackbarHostState.showSnackbar("Task deleted successfully")
                            }
                        },
                        onDeleteRecurringGroup = { groupId ->
                            coroutineScope.launch {
                                taskListViewModel.deleteRecurringGroup(groupId)
                                snackbarHostState.showSnackbar("Recurring task group deleted")
                            }
                        }
                    )
                }
            }

            composable("Calendar") {
                CalendarScreen(
                    tasks = tasks,
                    navController = navController,
                    onAddTask = { task ->
                        coroutineScope.launch {
                            taskListViewModel.addTask(task)
                        }
                    },
                    onUpdateTask = { task ->
                        coroutineScope.launch {
                            taskListViewModel.updateTask(task)
                        }
                    },
                    onDeleteTask = { taskId ->
                        coroutineScope.launch {
                            taskListViewModel.deleteTask(taskId)
                        }
                    }
                )
            }

            composable("Profile") {
                ProfileScreen(navController = navController)
            }

            composable("account_settings") {
                AccountSettingsScreen(navController = navController)
            }

            composable("app_settings") {
                AppSettingsScreen(navController = navController)
            }

            composable("addToCalendar?taskName={taskName}") { backStackEntry ->
                val taskName = backStackEntry.arguments?.getString("taskName") ?: "New Task"
                AddToCalendarPage(
                    navController = navController,
                    taskName = taskName,
                    onTaskAdded = { newTask ->
                        coroutineScope.launch {
                            taskListViewModel.addTask(newTask)
                        }
                    }
                )
            }
        }
    }
}
