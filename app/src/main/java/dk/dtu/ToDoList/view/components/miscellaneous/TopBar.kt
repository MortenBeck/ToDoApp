package dk.dtu.ToDoList.view.components.miscellaneous

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.gestures.detectTapGestures


/**
 * A top bar composable that displays a dynamic search icon and an optional search text field.
 *
 * When the search icon is toggled, it slides a search field into view. Tapping anywhere else
 * (outside the search field) will hide the field if it's visible.
 *
 * @param searchText The current text in the search field.
 * @param onSearchTextChange A callback that is invoked whenever the search text changes.
 * @param navController A [NavController] that can be used for navigation if needed.
 */
@Composable
fun TopBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    navController: NavController
) {
    var isSearchActive by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            if (isSearchActive) {
                                isSearchActive = false
                                onSearchTextChange("")
                            }
                        }
                    )
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
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
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .padding(horizontal = 8.dp),
                        shape = MaterialTheme.shapes.medium,
                        color = Color.White
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            BasicTextField(
                                value = searchText,
                                onValueChange = onSearchTextChange,
                                textStyle = TextStyle(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 16.sp
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                    }
                }

                IconButton(
                    onClick = {
                        isSearchActive = !isSearchActive
                        if (!isSearchActive) {
                            onSearchTextChange("")
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Toggle search"
                    )
                }
            }
        }
    }
}
