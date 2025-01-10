package dk.dtu.ToDoList.ui


import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview
import dk.dtu.ToDoList.R
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem



@Composable
fun BottomNavBar(
    items: List<BottomNavItem>,
    currentScreen: String, // Pass the current screen
    onItemClick: (BottomNavItem) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        contentColor = Color.Black
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = item.label == currentScreen, // Highlight the active screen
                onClick = { onItemClick(item) },
                icon = {
                    Icon(
                        painter = painterResource(
                            id = if (item.label == currentScreen) item.activeIcon else item.icon
                        ),
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (item.label == currentScreen) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            )
        }
    }
}


data class BottomNavItem(
    val label: String,
    val icon: Int,
    val activeIcon: Int,
    val isSelected: Boolean = false
)
