package dk.dtu.ToDoList.view.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dk.dtu.ToDoList.R



/**
 * A composable screen that displays application-level settings, such as Theme, Language, and Data Usage.
 * Each setting has a corresponding dialog for user interaction, although some features may not be fully implemented.
 *
 * @param navController A [NavController] used for navigating back or to other screens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettingsScreen(navController: NavController) {
    var showDataUsageNotification by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("English") }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Account Settings",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors()
            )
        }
    ) { paddingValues ->
        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .paint(
                        painterResource(id = R.drawable.background_gradient),
                        contentScale = ContentScale.FillBounds
                    )
                    .padding(paddingValues)
            ) {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.elevatedCardColors()
                ) {
                    // Theme Item
                    ListItem(
                        headlineContent = { Text("Theme") },
                        leadingContent = {
                            Icon(
                                Icons.Default.Palette,
                                contentDescription = "Theme",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingContent = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { showThemeDialog = true }
                            ) {
                                Text(
                                    text = "Light",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Select theme",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        },
                        modifier = Modifier.clickable { showThemeDialog = true }
                    )
                    HorizontalDivider()

                    // Language Item
                    ListItem(
                        headlineContent = { Text("Language") },
                        leadingContent = {
                            Icon(
                                Icons.Default.Language,
                                contentDescription = "Language",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingContent = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { showLanguageDialog = true }
                            ) {
                                Text(
                                    text = selectedLanguage,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Select language",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        },
                        modifier = Modifier.clickable { showLanguageDialog = true }
                    )
                    HorizontalDivider()

                    // Data Usage Item
                    ListItem(
                        headlineContent = { Text("Data Usage") },
                        leadingContent = {
                            Icon(
                                Icons.Default.DataUsage,
                                contentDescription = "Data Usage",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        modifier = Modifier.clickable { showDataUsageNotification = true }
                    )
                }
            }

            // Data Usage Notification
            if (showDataUsageNotification) {
                AlertDialog(
                    onDismissRequest = { showDataUsageNotification = false },
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    title = { Text("Feature Not Available") },
                    text = { Text("Data usage feature is not implemented yet.") },
                    confirmButton = {
                        TextButton(onClick = { showDataUsageNotification = false }) {
                            Text("OK")
                        }
                    }
                )
            }

            // Theme Dialog
            if (showThemeDialog) {
                AlertDialog(
                    onDismissRequest = { showThemeDialog = false },
                    title = { Text("Select Theme") },
                    text = {
                        Column {
                            ListItem(
                                headlineContent = { Text("Light") },
                                modifier = Modifier.clickable {
                                    // Handle theme selection
                                    showThemeDialog = false
                                }
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showThemeDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            // Language Dialog
            if (showLanguageDialog) {
                AlertDialog(
                    onDismissRequest = { showLanguageDialog = false },
                    title = { Text("Select Language") },
                    text = {
                        Column {
                            ListItem(
                                headlineContent = { Text("English") },
                                modifier = Modifier.clickable {
                                    selectedLanguage = "English"
                                    showLanguageDialog = false
                                }
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showLanguageDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}