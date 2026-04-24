package com.hearts.game.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hearts.game.data.model.AIDifficulty
import com.hearts.game.ui.theme.*

@Composable
fun MainMenuScreen(
    onNewGame: (AIDifficulty) -> Unit,
    onSettings: () -> Unit,
    onHowToPlay: () -> Unit,
    onStats: () -> Unit
) {
    var showDifficultyPicker by remember { mutableStateOf(false) }

    // Animated suit symbols in background
    val infiniteTransition = rememberInfiniteTransition(label = "bgAnim")
    val float1 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing)),
        label = "float1"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkNavy, DeepGreen, DarkNavy)
                )
            )
    ) {
        // Decorative floating suits
        val suits = listOf("♠", "♥", "♦", "♣")
        suits.forEachIndexed { i, suit ->
            val offset by infiniteTransition.animateFloat(
                initialValue = -20f,
                targetValue = 20f,
                animationSpec = infiniteRepeatable(
                    tween(3000 + i * 500, easing = FastOutSlowInEasing),
                    RepeatMode.Reverse
                ),
                label = "suit$i"
            )
            Text(
                text = suit,
                color = if (suit == "♥" || suit == "♦") HeartRed.copy(alpha = 0.08f)
                else TextPrimary.copy(alpha = 0.05f),
                fontSize = (60 + i * 20).sp,
                modifier = Modifier
                    .offset(
                        x = (20 + i * 80).dp,
                        y = (100 + i * 150 + offset).dp
                    )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo area
            val heartPulse by infiniteTransition.animateFloat(
                initialValue = 0.95f, targetValue = 1.1f,
                animationSpec = infiniteRepeatable(
                    tween(1200, easing = FastOutSlowInEasing),
                    RepeatMode.Reverse
                ),
                label = "heartPulse"
            )

            Text(
                text = "♥",
                fontSize = 72.sp,
                color = HeartRed,
                modifier = Modifier.scale(heartPulse)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "HEARTS",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = GoldAccent,
                letterSpacing = 8.sp
            )

            Text(
                text = "CARD GAME",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary,
                letterSpacing = 6.sp
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Menu buttons
            MenuButton(
                text = "NEW GAME",
                icon = Icons.Filled.PlayArrow,
                gradient = listOf(GoldDark, GoldAccent),
                onClick = { showDifficultyPicker = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            MenuButton(
                text = "SETTINGS",
                icon = Icons.Filled.Settings,
                gradient = listOf(CardSurface, DarkSurface),
                onClick = onSettings
            )

            Spacer(modifier = Modifier.height(16.dp))

            MenuButton(
                text = "HOW TO PLAY",
                icon = Icons.Filled.Info,
                gradient = listOf(CardSurface, DarkSurface),
                onClick = onHowToPlay
            )

            Spacer(modifier = Modifier.height(16.dp))

            MenuButton(
                text = "STATISTICS",
                icon = Icons.Filled.Star,
                gradient = listOf(CardSurface, DarkSurface),
                onClick = onStats
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "v1.0.0",
                color = TextMuted,
                fontSize = 12.sp
            )
        }

        // Difficulty picker dialog
        if (showDifficultyPicker) {
            DifficultyPicker(
                onDismiss = { showDifficultyPicker = false },
                onSelect = { difficulty ->
                    showDifficultyPicker = false
                    onNewGame(difficulty)
                }
            )
        }
    }
}

@Composable
private fun MenuButton(
    text: String,
    icon: ImageVector,
    gradient: List<Color>,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.75f)
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.horizontalGradient(gradient))
            .border(1.dp, GoldAccent.copy(alpha = 0.3f), RoundedCornerShape(28.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = TextPrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }
    }
}

@Composable
private fun DifficultyPicker(
    onDismiss: () -> Unit,
    onSelect: (AIDifficulty) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clickable(enabled = false) {},
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SELECT DIFFICULTY",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                DifficultyOption(
                    title = "Easy",
                    description = "Random play, no strategy",
                    color = SuccessGreen,
                    onClick = { onSelect(AIDifficulty.EASY) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                DifficultyOption(
                    title = "Medium",
                    description = "Suit tracking, avoids risks",
                    color = WarningYellow,
                    onClick = { onSelect(AIDifficulty.MEDIUM) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                DifficultyOption(
                    title = "Hard",
                    description = "Card counting, moon defense",
                    color = HeartRed,
                    onClick = { onSelect(AIDifficulty.HARD) }
                )
            }
        }
    }
}

@Composable
private fun DifficultyOption(
    title: String,
    description: String,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardSurface)
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = description, color = TextSecondary, fontSize = 12.sp)
            }
        }
    }
}
