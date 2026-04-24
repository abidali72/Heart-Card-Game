package com.hearts.game.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hearts.game.data.model.PlayerPosition
import com.hearts.game.ui.theme.*

/**
 * Top HUD bar showing player scores, round info, and hearts broken indicator.
 */
@Composable
fun ScoreBar(
    scores: Map<PlayerPosition, Int>,
    roundNumber: Int,
    heartsBroken: Boolean,
    currentPlayer: PlayerPosition?,
    modifier: Modifier = Modifier
) {
    val heartGlow = rememberInfiniteTransition(label = "heartGlow")
    val heartAlpha by heartGlow.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heartAlpha"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(ScoreBarBg, ScoreBarBg.copy(alpha = 0.8f))
                )
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Round indicator
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Round",
                color = TextMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$roundNumber",
                color = GoldAccent,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Player scores
        PlayerPosition.allPositions.forEach { pos ->
            val isActive = pos == currentPlayer
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(
                    text = pos.displayName,
                    color = if (isActive) ActivePlayerGlow else TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    text = "${scores[pos] ?: 0}",
                    color = if (isActive) ActivePlayerGlow else TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Hearts broken indicator
        if (heartsBroken) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "Hearts Broken",
                tint = HeartsBrokenRed.copy(alpha = heartAlpha),
                modifier = Modifier.size(24.dp)
            )
        } else {
            Spacer(modifier = Modifier.size(24.dp))
        }
    }
}
