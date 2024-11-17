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
                    BottomNavItem("Tasks", R.drawable.ic_home_black_24dp, isSelected = currentScreen.value == "Tasks"),
                    BottomNavItem("Favourites", R.drawable.favorite, isSelected = currentScreen.value == "Favourites"),
                    BottomNavItem("Planned", R.drawable.favorites, isSelected = currentScreen.value == "Planned"),
                    BottomNavItem("Profile", R.drawable.favorite, isSelected = currentScreen.value == "Profile")
                ),
                onItemClick = { item ->
                    currentScreen.value = item.label
                    navController.navigate(item.label)
                }
            )
        },
        content = { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "Tasks",
                modifier = Modifier.fillMaxSize().padding(paddingValues)
            ) {
                composable("Tasks") {
                    TaskListScreen()
                }
                composable("Favourites") {
                    FavouritesScreen()
                }
                composable("Planned") {
                    PlannedScreen()
                }
                composable("Profile") {
                    ProfileScreen()
                }
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
                name = "Buy groceries",
                deadline = simpleDateFormat.parse("20-11-2024")!!,
                priority = TaskPriority.LOW,
                tag = TaskTag.PRIVATE,
                completed = false
            ),
            Task(
                name = "Prepare presentation",
                deadline = simpleDateFormat.parse("19-11-2024")!!,
                priority = TaskPriority.HIGH,
                tag = TaskTag.WORK,
                completed = false
            ),
            Task(
                name = "Morning run",
                deadline = simpleDateFormat.parse("17-11-2024")!!,
                priority = TaskPriority.LOW,
                tag = TaskTag.SPORT,
                completed = true
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

