package dk.dtu.ToDoList.view.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettingsScreen(navController: NavController) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showNotImplementedDialog by remember { mutableStateOf(false) }

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
                            imageVector = Icons.Default.ArrowBack,
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
                // Profile Card
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.elevatedCardColors()
                ) {
                    ListItem(
                        headlineContent = {
                            Column {
                                Text(
                                    "John Doe",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    "john.doe@example.com",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        leadingContent = {
                            Icon(
                                Icons.Default.AccountCircle,
                                contentDescription = "Profile",
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        modifier = Modifier.clickable { showNotImplementedDialog = true }
                    )
                }

                // Account Settings Card
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.elevatedCardColors()
                ) {
                    ListItem(
                        headlineContent = { Text("Change Password") },
                        leadingContent = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = "Change Password",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        modifier = Modifier.clickable { showNotImplementedDialog = true }
                    )
                    HorizontalDivider()

                    ListItem(
                        headlineContent = { Text("Privacy Settings") },
                        leadingContent = {
                            Icon(
                                Icons.Default.Security,
                                contentDescription = "Privacy",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        modifier = Modifier.clickable { showNotImplementedDialog = true }
                    )
                    HorizontalDivider()

                    ListItem(
                        headlineContent = { Text("Notification Preferences") },
                        leadingContent = {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        modifier = Modifier.clickable { showNotImplementedDialog = true }
                    )
                }

                // Danger Zone Card
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.elevatedCardColors()
                ) {
                    ListItem(
                        headlineContent = { Text("Logout") },
                        leadingContent = {
                            Icon(
                                Icons.Default.Logout,
                                contentDescription = "Logout",
                                tint = MaterialTheme.colorScheme.error
                            )
                        },
                        modifier = Modifier.clickable { showLogoutDialog = true }
                    )
                    HorizontalDivider()

                    ListItem(
                        headlineContent = {
                            Text(
                                "Delete Account",
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        leadingContent = {
                            Icon(
                                Icons.Default.DeleteForever,
                                contentDescription = "Delete Account",
                                tint = MaterialTheme.colorScheme.error
                            )
                        },
                        modifier = Modifier.clickable { showDeleteAccountDialog = true }
                    )
                }
            }

            // Not Implemented Dialog
            if (showNotImplementedDialog) {
                AlertDialog(
                    onDismissRequest = { showNotImplementedDialog = false },
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    title = { Text("Feature Not Available") },
                    text = { Text("This feature is not implemented yet.") },
                    confirmButton = {
                        TextButton(onClick = { showNotImplementedDialog = false }) {
                            Text("OK")
                        }
                    }
                )
            }

            // Logout Dialog
            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    icon = { Icon(Icons.Default.Logout, contentDescription = null) },
                    title = { Text("Confirm Logout") },
                    text = { Text("Are you sure you want to log out?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showLogoutDialog = false
                                // Handle logout
                            }
                        ) {
                            Text("Logout")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showLogoutDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            // Delete Account Dialog
            if (showDeleteAccountDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteAccountDialog = false },
                    icon = {
                        Icon(
                            Icons.Default.DeleteForever,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                    title = { Text("Delete Account") },
                    text = {
                        Text("Are you sure you want to delete your account? This action cannot be undone.")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDeleteAccountDialog = false
                                // Handle account deletion
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteAccountDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}