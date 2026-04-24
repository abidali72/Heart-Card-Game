package com.hearts.game.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hearts.game.data.model.*
import com.hearts.game.domain.engine.GameRules
import com.hearts.game.ui.components.*
import com.hearts.game.ui.theme.*
import com.hearts.game.ui.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onBackToMenu: () -> Unit
) {
    val gameState by viewModel.gameState.collectAsState()
    val showError by viewModel.showError.collectAsState()
    val showMoon by viewModel.showMoonAnimation.collectAsState()
    val isAnimating by viewModel.isAnimating.collectAsState()
    
    var showStats by remember { mutableStateOf(false) }

    // Theme Colors
    val tableTheme = gameState.config.tableTheme
    val backgroundColor = when(tableTheme) {
        1 -> TableBlueBackground // Blue
        2 -> TableRedBackground // Red
        else -> TableGreenBackground // Green (Default)
    }
    val surfaceColor = when(tableTheme) {
        1 -> TableBlueSurface
        2 -> TableRedSurface
        else -> TableGreenSurface
    }
    val borderColor = when(tableTheme) {
        1 -> TableBlueBorder
        2 -> TableRedBorder
        else -> TableGreenBorder
    }

    // Main Background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // --- Game Table Surface ---
        // Centered rounded rectangle
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .fillMaxHeight(0.65f)
                .align(Alignment.Center)
                .border(8.dp, borderColor, RoundedCornerShape(32.dp))
                .clip(RoundedCornerShape(32.dp))
                .background(surfaceColor)
        ) {
            // Heart Watermark
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = null,
                tint = Color.Black.copy(alpha = 0.1f),
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.Center)
            )

            // Inner Table Content (Trick Area, Indicators)
             when (val phase = gameState.phase) {
                is GamePhase.Playing,
                is GamePhase.TrickComplete -> {
                     TrickArea(
                        currentTrick = gameState.currentTrick,
                        gameSpeed = gameState.config.gameSpeed,
                        cardTheme = gameState.config.cardTheme,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                 is GamePhase.Passing -> {
                     Text(
                        text = "PASSING PHASE",
                        color = LabelWhite.copy(alpha=0.5f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        modifier = Modifier.align(Alignment.Center)
                     )
                 }
                 else -> {}
             }
        }

        // --- Players & Controls Layer ---
        
        // 1. TOP UI CONTROLS
        // Back Button (Top Left)
        IconButton(
            onClick = onBackToMenu,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .size(48.dp)
                .shadow(4.dp, androidx.compose.foundation.shape.CircleShape)
                .background(ButtonWhite, androidx.compose.foundation.shape.CircleShape)
        ) {
            Icon(
                Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextDark
            )
        }

        // Stats Button (Top Right)
        IconButton(
            onClick = { showStats = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(48.dp)
                .shadow(4.dp, androidx.compose.foundation.shape.CircleShape)
                .background(ButtonWhite, androidx.compose.foundation.shape.CircleShape)
        ) {
            Icon(
                Icons.Filled.BarChart, // Using BarChart as generic stats icon
                contentDescription = "Stats",
                tint = HeartRed
            )
        }

        // 2. PLAYER POSITIONS (Cross Layout)

        // --- TOP PLAYER (North) ---
        // Layout: Cards (Horizontal Fan) ABOVE Name Badge
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp), // Check padding relative to controls
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val northPlayer = gameState.getPlayer(PlayerPosition.NORTH)
            OpponentHand(
                position = PlayerPosition.NORTH,
                cardCount = northPlayer.handSize,
                isActive = gameState.currentPlayerPosition == PlayerPosition.NORTH,
                cardTheme = gameState.config.cardTheme,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            PlayerBadge(
                name = northPlayer.name,
                isActive = gameState.currentPlayerPosition == PlayerPosition.NORTH,
                score = northPlayer.roundScore,
                orientation = BadgeOrientation.HORIZONTAL
            )
        }


        // --- LEFT PLAYER (West) ---
        // Layout: Name Badge (Vertical) LEFT of Cards (Vertical Stack)
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
             val westPlayer = gameState.getPlayer(PlayerPosition.WEST)
             PlayerBadge(
                name = westPlayer.name,
                isActive = gameState.currentPlayerPosition == PlayerPosition.WEST,
                score = westPlayer.roundScore,
                orientation = BadgeOrientation.VERTICAL
            )
            Spacer(modifier = Modifier.width(8.dp))
            OpponentHand(
                position = PlayerPosition.WEST,
                cardCount = westPlayer.handSize,
                isActive = gameState.currentPlayerPosition == PlayerPosition.WEST,
                cardTheme = gameState.config.cardTheme
            )
        }


        // --- RIGHT PLAYER (East) ---
        // Layout: Cards (Vertical Stack) LEFT of Name Badge (Vertical)
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val eastPlayer = gameState.getPlayer(PlayerPosition.EAST)
            OpponentHand(
                position = PlayerPosition.EAST,
                cardCount = eastPlayer.handSize,
                isActive = gameState.currentPlayerPosition == PlayerPosition.EAST,
                cardTheme = gameState.config.cardTheme
            )
            Spacer(modifier = Modifier.width(8.dp))
            PlayerBadge(
                name = eastPlayer.name,
                isActive = gameState.currentPlayerPosition == PlayerPosition.EAST,
                score = eastPlayer.roundScore,
                orientation = BadgeOrientation.VERTICAL_MIRRORED
            )
        }


        // --- BOTTOM PLAYER (Human/South) ---
        // Layout: Name Badge "YOU" on Table Edge, Cards Fanned Below
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            val humanPlayer = gameState.humanPlayer ?: return@Box
            
            // "YOU" Badge - Positioned slightly overlapping the table bottom
            // We need to calculate this position or just place it above the hand
             PlayerBadge(
                name = "YOU",
                isActive = gameState.currentPlayerPosition == PlayerPosition.SOUTH,
                score = humanPlayer.roundScore,
                orientation = BadgeOrientation.HORIZONTAL,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-40).dp) // Shift up to sit on table edge roughly
            )

            // Player Hand
            val isHumanTurn = gameState.isHumanTurn && !isAnimating
             val validPlays = if (isHumanTurn) {
                GameRules.getValidPlays(
                    hand = humanPlayer.hand,
                    currentTrick = gameState.currentTrick,
                    isFirstTrick = gameState.currentTrick.trickNumber == 1,
                    heartsBroken = gameState.heartsBroken
                )
            } else emptyList()

            // Passing Phase Logic integration
            if (gameState.phase is GamePhase.Passing) {
                 PassingPhaseUI(
                    gameState = gameState,
                    onCardToggle = { viewModel.togglePassCard(it) },
                    onConfirmPass = { viewModel.confirmPass() }
                )
            } else {
                 PlayerHandLayout(
                    cards = humanPlayer.sortedHand,
                    selectedCards = emptyList(),
                    validPlays = validPlays,
                    isPlayerTurn = isHumanTurn,
                    gameSpeed = gameState.config.gameSpeed,
                    cardTheme = gameState.config.cardTheme,
                    onCardClick = { viewModel.playCard(it) },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }

        
        // --- Overlays ---
        if (gameState.phase is GamePhase.RoundComplete) {
            RoundCompleteOverlay(
                phase = gameState.phase as GamePhase.RoundComplete,
                gameState = gameState,
                showMoon = showMoon,
                onContinue = { viewModel.continueAfterRound() }
            )
        }
        
        if (gameState.phase is GamePhase.GameOver) {
             GameOverOverlay(
                phase = gameState.phase as GamePhase.GameOver,
                gameState = gameState,
                onNewGame = { viewModel.restartGame() },
                onBackToMenu = onBackToMenu
            )
        }
        
        // Stats Overlay
        if (showStats) {
            StatsOverlay(
                gameState = gameState,
                onDismiss = { showStats = false }
            )
        }
        
        // Error Snackbar
        AnimatedVisibility(
            visible = showError != null,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp)
        ) {
            showError?.let { error ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = ErrorShake)
                ) {
                   Text(
                        text = error,
                        color = Color.White,
                        modifier = Modifier.padding(12.dp)
                   )
                }
            }
        }
    }
}

