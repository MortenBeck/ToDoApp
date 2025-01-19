package dk.dtu.ToDoList.view.components


import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults


@Composable
fun BottomNavBar(
    items: List<BottomNavItem>,
    currentScreen: String,
    onItemClick: (BottomNavItem) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        contentColor = Color.Black
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = item.label == currentScreen,
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
                        color = if (item.label == currentScreen) Color(0xFF2A4174) else Color.Gray
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xFF2A4174),
                    selectedIconColor = Color.White,  // Make the icon white when selected
                    selectedTextColor = Color(0xFF2A4174)
                )
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
