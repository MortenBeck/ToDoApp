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
import androidx.compose.runtime.ComposableOpenTarget
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp


@Composable
fun TopBar(searchText: String, onSearchTextChange: (String) -> Unit) {
    var isSearchActive by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Profile Icon
        Image(
            painter = painterResource(id = R.drawable.profile_black),
            contentDescription = "Profile Icon",
            modifier = Modifier.size(32.dp)
        )

        // Search Bar
        AnimatedVisibility(
            visible = isSearchActive,
            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
        ) {
            BasicTextField(
                value = searchText,
                onValueChange = onSearchTextChange, // Pass the text change callback
                modifier = Modifier
                    .fillMaxWidth(0.8f) // Adjust width
                    .height(40.dp) // Adjust height
                    .background(
                        color = Color.LightGray,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp), // Inner padding
                textStyle = TextStyle(color = Color.Black),
                singleLine = true
            )
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
    MaterialTheme {
        TopBar(searchText = searchText, onSearchTextChange = { searchText = it })

    }
}
