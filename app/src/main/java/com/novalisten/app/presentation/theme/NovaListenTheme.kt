// presentation/theme/NovaListenTheme.kt
package com.novalisten.app.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NovaDarkColorScheme = darkColorScheme(
    primary        = Color(0xFF6650A4),
    secondary      = Color(0xFF625B71),
    tertiary       = Color(0xFF7D5260),
    background     = Color(0xFF0A0A0A),
    surface        = Color(0xFF1A1A1A),
    onPrimary      = Color.White,
    onBackground   = Color.White,
    onSurface      = Color.White
)

/**
 * Thème principal de NovaListen.
 * Dark mode uniquement (app musicale = dark natif).
 */
@Composable
fun NovaListenTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NovaDarkColorScheme,
        content     = content
    )
}