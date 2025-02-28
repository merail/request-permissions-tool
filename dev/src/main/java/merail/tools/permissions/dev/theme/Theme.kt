package merail.tools.permissions.dev.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun RequestPermissionsToolTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            onPrimary = Color.White,
        ),
        typography = Typography,
        content = content,
    )
}