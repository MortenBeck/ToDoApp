package dk.dtu.ToDoList.view

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dk.dtu.ToDoList.R
import dk.dtu.ToDoList.model.data.Task
import dk.dtu.ToDoList.model.repository.TasksRepository
import dk.dtu.ToDoList.view.components.BottomNavBar
import dk.dtu.ToDoList.view.components.BottomNavItem
import dk.dtu.ToDoList.view.screens.AddToCalendarPage
import dk.dtu.ToDoList.view.screens.CalendarScreen
import dk.dtu.ToDoList.view.screens.FavouritesScreen
import dk.dtu.ToDoList.view.screens.HomeScreen
import dk.dtu.ToDoList.view.screens.ProfileScreen
import dk.dtu.ToDoList.view.theme.ToDoListTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ToDoListTheme {
                    ToDoApp()
                }
            }
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
                    navController = navController
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
                )
            }
            composable("Profile") {
                ProfileScreen(
                    navController = navController
                )
            }
            composable("addToCalendar?taskName={taskName}") { backStackEntry ->
                val taskName = backStackEntry.arguments?.getString("taskName") ?: "New Task"

                AddToCalendarPage(
                    navController = navController,
                    taskName = taskName,
                    onTaskAdded = { newTask ->
                        mutableTasks.add(newTask)
                    }
                )
            }
        }
    }
}

