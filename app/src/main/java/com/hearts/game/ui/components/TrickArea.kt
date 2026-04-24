package com.hearts.game.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hearts.game.data.model.*
import com.hearts.game.ui.theme.*

/**
 * Center trick area showing up to 4 played cards in a diamond/cross pattern.
 */
@Composable
fun TrickArea(
    currentTrick: Trick,
    gameSpeed: GameSpeed,
    cardTheme: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        // Trick number indicator
        if (currentTrick.trickNumber > 0) {
            Text(
                text = "Trick ${currentTrick.trickNumber}",
                color = TextMuted.copy(alpha = 0.5f),
                fontSize = 10.sp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp)
            )
        }

        // Cards positioned in cross pattern: South (bottom), West (left), North (top), East (right)
        currentTrick.plays.forEach { play ->
            val (offsetX, offsetY) = when (play.player) {
                PlayerPosition.SOUTH -> Pair(0.dp, 40.dp)
                PlayerPosition.WEST -> Pair((-50).dp, 0.dp)
                PlayerPosition.NORTH -> Pair(0.dp, (-40).dp)
                PlayerPosition.EAST -> Pair(50.dp, 0.dp)
            }

            // Entry animation
            val entryAnim = remember(play) { Animatable(0f) }
            LaunchedEffect(play) {
                entryAnim.animateTo(
                    1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = gameSpeed.animStiffness
                    )
                )
            }

            Box(
                modifier = Modifier
                    .offset(x = offsetX, y = offsetY)
                    .graphicsLayer {
                        scaleX = entryAnim.value
                        scaleY = entryAnim.value
                        alpha = entryAnim.value
                    }
            ) {
                CardComposable(
                    card = play.card,
                    isFaceUp = true,
                    cardWidth = 48.dp,
                    cardHeight = 68.dp,
                    cardTheme = cardTheme
                )
            }
        }

        // Lead suit indicator
        if (currentTrick.leadSuit != null && currentTrick.plays.isNotEmpty()) {
            Text(
                text = "Lead: ${currentTrick.leadSuit!!.symbol}",
                color = GoldAccent.copy(alpha = 0.7f),
                fontSize = 9.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp)
            )
        }
    }
}
