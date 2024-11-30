package dk.dtu.ToDoList.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

    // Create mutableTasks from TasksRepository.Tasks
    val mutableTasks = remember { mutableStateListOf<Task>().apply { addAll(TasksRepository.Tasks) } }

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
                    tasks = mutableTasks, // Pass the task list
                    navController = navController // Pass the navigation controller
                )
            }
            composable("Planned") {
                PlannedScreen(
                    tasks = mutableTasks, // Pass the task list
                    navController = navController // Pass the navigation controller
                )
            }
            composable("Profile") {
                ProfileScreen(
                    navController = navController // Pass the navigation controller
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
