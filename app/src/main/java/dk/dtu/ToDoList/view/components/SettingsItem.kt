package dk.dtu.ToDoList.view.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.Role


/**
 * A composable function representing a single settings row item,
 * typically used within a list or screen of settings/preferences.
 *
 * @param icon The [ImageVector] displayed at the start of the row (e.g., a settings icon).
 * @param text The text to display next to the icon.
 * @param onClick A callback invoked when the item is clicked.
 * @param modifier The [Modifier] to be applied to the parent [Surface].
 * @param enabled Whether the item is enabled or not. If `false`, the item is displayed
 * in a visually "disabled" state and the click action is inactive.
 */
@Composable
fun SettingsItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .clickable(
                    onClick = onClick,
                    enabled = enabled,
                    role = Role.Button
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .heightIn(min = 48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = if (enabled) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = if (enabled) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}