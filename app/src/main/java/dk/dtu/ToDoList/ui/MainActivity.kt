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
            composable("Tasks") { TaskListScreen() }
            composable("Favourites") { FavouritesScreen() }
            composable("Planned") { PlannedScreen() }
            composable("Profile") { ProfileScreen() }
        }
    }
}

@Composable
fun TaskListScreen() {
    // Define today's tasks
    val todayTasks = remember {
        listOf(
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
    }


    // Define future tasks
    val futureTasks = remember {
        listOf(
            Task(
                name = "Call mechanic",
                deadline = simpleDateFormat.parse("18-11-2024")!!,
                priority = TaskPriority.HIGH,
                tag = TaskTag.TRANSPORT,
                completed = false
            ),
            Task(
                name = "Grocery Shopping",
                deadline = simpleDateFormat.parse("18-11-2024")!!,
                priority = TaskPriority.MEDIUM,
                tag = TaskTag.HOME,
                completed = false
            ),
            Task(
                name = "Reorganize desk at work",
                deadline = simpleDateFormat.parse("18-11-2024")!!,
                priority = TaskPriority.LOW,
                tag = TaskTag.WORK,
                completed = false
            ),
            Task(
                name = "Clean bathroom",
                deadline = simpleDateFormat.parse("19-11-2024")!!,
                priority = TaskPriority.MEDIUM,
                tag = TaskTag.HOME,
                completed = false
            ),
            Task(
                name = "Get ready for album drop",
                deadline = simpleDateFormat.parse("21-11-2024")!!,
                priority = TaskPriority.LOW,
                tag = TaskTag.HOME,
                completed = false
            ),
            Task(
                name = "Homework - Math",
                deadline = simpleDateFormat.parse("22-11-2024")!!,
                priority = TaskPriority.HIGH,
                tag = TaskTag.SCHOOL,
                completed = false
            ),
            Task(
                name = "Find passport",
                deadline = simpleDateFormat.parse("31-11-2024")!!,
                priority = TaskPriority.MEDIUM,
                tag = TaskTag.HOME,
                completed = false
            ),
            Task(
                name = "Research christmas gifts",
                deadline = simpleDateFormat.parse("12-12-2024")!!,
                priority = TaskPriority.LOW,
                tag = TaskTag.HOME,
                completed = false
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // App Title
        Text(
            text = "To-Do List",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Today's Tasks Section
        Text(
            text = "Today",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TaskList(
            Tasks = todayTasks,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.padding(vertical = 12.dp))

        // Future Tasks Section
        Text(
            text = "Future",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TaskList(
            Tasks = futureTasks,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun FavouritesScreen() {
    Text(
        text = "Favourites Screen",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    )

    val favouriteTasks = remember (
        Task(
        name = "Walk the dog",
        deadline = simpleDateFormat.parse("17-11-2024")!!,
        priority = TaskPriority.MEDIUM,
        tag = TaskTag.PET,
        completed = false
    ),
        Task(
            name = "Grocery Shopping",
            deadline = simpleDateFormat.parse("18-11-2024")!!,
            priority = TaskPriority.MEDIUM,
            tag = TaskTag.HOME,
            completed = false
        ),
        Task(
            name = "Research christmas gifts",
            deadline = simpleDateFormat.parse("12-12-2024")!!,
            priority = TaskPriority.LOW,
            tag = TaskTag.HOME,
            completed = false
        )
    )
}

@Composable
fun PlannedScreen() {
    Text(
        text = "Planned Screen",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    )
}

@Composable
fun ProfileScreen() {
    Text(
        text = "Profile Screen",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    )
}
