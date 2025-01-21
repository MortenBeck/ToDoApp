package dk.dtu.ToDoList.view.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

//@Composable
//fun BottomNavBar(
//    items: List<BottomNavItem>,
//    currentScreen: String,
//    onItemClick: (BottomNavItem) -> Unit
//) {
//    NavigationBar(
  //      containerColor = MaterialTheme.colorScheme.surface,
    //    contentColor = MaterialTheme.colorScheme.onSurface,
      //  tonalElevation = 8.dp
    //) {
      //  items.forEach { item ->
        //    val selected = item.label == currentScreen
          //  NavigationBarItem(
            //    selected = selected,
              //  onClick = { onItemClick(item) },
                //icon = {
                  //  Icon(
                    //    painter = painterResource(
                      //      id = if (selected) item.activeIcon else item.icon
                        //),
                       // contentDescription = item.label
                   // )
               // },
               // label = {
                 //   Text(
                   //     text = item.label,
                     //   style = MaterialTheme.typography.labelMedium
                   // )
               // },
               // colors = NavigationBarItemDefaults.colors(
                 //   selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                   // selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                   // indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                   // unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                   // unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
               // )
           // )
       // }
  //  }
//}

//data class BottomNavItem(
  //  val label: String,
   // val icon: Int,
    //val activeIcon: Int,
    //val isSelected: Boolean = false
//)