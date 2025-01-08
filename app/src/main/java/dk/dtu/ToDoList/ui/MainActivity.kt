package dk.dtu.ToDoList.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dk.dtu.ToDoList.R
import dk.dtu.ToDoList.data.Task
import dk.dtu.ToDoList.data.TasksRepository
import dk.dtu.ToDoList.feature.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoApp()
        }
    }
}

@Composable
fun ToDoApp() {
    val navController = rememberNavController()


    // Get current user ID (using FirebaseAuth)
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "defaultUser" // Placeholder for testing

    // Create mutableTasks to store tasks fetched from Firestore
    val mutableTasks = remember { mutableStateListOf<Task>() }

    // Fetch tasks for the current user
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
                    BottomNavItem("Tasks", R.drawable.home_grey),
                    BottomNavItem("Favourites", R.drawable.favorite_grey),
                    BottomNavItem("Planned", R.drawable.calender_grey),
                    BottomNavItem("Profile", R.drawable.profile_grey),
                ),
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
                    navController = navController
                )
            }
            composable("Favourites") {
                FavouritesScreen(
                    tasks = mutableTasks,
                    navController = navController)
            }
            composable("Planned") {
                PlannedScreen(
                    tasks = mutableTasks,
                    navController = navController) // Use the shared mutable task list
            }
            composable("Profile") {
                ProfileScreen(
                    tasks = mutableTasks,
                    navController = navController)
            }
            composable("addToCalendar?taskName={taskName}") { backStackEntry ->
                val taskName = backStackEntry.arguments?.getString("taskName") ?: "New Task"

                AddToCalendarPage(
                    navController = navController,
                    taskName = taskName,
                    onTaskAdded = { newTask ->
                        mutableTasks.add(newTask) // Add the task to the mutable list
                        // Optionally, you could save this task to Firestore as well
                        TasksRepository.addTask(newTask,
                            onSuccess = { taskId ->
                                // Handle success (e.g., show a confirmation)
                                println("Task added with ID: $taskId")
                            },
                            onFailure = { exception ->
                                // Handle failure (e.g., show an error message)
                                println("Error adding task: ${exception.message}")
                            }
                        )
                    }
                )
            }
        }
    }
}
