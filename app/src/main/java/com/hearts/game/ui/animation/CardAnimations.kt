package com.hearts.game.ui.animation

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import com.hearts.game.data.model.GameSpeed
import kotlinx.coroutines.delay

/**
 * Shake animation modifier for invalid moves.
 */
fun Modifier.shakeAnimation(trigger: Boolean): Modifier = composed {
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(trigger) {
        if (trigger) {
            for (i in 0 until 4) {
                shakeOffset.animateTo(10f, animationSpec = tween(50))
                shakeOffset.animateTo(-10f, animationSpec = tween(50))
            }
            shakeOffset.animateTo(0f, animationSpec = tween(50))
        }
    }

    this.graphicsLayer { translationX = shakeOffset.value }
}

/**
 * Card dealing animation — cards scale and fade in from center.
 */
@Composable
fun rememberDealAnimation(index: Int, totalCards: Int, gameSpeed: GameSpeed): State<Float> {
    val anim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(index * (gameSpeed.dealDurationMs / 3).toLong()) // Stagger based on speed
        anim.animateTo(
            1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = gameSpeed.animStiffness
            )
        )
    }
    return anim.asState()
}

/**
 * Victory/Moon celebration pulse animation.
 */
@Composable
fun rememberCelebrationPulse(): State<Float> {
    val infiniteTransition = rememberInfiniteTransition(label = "celebration")
    return infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "celebPulse"
    )
}

/**
 * Score count-up animation.
 */
@Composable
fun rememberScoreCountUp(targetScore: Int, durationMs: Int = 1500): State<Int> {
    val animatedScore = remember { Animatable(0f) }
    LaunchedEffect(targetScore) {
        animatedScore.animateTo(
            targetScore.toFloat(),
            animationSpec = tween(durationMs, easing = FastOutSlowInEasing)
        )
    }
    return derivedStateOf { animatedScore.value.toInt() }
}
