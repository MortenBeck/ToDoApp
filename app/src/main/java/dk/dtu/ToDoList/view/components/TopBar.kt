package dk.dtu.ToDoList.view.components

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dk.dtu.ToDoList.R

@Composable
fun TopBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    navController: NavController
) {
    var isSearchActive by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF7FA7F6))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        WeatherWidget()

        AnimatedVisibility(
            visible = isSearchActive,
            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(40.dp)
                    .background(
                        color = Color.LightGray,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = searchText,
                    onValueChange = onSearchTextChange,
                    textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
                    singleLine = true
                )
            }
        }

        IconButton(
            onClick = { isSearchActive = !isSearchActive },
            modifier = Modifier.padding(start = if (isSearchActive) 8.dp else 0.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.search),
                contentDescription = "Search Icon",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}