package dk.dtu.ToDoList.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.ConnectionResult
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import dk.dtu.ToDoList.R
import dk.dtu.ToDoList.model.data.Task
import dk.dtu.ToDoList.model.repository.TaskCRUD
import dk.dtu.ToDoList.util.UserIdManager
import dk.dtu.ToDoList.view.components.BottomNavBar
import dk.dtu.ToDoList.view.components.BottomNavItem
import dk.dtu.ToDoList.view.screens.*
import dk.dtu.ToDoList.view.theme.ToDoListTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val RC_PLAY_SERVICES = 123

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

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
        // Check authentication state
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_PLAY_SERVICES) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d("MainActivity", "Google Play Services resolved")
                initializeApp()
            } else {
                Log.e("MainActivity", "Google Play Services resolution failed")
                // Handle the failure - maybe show an error message to the user
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoApp() {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val taskCRUD = remember { TaskCRUD(context) }
    val tasks = taskCRUD.getTasksFlow().collectAsState(initial = emptyList())

    Scaffold(
        bottomBar = {
            BottomNavBar(
                items = listOf(
                    BottomNavItem("Tasks", R.drawable.home_grey, R.drawable.home_black),
                    BottomNavItem("Calendar", R.drawable.calender_grey, R.drawable.calender_black),
                    BottomNavItem("Profile", R.drawable.profile_grey, R.drawable.profile_black)
                ),
                currentScreen = navController.currentBackStackEntryFlow.collectAsState(initial = navController.currentBackStackEntry).value?.destination?.route ?: "Tasks",
                onItemClick = { item ->
                    navController.navigate(item.label) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
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
                HomeScreen(
                    tasks = tasks.value,
                    onAddTask = { task ->
                        scope.launch {
                            taskCRUD.addTask(task)
                        }
                    },
                    onUpdateTask = { task ->
                        scope.launch {
                            taskCRUD.updateTask(task)
                        }
                    },
                    onDeleteTask = { taskId ->
                        scope.launch {
                            taskCRUD.deleteTask(taskId)
                        }
                    },
                    navController = navController
                )
            }
            composable("Calendar") {
                CalendarScreen(
                    tasks = tasks.value,
                    navController = navController,
                    onAddTask = { task ->
                        scope.launch {
                            taskCRUD.addTask(task)
                        }
                    },
                    onUpdateTask = { task ->
                        scope.launch {
                            taskCRUD.updateTask(task)
                        }
                    },
                    onDeleteTask = { taskId ->
                        scope.launch {
                            taskCRUD.deleteTask(taskId)
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
                        }
                    }
                )
            }
        }
    }
}