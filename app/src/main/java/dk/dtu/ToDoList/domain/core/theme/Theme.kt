package dk.dtu.ToDoList.domain.core.theme

import android.app.Activity
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import dk.dtu.ToDoList.domain.model.TaskPriority
import dk.dtu.ToDoList.domain.model.TaskTag

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4A89DC),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD6E2FF),
    onPrimaryContainer = Color(0xFF001947),

    secondary = Color(0xFF575E71),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFDBE2F9),
    onSecondaryContainer = Color(0xFF141B2C),

    tertiary = Color(0xFF715573),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFBD7FC),
    onTertiaryContainer = Color(0xFF29132D),

    error = Color(0xFFED5565),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    background = Color(0xFFE6E6F1),
    onBackground = Color(0xFF1B1B1F),

    surface = Color(0xFFFEFBFF),
    onSurface = Color(0xFF1B1B1F),
    surfaceVariant = Color(0xFFE1E2EC),
    onSurfaceVariant = Color(0xFF44474F),

    outline = Color(0xFF83868A),
    outlineVariant = Color(0xFFC4C6D0),

    // Surface containers for elevation
    surfaceContainerHighest = Color(0xFFE3E2E6),
    surfaceContainerHigh = Color(0xFFE9E8EC),
    surfaceContainer = Color(0xFFEFEDF1),
    surfaceContainerLow = Color(0xFFF5F3F7),
    surfaceContainerLowest = Color(0xFFFFFFFF)
)


/**
 * The main theme for the ToDoList application.
 * Applies a custom [LightColorScheme] and configures the status bar appearance.
 *
 * @param content A composable lambda representing the UI content that will inherit this theme.
 */
@Composable
fun ToDoListTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}


/**
 * Returns a color associated with a given [TaskTag].
 *
 * @param tag The [TaskTag] for which color is requested (e.g., WORK, SCHOOL).
 * @return A [Color] that visually represents the specified tag.
 */
@Composable
fun getTaskColor(tag: TaskTag): Color {
    return when (tag) {
        TaskTag.WORK -> Color(0xFF5D9CEC)
        TaskTag.SCHOOL -> Color(0xFFFC6E51)
        TaskTag.SPORT -> Color(0xFFAC92EC)
        TaskTag.TRANSPORT -> Color(0xFFFFCE54)
        TaskTag.PET -> Color(0xFFA0D468)
        TaskTag.HOME -> Color(0xFFCCD1D9)
        TaskTag.PRIVATE -> Color(0xFFED5565)
        TaskTag.SOCIAL -> Color(0xFF48CFAD)
    }
}


/**
 * Returns a color corresponding to the priority level of a task.
 *
 * @param priority The [TaskPriority] (HIGH, MEDIUM, LOW).
 * @return A [Color] that visually indicates the priority level.
 */
@Composable
fun getPrioColor(priority: TaskPriority): Color {
    return when (priority){
        TaskPriority.HIGH -> Color(0xFFDA4453)
        TaskPriority.MEDIUM -> Color(0xFFF6BB42)
        TaskPriority.LOW -> Color(0xFF4A89DC)
    }
}