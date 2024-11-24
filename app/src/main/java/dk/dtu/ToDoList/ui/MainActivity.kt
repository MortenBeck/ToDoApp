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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dk.dtu.ToDoList.R
import dk.dtu.ToDoList.data.Task
import dk.dtu.ToDoList.data.TaskPriority
import dk.dtu.ToDoList.data.TaskTag
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
    val mutableTasks = remember { mutableStateListOf<Task>() } // Use a state list for recomposition
    val combinedTasks = remember { mutableStateListOf<Task>() }

    LaunchedEffect(Unit) {
        combinedTasks.addAll(TasksRepository.Tasks)
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                items = listOf(
                    BottomNavItem(
                        label = "Tasks",
                        icon = R.drawable.home_grey,
                        isSelected = true
                    ),
                    BottomNavItem(
                        label = "Favourites",
                        icon = R.drawable.favorite_grey
                    ),
                    BottomNavItem(
                        label = "Planned",
                        icon = R.drawable.calender_grey
                    ),
                    BottomNavItem(
                        label = "Profile",
                        icon = R.drawable.profile_grey
                    )
                ),
                onItemClick = { item ->
                    navController.navigate(item.label)
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
                    tasks = combinedTasks, // Pass combined list of tasks
                    mutableTasks = mutableTasks,
                    navController = navController
                )
            }
            composable("AddToCalendar/{taskName}") { backStackEntry ->
                val taskName = backStackEntry.arguments?.getString("taskName") ?: "Untitled Task"
                AddToCalendarPage(
                    navController = navController,
                    taskName = taskName,
                    onTaskAdded = { newTask ->
                        combinedTasks.add(newTask) // Add task to the combined list
                    }
                )
            }
        }
    }
}