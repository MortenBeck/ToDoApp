package dk.dtu.ToDoList.ui


import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview
import dk.dtu.ToDoList.R
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem



@Composable
fun BottomNavBar(
    items: List<BottomNavItem>,
    onItemClick: (BottomNavItem) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        contentColor = Color.Black
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = item.isSelected,
                onClick = { onItemClick(item) },
                icon = {
                    Icon(
                        painter = painterResource(
                            id = if (item.isSelected) item.activeIcon else item.icon
                        ),
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
    val activeIcon: Int,
    val isSelected: Boolean = false
)




@Preview(showBackground = true)
@Composable
fun BottomNavBarPreview() {
    val items = listOf(
        BottomNavItem("Home", R.drawable.home_black, R.drawable.home_black, isSelected = true),
        BottomNavItem("Favourites", R.drawable.favorite_black, R.drawable.favorite_black),
        BottomNavItem("Calendar", R.drawable.calender_black, R.drawable.calender_black)
    )
    BottomNavBar(items = items, onItemClick = {})
}
