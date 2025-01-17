package dk.dtu.ToDoList.view.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dk.dtu.ToDoList.R
import dk.dtu.ToDoList.view.components.SettingsItem
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettingsScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painterResource(id = R.drawable.background_gradient),
                contentScale = ContentScale.FillBounds
            )
    ) {
        TopAppBar(
            title = { Text("Account Settings") },
            navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }
            }
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp
        ) {
            Column {
                SettingsItem(
                    icon = Icons.Default.Palette,
                    text = "Theme",
                    onClick = { /* Handle theme settings */ }
                )
                HorizontalDivider()
                SettingsItem(
                    icon = Icons.Default.Language,
                    text = "Language",
                    onClick = { /* Handle language settings */ }
                )
                HorizontalDivider()
                SettingsItem(
                    icon = Icons.Default.DataUsage,
                    text = "Data Usage",
                    onClick = { /* Handle data settings */ }
                )
            }
        }
    }
}
