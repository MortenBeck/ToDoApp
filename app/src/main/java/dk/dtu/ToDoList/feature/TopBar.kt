package dk.dtu.ToDoList.feature

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.res.painterResource
import dk.dtu.ToDoList.R
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.IconButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp


@Composable
fun TopBar(searchText: String,
           onSearchTextChange: (String) -> Unit,
           onProfileClick: () -> Unit) {
    var isSearchActive by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Profile Icon
        IconButton(onClick = onProfileClick) {
            Image(
                painter = painterResource(id = R.drawable.profile_black),
                contentDescription = "Profile Icon",
                modifier = Modifier.size(32.dp)
            )
        }

        // Search Bar
        AnimatedVisibility(
            visible = isSearchActive,
            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(40.dp)
                    .background(
                        color = Color.LightGray,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp), // Outer padding for the box
                contentAlignment = Alignment.CenterStart // Center content vertically and align text to the start
            ) {
                BasicTextField(
                    value = searchText,
                    onValueChange = onSearchTextChange,
                    textStyle = TextStyle(color = Color.Black, fontSize = 16.sp), // Ensure proper text styling
                    singleLine = true
                )
            }
        }

        // Search Icon Button
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

@Preview(showBackground = true)
@Composable
private fun TopBarPreview(){
    var searchText by remember { mutableStateOf("") }

    TopBar(searchText = searchText,
        onSearchTextChange = {searchText = it},
        onProfileClick = {})
}
