package bapale.rioc.unizon.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = TextOnPrimary,
    primaryContainer = GreenLight,
    onPrimaryContainer = GreenDark,

    secondary = GreenDark,
    onSecondary = Color.White,
    secondaryContainer = GreenLighter,
    onSecondaryContainer = GreenDark,

    tertiary = GreenPrimary,
    onTertiary = TextOnPrimary,
    tertiaryContainer = GreenLighter,
    onTertiaryContainer = GreenDark,

    background = PastelBackground,
    onBackground = TextOnSurface,

    surface = GreenLighter,
    onSurface = TextOnSurface,
    surfaceVariant = GreenLighter,
    onSurfaceVariant = GreenDark,

    outline = GreenDark.copy(alpha = 0.5f),
    outlineVariant = GreenLight,

    error = Color(0xFFB00020),
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = GreenLight,
    secondary = GreenDark,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E)
)

@Composable
fun UnizonTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}