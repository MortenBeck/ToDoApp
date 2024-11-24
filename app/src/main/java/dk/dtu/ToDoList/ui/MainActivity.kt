package dk.dtu.ToDoList.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dk.dtu.ToDoList.R
import dk.dtu.ToDoList.data.Task
import dk.dtu.ToDoList.data.TaskTag
import dk.dtu.ToDoList.data.TaskPriority
import dk.dtu.ToDoList.data.TasksRepository.simpleDateFormat
import dk.dtu.ToDoList.feature.TaskList
import dk.dtu.ToDoList.feature.BottomNavBar
import dk.dtu.ToDoList.feature.BottomNavItem
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import java.time.ZoneId
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Icon
import dk.dtu.ToDoList.data.TasksRepository.Tasks
import dk.dtu.ToDoList.data.TasksRepository.todayTasks
import dk.dtu.ToDoList.feature.FavouritesScreen
import dk.dtu.ToDoList.feature.HomeScreen
import dk.dtu.ToDoList.feature.PlannedScreen
import dk.dtu.ToDoList.feature.ProfileScreen
import dk.dtu.ToDoList.feature.TaskListScreen
import dk.dtu.ToDoList.feature.TopBar


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

    // Convert Tasks to MutableList
    val mutableTasks = Tasks.toMutableList()  // Make sure it's a mutable list

    Scaffold(
        bottomBar = {
            BottomNavBar(
                items = listOf(
                    BottomNavItem(
                        label = "Tasks",
                        icon = R.drawable.home_grey,
                        isSelected = currentScreen.value == "Tasks"
                    ),
                    BottomNavItem(
                        label = "Favourites",
                        icon = R.drawable.favorite_grey,
                        isSelected = currentScreen.value == "Favourites"
                    ),
                    BottomNavItem(
                        label = "Planned",
                        icon = R.drawable.calender_grey,
                        isSelected = currentScreen.value == "Planned"
                    ),
                    BottomNavItem(
                        label = "Profile",
                        icon = R.drawable.profile_grey,
                        isSelected = currentScreen.value == "Profile"
                    )
                ),
                onItemClick = { item ->
                    currentScreen.value = item.label
                    navController.navigate(item.label) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
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
            composable("Tasks") { HomeScreen(tasks = mutableTasks) }  // Pass mutable list here
            composable("Favourites") { FavouritesScreen() }
            composable("Planned") { PlannedScreen() }
            composable("Profile") { ProfileScreen() }
        }
    }
}

