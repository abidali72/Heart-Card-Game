package com.hearts.game.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hearts.game.data.model.Card
import com.hearts.game.data.model.GameSpeed
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Displays the human player's hand in a fanned arc layout at the bottom of the screen.
 */
@Composable
fun PlayerHandLayout(
    cards: List<Card>,
    selectedCards: List<Card>,
    validPlays: List<Card>,
    isPlayerTurn: Boolean,
    gameSpeed: GameSpeed,
    cardTheme: Int,
    onCardClick: (Card) -> Unit,
    modifier: Modifier = Modifier
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth = 54.dp
    val cardHeight = 76.dp

    val cardCount = cards.size
    if (cardCount == 0) return

    // Calculate overlap and fan angle based on card count
    val maxWidth = screenWidth - 32.dp
    val totalCardWidth = cardWidth * cardCount
    val overlap = if (totalCardWidth > maxWidth) {
        ((totalCardWidth - maxWidth).value / (cardCount - 1).coerceAtLeast(1)).dp
    } else {
        0.dp
    }

    val actualCardSpacing = cardWidth - overlap
    val totalWidth = actualCardSpacing * (cardCount - 1) + cardWidth

    // Fan angle parameters
    val maxFanAngle = 40f  // Total spread in degrees
    val arcRadius = 600f   // Curvature radius

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(cardHeight + 24.dp)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier.width(totalWidth),
            contentAlignment = Alignment.BottomCenter
        ) {
            cards.forEachIndexed { index, card ->
                val isSelected = selectedCards.contains(card)
                val isValid = validPlays.contains(card) && isPlayerTurn

                // Calculate fan position
                val progress = if (cardCount > 1) {
                    (index.toFloat() / (cardCount - 1)) - 0.5f
                } else 0f

                val angle = progress * maxFanAngle
                val radians = angle * (PI / 180f).toFloat()
                val yOffset = (arcRadius * (1 - cos(radians.toDouble()))).toFloat()

                val entryAnim = remember { Animatable(0f) }
                LaunchedEffect(card) {
                    entryAnim.animateTo(
                        1f,
                        animationSpec = tween(
                            durationMillis = gameSpeed.dealDurationMs * 2,
                            delayMillis = index * (gameSpeed.dealDurationMs / 5).coerceAtLeast(10),
                            easing = FastOutSlowInEasing
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .offset(x = actualCardSpacing * index - totalWidth / 2 + cardWidth / 2)
                        .graphicsLayer {
                            rotationZ = angle
                            translationY = yOffset - if (isSelected) 20f else 0f
                            alpha = entryAnim.value
                            scaleX = entryAnim.value
                            scaleY = entryAnim.value
                        }
                ) {
                    CardComposable(
                        card = card,
                        isSelected = isSelected,
                        isValid = isValid,
                        isFaceUp = true,
                        cardWidth = cardWidth,
                        cardHeight = cardHeight,
                        cardTheme = cardTheme,
                        onClick = if (isPlayerTurn) {
                            { onCardClick(card) }
                        } else null
                    )
                }
            }
        }
    }
}
