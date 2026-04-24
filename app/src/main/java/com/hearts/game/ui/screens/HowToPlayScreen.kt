package com.hearts.game.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hearts.game.ui.theme.*

@Composable
fun HowToPlayScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DarkNavy, DeepGreen, DarkNavy)))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, "Back", tint = TextPrimary)
            }
            Text(
                text = "HOW TO PLAY",
                color = GoldAccent,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            )
        }

        RuleSection("Objective", "Avoid taking points! The player with the lowest score when someone reaches the score limit wins.")
        RuleSection("Card Values", "♥ Hearts = 1 point each\n♠ Queen of Spades = 13 points\nAll other cards = 0 points")
        RuleSection("Dealing", "Each player gets 13 cards from a standard 52-card deck.")
        RuleSection("Passing Cards",
            "Before each round, pass 3 cards:\n• Round 1: Pass Left\n• Round 2: Pass Right\n• Round 3: Pass Across\n• Round 4: No Pass\nThis cycle repeats."
        )
        RuleSection("Playing",
            "• The player with 2♣ leads the first trick\n• You must follow the lead suit if you can\n• If you're void, play any card\n• Highest card of the lead suit wins the trick\n• Winner leads the next trick"
        )
        RuleSection("First Trick Rules",
            "• No Hearts can be played\n• No Queen of Spades can be played\n(unless you have no choice)"
        )
        RuleSection("Hearts Breaking",
            "Hearts cannot be led until a Heart has been played on a previous trick. Once played, Hearts are \"broken\" and can be led."
        )
        RuleSection("Shoot the Moon 🌙",
            "If you collect ALL 13 Hearts AND the Queen of Spades, you score 0 points and every other player gets +26! A risky but rewarding strategy."
        )
        RuleSection("Winning", "When any player's score reaches the limit (default: 100), the game ends. The player with the lowest score wins!")

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun RuleSection(title: String, content: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface.copy(alpha = 0.7f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                color = GoldAccent,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = content,
                color = TextSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}
