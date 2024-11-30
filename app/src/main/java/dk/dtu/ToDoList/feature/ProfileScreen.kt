package dk.dtu.ToDoList.feature

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dk.dtu.ToDoList.R
import dk.dtu.ToDoList.data.Task


@Composable
fun ProfileScreen(tasks: MutableList<Task>, navController: NavController) {
    // State to control the visibility of the Task Statistics section
    var showTaskStatistics by remember { mutableStateOf(false) }
    var showAddTaskDialog by remember{mutableStateOf(false)}
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Title
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Profile Picture Placeholder
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                )
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "VP",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Profile Stats
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatsItem(label = "Total Tasks", value = "12")
            StatsItem(label = "Completed", value = "8")
            StatsItem(label = "Pending", value = "4")
        }

        // Settings Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            ListItem(
                icon = R.drawable.profile_grey,
                title = "Edit Profile"
            )
            ListItem(
                icon = R.drawable.favorite_grey,
                title = "Favorites"
            )
            // "Task Statistics" button toggles visibility of expanded stats
            ListItem(
                icon = R.drawable.calender_grey,
                title = "Task Statistics",
                onClick = { showTaskStatistics = !showTaskStatistics } // Toggle the visibility
            )
        }

        // Show Task Statistics if the button is clicked
        if (showTaskStatistics) {
            // Expanded Task Statistics Section
            Text(
                text = "Task Statistics",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Priority Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatsItem(label = "High Priority", value = "3")
                StatsItem(label = "Medium Priority", value = "5")
                StatsItem(label = "Low Priority", value = "4")
            }

            // Tag Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatsItem(label = "Work", value = "5")
                StatsItem(label = "Home", value = "3")
                StatsItem(label = "School", value = "2")
                StatsItem(label = "Pet", value = "2")
            }

            // Upcoming Tasks
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Upcoming Tasks: 5",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Task Progress (Completion Percentage)
            val completionPercentage = (8 / 12f) * 100  // 8 completed out of 12 tasks
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Progress: ${completionPercentage.toInt()}% Completed",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd // Align to bottom-end
    ) {
        IconButton(
            onClick = { showDialog = true },
            modifier = Modifier.size(64.dp) // Adjust size as needed
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                shadowElevation = 6.dp
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Task",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .padding(12.dp)
                        .size(32.dp) // Icon size
                )
            }
        }
    }
    // Add Task Dialog
    if (showDialog) {
        AddTaskDialog(
            showDialog = showDialog,
            navController = navController,
            onDismiss = { showDialog = false },
            onTaskAdded = { newTask ->
                tasks.add(newTask) // Add the new task to the list
                showDialog = false
            }
        )
    }
}

@Composable
fun StatsItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ListItem(
    icon: Int,
    title: String,
    onClick: () -> Unit = {}  // Add onClick parameter to handle button clicks
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}