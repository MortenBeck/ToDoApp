package dk.dtu.ToDoList.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dk.dtu.ToDoList.R

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
                    BottomNavItem("Profile", R.drawable.favorite, isSelected = currentScreen.value == "Profile")
                ),
                onItemClick = { item ->
                    currentScreen.value = item.label
                    navController.navigate(item.label)
                }
            )
        },
        content = { paddingValues ->
            // Handle padding to avoid UI being hidden behind the bottom navigation
            NavHost(
                navController = navController,
                startDestination = "Tasks",
                modifier = Modifier.fillMaxSize().padding(paddingValues)
            ) {
                composable("Tasks") {
                    TaskListScreen()
                }
                composable("Profile") {
                    ProfileScreen() // Replace with actual Profile screen composable
                }
            }
        }
    )
}

@Composable
fun TaskListScreen() {
    // Example Task List
    LazyColumn {
        items(20) { index ->
            Text("Task #$index", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun ProfileScreen() {
    // Placeholder for Profile screen
    Text("Profile Screen", style = MaterialTheme.typography.bodyLarge)
}
