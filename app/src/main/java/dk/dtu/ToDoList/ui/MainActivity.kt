package dk.dtu.ToDoList.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dk.dtu.ToDoList.R
import dk.dtu.ToDoList.data.Task
import dk.dtu.ToDoList.data.TasksRepository
import dk.dtu.ToDoList.feature.AddToCalendarPage
import dk.dtu.ToDoList.feature.CalendarScreen
import dk.dtu.ToDoList.feature.FavouritesScreen
import dk.dtu.ToDoList.feature.HomeScreen
import dk.dtu.ToDoList.feature.ProfileScreen


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        val firebaseAuth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        setContent {

            ToDoApp()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ToDoApp() {
    val navController = rememberNavController()

    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "testuser123"

    // Create mutableTasks from TasksRepository.Tasks
    val mutableTasks = remember { mutableStateListOf<Task>() }

    LaunchedEffect(userId) {
        TasksRepository.getTasks(userId,
            onSuccess = { tasks ->
                mutableTasks.clear()
                mutableTasks.addAll(tasks)
            },
            onFailure = { exception ->
                // Handle failure (e.g., show a message to the user)
                println("Error fetching tasks: ${exception.message}")
            }
        )
    }





    Scaffold(
        bottomBar = {
            BottomNavBar(
                items = listOf(
                    BottomNavItem("Tasks", R.drawable.home_grey,R.drawable.home_black),
                    BottomNavItem("Calendar", R.drawable.calender_grey,R.drawable.calender_black),
                    BottomNavItem("Profile", R.drawable.profile_grey, R.drawable.profile_black),
                ),
                currentScreen = navController.currentBackStackEntryFlow.collectAsState(initial = navController.currentBackStackEntry).value?.destination?.route ?: "Tasks", // Get current screen
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
                    tasks = mutableTasks,
                    navController = navController,
                    userId = userId
                )
            }
            composable("Favourites") {
                FavouritesScreen(
                    tasks = mutableTasks,
                    navController = navController
                )
            }
            composable("Calendar") {
                CalendarScreen(
                    tasks = mutableTasks,
                    navController = navController
                ) // Use the shared mutable task list
            }
            composable("Profile") {
                ProfileScreen(
                    tasks = mutableTasks,
                    navController = navController
                )
            }
            composable("addToCalendar?taskName={taskName}") { backStackEntry ->
                val taskName = backStackEntry.arguments?.getString("taskName") ?: "New Task"

                AddToCalendarPage(
                    navController = navController,
                    taskName = taskName,
                    onTaskAdded = { newTask ->
                        mutableTasks.add(newTask) // Add the task to the mutable list
                    }
                )
            }
        }
    }
}

