package com.hearts.game.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.shape.RoundedCornerShape
import com.hearts.game.data.model.PlayerPosition

/**
 * Displays an opponent's hand as stacked face-down cards.
 */
@Composable
fun OpponentHand(
    position: PlayerPosition,
    cardCount: Int,
    isActive: Boolean,
    cardTheme: Int,
    modifier: Modifier = Modifier
) {
    val glowAnim = rememberInfiniteTransition(label = "opponentGlow")
    val glowAlpha by glowAnim.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val displayCount = minOf(cardCount, 6) // Show max 6 stacked
    val rotation = when (position) {
        PlayerPosition.WEST -> 90f
        PlayerPosition.EAST -> -90f
        PlayerPosition.NORTH -> 0f
        else -> 0f
    }

    val isHorizontal = position == PlayerPosition.WEST || position == PlayerPosition.EAST

    Box(
        modifier = modifier
            .graphicsLayer {
                if (isActive) {
                    // Subtle glow effect for active player
                    alpha = 1f
                    scaleX = 1.05f
                    scaleY = 1.05f
                } else {
                    alpha = 0.9f
                    scaleX = 1f
                    scaleY = 1f
                }
            },
        contentAlignment = Alignment.Center
    ) {
        if (isHorizontal) {
            // Vertical stacking for side players (Left/Right)
            // Stacking downwards or upwards depending on visual preference?
            // Reference image shows a neat vertical stack.
            Box(
                modifier = Modifier
                    .width(44.dp)
                    .height((56 + displayCount * 6).dp)
            ) {
                for (i in 0 until displayCount) {
                    Box(
                        modifier = Modifier
                            .offset(y = (i * 6).dp) // Tighter vertical overlap
                            .shadow(2.dp, RoundedCornerShape(4.dp))
                    ) {
                        CardComposable(
                            card = com.hearts.game.data.model.Card(
                                com.hearts.game.data.model.Suit.SPADES,
                                com.hearts.game.data.model.Rank.ACE
                            ),
                            isFaceUp = false,
                            cardWidth = 44.dp,
                            cardHeight = 60.dp,
                            cardTheme = cardTheme
                        )
                    }
                }
            }
        } else {
            // Horizontal fan for top player
            Box(
                modifier = Modifier
                    .width((44 + displayCount * 12).dp)
                    .height(60.dp)
            ) {
                for (i in 0 until displayCount) {
                    Box(
                        modifier = Modifier
                            .offset(x = (i * 12).dp)
                            .shadow(2.dp, RoundedCornerShape(4.dp))
                    ) {
                        CardComposable(
                            card = com.hearts.game.data.model.Card(
                                com.hearts.game.data.model.Suit.SPADES,
                                com.hearts.game.data.model.Rank.ACE
                            ),
                            isFaceUp = false,
                            cardWidth = 44.dp,
                            cardHeight = 60.dp,
                            cardTheme = cardTheme
                        )
                    }
                }
            }
        }
    }
}
