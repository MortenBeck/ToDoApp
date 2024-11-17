package dk.dtu.ToDoList.ui

import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dk.dtu.ToDoList.R
import androidx.compose.material3.Text
import dk.dtu.ToDoList.data.Task
import dk.dtu.ToDoList.data.TaskTag
import dk.dtu.ToDoList.data.TaskPriority
import dk.dtu.ToDoList.data.TasksRepository.simpleDateFormat
import dk.dtu.ToDoList.feature.TaskList
import androidx.compose.ui.unit.dp


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
    val currentScreen = remember { mutableStateOf("Tasks") }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                items = listOf(
                    BottomNavItem(
                        label = "Tasks",
                        icon = R.drawable.home_grey,
                        activeIcon = R.drawable.home_black,
                        isSelected = currentScreen.value == "Tasks"
                    ),
                    BottomNavItem(
                        label = "Favourites",
                        icon = R.drawable.favorite_grey,
                        activeIcon = R.drawable.favorite_black,
                        isSelected = currentScreen.value == "Favourites"
                    ),
                    BottomNavItem(
                        label = "Planned",
                        icon = R.drawable.calender_grey,
                        activeIcon = R.drawable.calender_black,
                        isSelected = currentScreen.value == "Planned"
                    ),
                    BottomNavItem(
                        label = "Profile",
                        icon = R.drawable.profile_grey,
                        activeIcon = R.drawable.profile_black,
                        isSelected = currentScreen.value == "Profile"
                    )
                ),
                onItemClick = { item ->
                    currentScreen.value = item.label
                    navController.navigate(item.label) {
                        // Avoid multiple copies of the same destination on back stack
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        },
        content = { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "Tasks",
                modifier = Modifier.fillMaxSize().padding(paddingValues)
            ) {
                composable("Tasks") { TaskListScreen() }
                composable("Favourites") { FavouritesScreen() }
                composable("Planned") { PlannedScreen() }
                composable("Profile") { ProfileScreen() }
            }
        }
    )
}



@Composable
fun TaskListScreen() {
    TaskList(
        Tasks = listOf(
            Task(
                name = "Homework - UX",
                deadline = simpleDateFormat.parse("17-11-2024")!!,
                priority = TaskPriority.HIGH,
                tag = TaskTag.SCHOOL,
                completed = false
            ),
            Task(
                name = "Fix project at work",
                deadline = simpleDateFormat.parse("18-11-2024")!!,
                priority = TaskPriority.MEDIUM,
                tag = TaskTag.WORK,
                completed = true
            ),
            Task(
                name = "Walk the dog",
                deadline = simpleDateFormat.parse("17-11-2024")!!,
                priority = TaskPriority.MEDIUM,
                tag = TaskTag.PET,
                completed = false
            ),
            Task(
                name = "Cancel Netflix subscription",
                deadline = simpleDateFormat.parse("17-11-2024")!!,
                priority = TaskPriority.LOW,
                tag = TaskTag.HOME,
                completed = false
        )
    )
    )
}

@Composable
fun FavouritesScreen() {
    Text(
        text = "Favourites Screen",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    )
}

@Composable
fun PlannedScreen() {
    Text(
        text = "Planned Screen",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    )
}

@Composable
fun ProfileScreen() {
    Text(
        text = "Profile Screen",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    )
}

