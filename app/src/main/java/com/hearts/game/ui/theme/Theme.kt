package com.hearts.game.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PlayerBadgeGold,
    onPrimary = PlayerBadgeText,
    primaryContainer = GameTableBorder,
    onPrimaryContainer = PlayerBadgeText,
    secondary = GameTableTeal,
    onSecondary = LabelWhite,
    secondaryContainer = WarmRedBackground,
    onSecondaryContainer = LabelWhite,
    tertiary = ActivePlayerGlow,
    onTertiary = TextDark,
    background = WarmRedBackground,
    onBackground = LabelWhite,
    surface = GameTableTeal,
    onSurface = LabelWhite,
    surfaceVariant = GameTableBorder,
    onSurfaceVariant = PlayerBadgeText,
    error = ErrorShake,
    onError = Color.White,
    outline = CardBorder
)

@Composable
fun HeartsTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = HeartsTypography,
        content = content
    )
}
