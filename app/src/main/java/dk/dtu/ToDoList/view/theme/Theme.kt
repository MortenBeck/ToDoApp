package dk.dtu.ToDoList.view.theme

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Blue500,
    secondary = Green500,
    tertiary = Amber500,
            background = Color(0xFFE2EFF5)
)

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun ToDoListTheme(
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (dynamicColor) {
        val context = LocalContext.current
        dynamicLightColorScheme(context)
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
