package com.hearts.game.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hearts.game.data.model.GameStats
import com.hearts.game.ui.theme.*

import com.hearts.game.ui.viewmodel.SettingsViewModel

@Composable
fun StatsScreen(
    onBack: () -> Unit,
    settingsViewModel: SettingsViewModel? = null // Optional to allow preview or simpler construction
) {
    val stats by settingsViewModel?.gameStats?.collectAsState() ?: remember { mutableStateOf(GameStats()) }
    
    // Calculate display values
    val winRate = if (stats.gamesPlayed > 0) {
        "%.1f%%".format((stats.gamesWon.toFloat() / stats.gamesPlayed) * 100)
    } else "0%"
    
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
                text = "STATISTICS",
                color = GoldAccent,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            )
        }

        StatCard("Games Played", "${stats.gamesPlayed}")
        StatCard("Games Won", "${stats.gamesWon}")
        StatCard("Win Rate", winRate)
        StatCard("Total Tricks Won", "${stats.totalTricksWon}")
        StatCard("Moon Shots", "${stats.moonShots}")
        // StatCard("Avg Points/Round", "0.0") // Not tracked yet

        Spacer(modifier = Modifier.height(24.dp))

        // Achievements section
        Text(
            text = "ACHIEVEMENTS",
            color = GoldAccent,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
        )

        AchievementCard("🏆", "First Victory", "Win your first game", locked = stats.gamesWon < 1)
        AchievementCard("🌙", "Moon Shooter", "Successfully shoot the moon", locked = stats.moonShots < 1)
        AchievementCard("⭐", "Veteran", "Play 10 games", locked = stats.gamesPlayed < 10)
        AchievementCard("🎯", "Trick Master", "Win 50 tricks", locked = stats.totalTricksWon < 50)
        // AchievementCard("🔥", "Win Streak", "Win 3 games in a row", locked = true) // Not tracked

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun StatCard(label: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface.copy(alpha = 0.7f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, color = TextSecondary, fontSize = 15.sp)
            Text(text = value, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AchievementCard(
    emoji: String,
    title: String,
    description: String,
    locked: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (locked) DarkSurface.copy(alpha = 0.4f) else DarkSurface.copy(alpha = 0.8f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = emoji,
                fontSize = 28.sp,
                modifier = Modifier.padding(end = 12.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = if (locked) TextMuted else GoldAccent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
            if (locked) {
                Text(text = "🔒", fontSize = 18.sp)
            } else {
                Text(text = "✅", fontSize = 18.sp)
            }
        }
    }
}
