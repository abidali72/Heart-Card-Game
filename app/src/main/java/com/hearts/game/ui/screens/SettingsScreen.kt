package com.hearts.game.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hearts.game.data.model.*
import com.hearts.game.ui.theme.*
import com.hearts.game.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val settings by settingsViewModel.settings.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DarkNavy, DeepGreen, DarkNavy)))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, "Back", tint = TextPrimary)
            }
            Text(
                text = "SETTINGS",
                color = GoldAccent,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            )
        }

        // Sound & Music
        SettingsSection(title = "Audio") {
            SettingsToggle("Sound Effects", settings.soundEnabled) {
                settingsViewModel.updateSound(it)
            }
            SettingsToggle("Background Music", settings.musicEnabled) {
                settingsViewModel.updateMusic(it)
            }
            SettingsToggle("Vibration", settings.vibrationEnabled) {
                settingsViewModel.updateVibration(it)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Gameplay
        SettingsSection(title = "Gameplay") {
            // Game Speed
            SettingsLabel("Game Speed")
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                GameSpeed.entries.forEach { speed ->
                    val isSelected = settings.gameSpeed == speed
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) GoldAccent else CardSurface)
                            .clickable { settingsViewModel.updateGameSpeed(speed) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = speed.displayName,
                            color = if (isSelected) DarkNavy else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Score Limit
            SettingsLabel("Score Limit")
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                listOf(50, 100, 150).forEach { limit ->
                    val isSelected = settings.scoreLimit == limit
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) GoldAccent else CardSurface)
                            .clickable { settingsViewModel.updateScoreLimit(limit) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$limit",
                            color = if (isSelected) DarkNavy else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // AI Difficulty
            SettingsLabel("AI Difficulty")
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                AIDifficulty.entries.forEach { diff ->
                    val isSelected = settings.aiDifficulty == diff
                    val color = when (diff) {
                        AIDifficulty.EASY -> SuccessGreen
                        AIDifficulty.MEDIUM -> WarningYellow
                        AIDifficulty.HARD -> HeartRed
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) color else CardSurface)
                            .clickable { settingsViewModel.updateDifficulty(diff) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = diff.displayName,
                            color = if (isSelected) DarkNavy else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Themes
        SettingsSection(title = "Appearance") {
            SettingsLabel("Card Theme")
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                listOf("Classic", "Modern", "Neon").forEachIndexed { idx, name ->
                    val isSelected = settings.cardTheme == idx
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) GoldAccent else CardSurface)
                            .clickable { settingsViewModel.updateCardTheme(idx) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name,
                            color = if (isSelected) DarkNavy else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            SettingsLabel("Table Theme")
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                listOf("Green", "Blue", "Red").forEachIndexed { idx, name ->
                    val isSelected = settings.tableTheme == idx
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) GoldAccent else CardSurface)
                            .clickable { settingsViewModel.updateTableTheme(idx) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name,
                            color = if (isSelected) DarkNavy else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface.copy(alpha = 0.8f))
    ) {
        Column(modifier = Modifier.padding(vertical = 12.dp)) {
            Text(
                text = title.uppercase(),
                color = GoldAccent,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            content()
        }
    }
}

@Composable
private fun SettingsToggle(label: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle(!checked) }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextPrimary, fontSize = 15.sp)
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = GoldAccent,
                checkedTrackColor = GoldDark,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = CardSurface
            )
        )
    }
}

@Composable
private fun SettingsLabel(text: String) {
    Text(
        text = text,
        color = TextSecondary,
        fontSize = 13.sp,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
    )
}
