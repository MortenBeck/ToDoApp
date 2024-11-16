package dk.dtu.ToDoList.ui

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource

import androidx.compose.material3.Icon
import androidx.compose.material3.Text

@Composable
fun BottomNavBar(
    items: List<BottomNavItem>,
    onItemClick: (BottomNavItem) -> Unit
) {
    BottomNavigation(
        backgroundColor = Color.White,
        contentColor = Color.Black
    ) {
        items.forEach { item ->
            BottomNavigationItem(
                selected = item.isSelected,
                onClick = { onItemClick(item) },
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.label
                    )
                },
                label = { Text(text = item.label) }
            )
        }
    }
}

data class BottomNavItem(
    val label: String,
    val icon: Int,
    val isSelected: Boolean = false
)

