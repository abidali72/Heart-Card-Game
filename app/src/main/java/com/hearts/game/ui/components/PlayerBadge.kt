package com.hearts.game.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hearts.game.ui.theme.*

enum class BadgeOrientation {
    HORIZONTAL,
    VERTICAL,
    VERTICAL_MIRRORED // Text rotated 180 degrees if needed, or just standard vertical
}

@Composable
fun PlayerBadge(
    name: String,
    isActive: Boolean,
    score: Int? = null,
    orientation: BadgeOrientation = BadgeOrientation.HORIZONTAL,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isActive) ActivePlayerGlow else PlayerBadgeGold,
        label = "badgeColor"
    )
    
    val textColor = PlayerBadgeText

    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .shadow(4.dp, shape)
            .clip(shape)
            .background(backgroundColor)
            .border(
                width = if (isActive) 3.dp else 0.dp,
                color = if (isActive) Color.White else Color.Transparent,
                shape = shape
            )
            .padding(horizontal = 16.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        val displayText = if (score != null) "${name.uppercase()} ($score)" else name.uppercase()
        
        when (orientation) {
            BadgeOrientation.HORIZONTAL -> {
                Text(
                    text = displayText,
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 1.sp
                )
            }
            BadgeOrientation.VERTICAL -> {
                 // For vertical side names, we rotate the text
                Text(
                    text = displayText,
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 1.sp,
                    modifier = Modifier.vertical(clockwise = true)
                )
            }
            BadgeOrientation.VERTICAL_MIRRORED -> {
               Text(
                    text = displayText,
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 1.sp,
                    modifier = Modifier.vertical(clockwise = false)
                )
            }
        }
    }
}

private fun Modifier.vertical(clockwise: Boolean) = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    layout(placeable.height, placeable.width) {
        placeable.place(
            x = -(placeable.width / 2 - placeable.height / 2),
            y = -(placeable.height / 2 - placeable.width / 2)
        )
    }
}.graphicsLayer {
    rotationZ = if (clockwise) 90f else -90f
}


