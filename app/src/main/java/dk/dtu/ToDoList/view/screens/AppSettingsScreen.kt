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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dk.dtu.ToDoList.R
import dk.dtu.ToDoList.data.events.SettingsEvent
import dk.dtu.ToDoList.viewmodel.SettingsViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import dk.dtu.ToDoList.model.repository.TaskCRUD
import dk.dtu.ToDoList.repository.AuthRepository
import dk.dtu.ToDoList.repository.FirebaseAuthRepository
import dk.dtu.ToDoList.viewmodel.SettingsViewModelFactory

/**
 * A composable screen that displays application-level settings, such as Theme, Language, and Data Usage.
 * Each setting has a corresponding dialog for user interaction, although some features may not be fully implemented.
 *
 * @param navController A [NavController] used for navigating back or to other screens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(
            authRepository = FirebaseAuthRepository(),
            taskRepository = TaskCRUD(LocalContext.current)
        )
    )
) {
    val appState by viewModel.appSettingsState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("App Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .paint(
                    painterResource(id = R.drawable.background_gradient),
                    contentScale = ContentScale.FillBounds
                )
                .padding(padding)
        ) {
            Column {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    ListItem(
                        headlineContent = { Text("Theme") },
                        trailingContent = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    appState.theme.replaceFirstChar { it.uppercase() },
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Icon(
                                    Icons.Default.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        leadingContent = {
                            Icon(
                                Icons.Default.DarkMode,
                                "Theme",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        modifier = Modifier.clickable {
                            viewModel.onEvent(SettingsEvent.App.ToggleThemeDialog(true))
                        }
                    )
                    HorizontalDivider()

                    ListItem(
                        headlineContent = { Text("Language") },
                        trailingContent = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    appState.language.replaceFirstChar { it.uppercase() },
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Icon(
                                    Icons.Default.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        leadingContent = {
                            Icon(
                                Icons.Default.Language,
                                "Language",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        modifier = Modifier.clickable {
                            viewModel.onEvent(SettingsEvent.App.ToggleLanguageDialog(true))
                        }
                    )
                    HorizontalDivider()

                    ListItem(
                        headlineContent = { Text("Data Usage") },
                        leadingContent = {
                            Icon(
                                Icons.Default.DataUsage,
                                "Data Usage",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        modifier = Modifier.clickable {
                            viewModel.onEvent(SettingsEvent.App.ToggleDataUsageDialog(true))
                        }
                    )
                }
            }

            if (appState.showDataUsageDialog) {
                AlertDialog(
                    onDismissRequest = {
                        viewModel.onEvent(SettingsEvent.App.ToggleDataUsageDialog(false))
                    },
                    title = { Text("Feature Not Available") },
                    text = { Text("Data usage feature is not implemented yet.") },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.onEvent(SettingsEvent.App.ToggleDataUsageDialog(false))
                        }) {
                            Text("OK")
                        }
                    }
                )
            }

            if (appState.showThemeDialog) {
                AlertDialog(
                    onDismissRequest = {
                        viewModel.onEvent(SettingsEvent.App.ToggleThemeDialog(false))
                    },
                    title = { Text("Select Theme") },
                    text = {
                        Column {
                            ListItem(
                                headlineContent = { Text("Light") },
                                leadingContent = {
                                    RadioButton(
                                        selected = appState.theme == "light",
                                        onClick = {
                                            viewModel.onEvent(SettingsEvent.App.SetTheme("light"))
                                            viewModel.onEvent(SettingsEvent.App.ToggleThemeDialog(false))
                                        }
                                    )
                                }
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.onEvent(SettingsEvent.App.ToggleThemeDialog(false))
                        }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            if (appState.showLanguageDialog) {
                AlertDialog(
                    onDismissRequest = {
                        viewModel.onEvent(SettingsEvent.App.ToggleLanguageDialog(false))
                    },
                    title = { Text("Select Language") },
                    text = {
                        Column {
                            ListItem(
                                headlineContent = { Text("English") },
                                leadingContent = {
                                    RadioButton(
                                        selected = appState.language == "english",
                                        onClick = {
                                            viewModel.onEvent(SettingsEvent.App.SetLanguage("english"))
                                            viewModel.onEvent(SettingsEvent.App.ToggleLanguageDialog(false))
                                        }
                                    )
                                }
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.onEvent(SettingsEvent.App.ToggleLanguageDialog(false))
                        }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}