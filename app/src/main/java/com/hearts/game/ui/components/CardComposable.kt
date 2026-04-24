package com.hearts.game.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hearts.game.data.model.Card
import com.hearts.game.data.model.CardColor
import com.hearts.game.data.model.Rank
import com.hearts.game.data.model.Suit
import com.hearts.game.ui.theme.*

/**
 * Renders a playing card face-up with suit, rank, and proper styling.
 */
@Composable
fun CardComposable(
    card: Card,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    isValid: Boolean = true,
    isFaceUp: Boolean = true,
    cardWidth: Dp = 60.dp,
    cardHeight: Dp = 84.dp,
    cardTheme: Int = 0,
    onClick: (() -> Unit)? = null
) {
    val elevation by animateDpAsState(
        targetValue = if (isSelected) 12.dp else 4.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "cardElevation"
    )

    val offsetY by animateDpAsState(
        targetValue = if (isSelected) (-12).dp else 0.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "cardOffset"
    )

    // Determine colors based on theme
    val faceColor = when(cardTheme) {
        2 -> Color(0xFF1A1A1A) // Neon Dark
        else -> CardFaceWhite
    }
    
    val backColor = when(cardTheme) {
        1 -> Color(0xFFD32F2F) // Modern Red
        2 -> Color(0xFF121212) // Neon Black
        else -> CardBackBlue // Classic Blue
    }
    
    val borderColor = when(cardTheme) {
        2 -> Color(0xFF00FF00) // Neon Green Border
        else -> CardBorder
    }

    val suitColor = if (card.suit.color == CardColor.RED) {
        if (cardTheme == 2) Color(0xFFFF0055) else SuitRed // Neon Pink or Standard Red
    } else {
        if (cardTheme == 2) Color(0xFF00FFFF) else SuitBlack // Neon Cyan or Standard Black
    }

    Box(
        modifier = modifier
            .offset(y = offsetY)
            .then(
                if (isSelected || (isValid && onClick != null)) {
                    Modifier.border(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                if (isSelected) ActivePlayerGlow
                                else ValidMoveHighlight,
                                if (isSelected) PlayerBadgeGold
                                else ValidMoveHighlight
                            )
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                } else Modifier
            )
    ) {
        Box(
            modifier = Modifier
                .width(cardWidth)
                .height(cardHeight)
                .shadow(elevation, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(if (isFaceUp) faceColor else backColor)
                .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                .then(
                    if (onClick != null && isValid) {
                        Modifier.clickable { onClick() }
                    } else Modifier
                )
        ) {
            if (isFaceUp) {
                CardFace(card = card, suitColor = suitColor, cardWidth = cardWidth, cardTheme = cardTheme)
            } else {
                CardBackDesign(cardTheme = cardTheme)
            }
        }
    }
}

@Composable
private fun CardFace(
    card: Card,
    suitColor: Color,
    cardWidth: Dp,
    cardTheme: Int
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(4.dp)
    ) {
        // Top-left rank + suit
        Column(
            modifier = Modifier.align(Alignment.TopStart),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = card.rank.displayName,
                color = suitColor,
                fontSize = if (cardWidth > 50.dp) 14.sp else 10.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = if (cardWidth > 50.dp) 16.sp else 12.sp,
                fontFamily = if (cardTheme == 1) androidx.compose.ui.text.font.FontFamily.SansSerif else androidx.compose.ui.text.font.FontFamily.Default
            )
            Text(
                text = card.suit.symbol,
                color = suitColor,
                fontSize = if (cardWidth > 50.dp) 12.sp else 9.sp,
                lineHeight = if (cardWidth > 50.dp) 14.sp else 10.sp
            )
        }

        // Center suit symbol (large)
        Text(
            text = card.suit.symbol,
            color = suitColor.copy(alpha = if (cardTheme == 2) 0.8f else 1f),
            fontSize = if (cardWidth > 50.dp) 28.sp else 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )

        // Bottom-right rank + suit (inverted)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .graphicsLayer { rotationZ = 180f },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = card.rank.displayName,
                color = suitColor,
                fontSize = if (cardWidth > 50.dp) 14.sp else 10.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = if (cardWidth > 50.dp) 16.sp else 12.sp,
                fontFamily = if (cardTheme == 1) androidx.compose.ui.text.font.FontFamily.SansSerif else androidx.compose.ui.text.font.FontFamily.Default
            )
            Text(
                text = card.suit.symbol,
                color = suitColor,
                fontSize = if (cardWidth > 50.dp) 12.sp else 9.sp,
                lineHeight = if (cardWidth > 50.dp) 14.sp else 10.sp
            )
        }
    }
}

@Composable
private fun CardBackDesign(cardTheme: Int) {
    val backColor = when(cardTheme) {
        1 -> Color(0xFFD32F2F) // Modern Red
        2 -> Color(0xFF121212) // Neon Black
        else -> CardBackBlue
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backColor)
            .padding(4.dp)
    ) {
        when(cardTheme) {
            0 -> { // Classic
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(2.dp, CardBackPattern, RoundedCornerShape(4.dp))
                )
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.Center)
                        .background(CardBackPattern, androidx.compose.foundation.shape.CircleShape)
                )
            }
            1 -> { // Modern - Minimalist Gradient or Solid
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFFF5252), Color(0xFFB71C1C))
                            ),
                            RoundedCornerShape(4.dp)
                        )
                ) {
                   // Minimalist white circle
                   Box(
                        modifier = Modifier
                            .size(12.dp)
                            .align(Alignment.Center)
                            .background(Color.White.copy(alpha=0.2f), androidx.compose.foundation.shape.CircleShape)
                   )
                }
            }
            2 -> { // Neon - Grid or Tech pattern
                 Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(1.dp, Color(0xFF00FF00).copy(alpha=0.5f), RoundedCornerShape(4.dp))
                )
                // Diagonal line
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .align(Alignment.Center)
                        .background(Color(0xFF00FF00).copy(alpha=0.2f))
                        .graphicsLayer { rotationZ = 45f }
                )
            }
        }
    }
}

/**
 * Small card for opponent display and trick area.
 */
@Composable
fun SmallCard(
    card: Card?,
    isFaceUp: Boolean = false,
    cardTheme: Int = 0,
    modifier: Modifier = Modifier
) {
    CardComposable(
        card = card ?: Card(Suit.SPADES, Rank.ACE),
        isFaceUp = isFaceUp && card != null,
        cardWidth = 40.dp,
        cardHeight = 56.dp,
        cardTheme = cardTheme,
        modifier = modifier
    )
}
