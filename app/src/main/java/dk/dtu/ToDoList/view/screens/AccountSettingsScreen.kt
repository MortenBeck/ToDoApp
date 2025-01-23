package dk.dtu.ToDoList.view.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dk.dtu.ToDoList.R
import dk.dtu.ToDoList.data.events.SettingsEvent
import dk.dtu.ToDoList.viewmodel.SettingsViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import dk.dtu.ToDoList.data.repository.FirebaseAuthRepository
import dk.dtu.ToDoList.model.data.state.UserProfileState
import dk.dtu.ToDoList.model.repository.TaskCRUD
import dk.dtu.ToDoList.viewmodel.SettingsViewModelFactory

/**
 * A screen that displays various account settings for the current user. This includes
 * options for changing the password, privacy settings, notifications, logging out,
 * and deleting the account. The screen also shows basic user profile details (email/username).
 *
 * @param navController Used for navigating to other screens within the app (e.g., going back or returning to a login screen).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(
            authRepository = FirebaseAuthRepository(),
            taskRepository = TaskCRUD(LocalContext.current)
        )
    )
) {
    val profileState by viewModel.profileState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Account Settings") },
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
            AccountSettingsContent(
                username = profileState.username,
                email = profileState.userEmail,
                profileState = profileState,
                onEvent = viewModel::onEvent,
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
private fun AccountSettingsContent(
    username: String,
    email: String,
    profileState: UserProfileState,
    onEvent: (SettingsEvent) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ListItem(
                headlineContent = {
                    Column {
                        Text(username)
                        Text(email, style = MaterialTheme.typography.bodyMedium)
                    }
                },
                leadingContent = {
                    Icon(
                        Icons.Default.AccountCircle,
                        "Profile",
                        Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )
        }

        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            ListItem(
                headlineContent = { Text("Change Password") },
                leadingContent = {
                    Icon(
                        Icons.Default.Lock,
                        "Password",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.clickable {
                    onEvent(SettingsEvent.Profile.ToggleNotImplementedDialog(true))
                }
            )
            HorizontalDivider()

            ListItem(
                headlineContent = { Text("Privacy Settings") },
                leadingContent = {
                    Icon(
                        Icons.Default.Security,
                        "Privacy",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.clickable {
                    onEvent(SettingsEvent.Profile.ToggleNotImplementedDialog(true))
                }
            )
            HorizontalDivider()

            ListItem(
                headlineContent = { Text("Notification Preferences") },
                leadingContent = {
                    Icon(
                        Icons.Default.Notifications,
                        "Notifications",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.clickable {
                    onEvent(SettingsEvent.Profile.ToggleNotImplementedDialog(true))
                }
            )
        }

        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ListItem(
                headlineContent = { Text("Logout") },
                leadingContent = {
                    Icon(
                        Icons.AutoMirrored.Filled.Logout,
                        "Logout",
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                modifier = Modifier.clickable {
                    onEvent(SettingsEvent.Account.ToggleLogoutDialog(true))
                }
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
                        "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                modifier = Modifier.clickable {
                    onEvent(SettingsEvent.Account.ToggleDeleteDialog(true))
                }
            )
        }

        // Dialogs
        if (profileState.showNotImplementedDialog) {
            AlertDialog(
                onDismissRequest = { onEvent(SettingsEvent.Profile.ToggleNotImplementedDialog(false)) },
                title = { Text("Feature Not Available") },
                text = { Text("This feature is not implemented yet.") },
                confirmButton = {
                    TextButton(onClick = {
                        onEvent(SettingsEvent.Profile.ToggleNotImplementedDialog(false))
                    }) {
                        Text("OK")
                    }
                }
            )
        }

        if (profileState.showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { onEvent(SettingsEvent.Account.ToggleLogoutDialog(false)) },
                icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
                title = { Text("Confirm Logout") },
                text = { Text("Are you sure you want to log out?") },
                confirmButton = {
                    TextButton(onClick = { onEvent(SettingsEvent.Account.Logout) }) {
                        Text("Logout")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        onEvent(SettingsEvent.Account.ToggleLogoutDialog(false))
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (profileState.showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { onEvent(SettingsEvent.Account.ToggleDeleteDialog(false)) },
                icon = {
                    Icon(
                        Icons.Default.DeleteForever,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                title = { Text("Delete Account") },
                text = { Text("Are you sure you want to delete your account? This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = { onEvent(SettingsEvent.Account.DeleteAccount) },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        onEvent(SettingsEvent.Account.ToggleDeleteDialog(false))
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}