// Retaining helper composables (PassingPhaseUI, RoundCompleteOverlay, GameOverOverlay) but updating their styles slightly if needed?
// For now, I will include abbreviated versions or just reuse the existing ones logic but assume they inherit the theme.
// To save space and ensure correctness, I'll basically keep them but wrap/style them.

@Composable
private fun PassingPhaseUI(
    gameState: GameState,
    onCardToggle: (Card) -> Unit,
    onConfirmPass: () -> Unit
) {
    val selectedCards = gameState.selectedCards
    // Simple overlay for passing button, hand is handled by main layout
    Box(
        modifier = Modifier.fillMaxWidth().height(200.dp),
        contentAlignment = Alignment.Center
    ) {
         Button(
                onClick = onConfirmPass,
                enabled = selectedCards.size == 3,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PlayerBadgeGold,
                    contentColor = PlayerBadgeText
                ),
                modifier = Modifier.offset(y = (-100).dp) // Float above hand
            ) {
                Text("CONFIRM PASS (${selectedCards.size}/3)")
            }
            
         // The hand itself is rendered by the parent for consistency, but we need to pass interactions.
         // Wait, the parent `PlayerHandLayout` call in the else block above handles PLAYING.
         // We need a version for PASSING.
         
          PlayerHandLayout(
            cards = gameState.humanPlayer?.sortedHand ?: emptyList(),
            selectedCards = selectedCards,
            validPlays = gameState.humanPlayer?.hand ?: emptyList(), // All valid
            isPlayerTurn = true,
            gameSpeed = gameState.config.gameSpeed,
            cardTheme = gameState.config.cardTheme,
            onCardClick = onCardToggle,
             modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun RoundCompleteOverlay(
    phase: GamePhase.RoundComplete,
    gameState: GameState,
    showMoon: Boolean,
    onContinue: () -> Unit
) {
    // Semi-transparent overlay
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable(enabled = false) {}, // Block clicks
        contentAlignment = Alignment.Center
    ) {
         Card(
            colors = CardDefaults.cardColors(containerColor = WarmRedBackground),
            modifier = Modifier.padding(32.dp)
         ) {
             Column(
                 modifier = Modifier.padding(24.dp),
                 horizontalAlignment = Alignment.CenterHorizontally
             ) {
                 Text("ROUND COMPLETE", style = MaterialTheme.typography.headlineMedium, color = LabelWhite)
                 Spacer(modifier = Modifier.height(16.dp))
                 // Simplified scoreboard for round
                 phase.roundScores.forEach { (pos, score) ->
                     Row(modifier = Modifier.fillMaxWidth().padding(horizontal=32.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                         Text(pos.displayName, color = LabelWhite)
                         Text("+$score", color = if(score>0) HeartRed else ActivePlayerGlow, fontWeight = FontWeight.Bold)
                     }
                 }
                 Spacer(modifier = Modifier.height(24.dp))
                 Button(
                     onClick = onContinue,
                     colors = ButtonDefaults.buttonColors(containerColor = PlayerBadgeGold)
                 ) {
                     Text("CONTINUE", color = PlayerBadgeText)
                 }
             }
         }
    }
}

@Composable
private fun GameOverOverlay(
    phase: GamePhase.GameOver,
    gameState: GameState,
    onNewGame: () -> Unit,
    onBackToMenu: () -> Unit
) {
     Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f)),
        contentAlignment = Alignment.Center
    ) {
         Column(horizontalAlignment = Alignment.CenterHorizontally) {
             Text(
                 if(phase.winner == PlayerPosition.SOUTH) "VICTORY!" else "GAME OVER",
                 style = MaterialTheme.typography.displayMedium,
                 color = if(phase.winner == PlayerPosition.SOUTH) ActivePlayerGlow else HeartRed
             )
             Spacer(modifier = Modifier.height(32.dp))
             Button(onClick = onNewGame, colors = ButtonDefaults.buttonColors(containerColor = PlayerBadgeGold)) {
                 Text("PLAY AGAIN", color = PlayerBadgeText)
             }
             TextButton(onClick = onBackToMenu) {
                 Text("EXIT", color = LabelWhite)
             }
         }
    }
}


@Composable
private fun StatsOverlay(
    gameState: GameState,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .clickable(enabled = false) {}, // Prevent dismiss
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "CURRENT SCORES",
                    color = PlayerBadgeGold,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                val players = remember(gameState.players) { gameState.players.values.sortedBy { it.totalScore } }
                
                players.forEach { player ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                         Row(verticalAlignment = Alignment.CenterVertically) {
                             Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(player.badgeColor, androidx.compose.foundation.shape.CircleShape),
                                contentAlignment = Alignment.Center
                             ) {
                                 Text(
                                    text = player.name.first().toString(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                 )
                             }
                             Spacer(modifier = Modifier.width(12.dp))
                             Text(
                                text = player.name,
                                color = if (player.isHuman) ActivePlayerGlow else LabelWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                             )
                         }
                        
                        Text(
                            text = "${player.totalScore}",
                            color = LabelWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = PlayerBadgeGold)
                ) {
                    Text("CLOSE", color = PlayerBadgeText)
                }
            }
        }
    }
}
