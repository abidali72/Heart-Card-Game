package com.hearts.game.ui.theme

import androidx.compose.ui.graphics.Color

// Main Backgrounds
val WarmRedBackground = Color(0xFFBC4B4B) // Muted warm red from reference
val GameTableTeal = Color(0xFF216477)     // Deep teal/blue surface
val GameTableBorder = Color(0xFFF4C464)   // Thick golden-yellow border

// Table Theme Colors
// Green Theme
val TableGreenSurface = Color(0xFF2E7D32)
val TableGreenBackground = Color(0xFF1B5E20)
val TableGreenBorder = Color(0xFFF1C40F)

// Blue Theme
val TableBlueSurface = Color(0xFF1565C0)
val TableBlueBackground = Color(0xFF0D47A1)
val TableBlueBorder = Color(0xFF90CAF9)

// Red Theme
val TableRedSurface = Color(0xFFC62828)
val TableRedBackground = Color(0xFF3E2723) // Dark brown background for red table looks better
val TableRedBorder = Color(0xFFE57373)

// UI Accent Colors
val PlayerBadgeGold = Color(0xFFF4C464)   // Same as border for consistency
val PlayerBadgeText = Color(0xFF5A3A29)   // Dark brown for contrast on gold
val ButtonWhite = Color(0xFFFFFFFF)       // Top UI buttons

// Card Colors
val CardFaceWhite = Color(0xFFFFFFFF)
val CardBorder = Color(0xFF2A2A2A)        // Thin dark border
val CardBackBlue = Color(0xFF2B3A42)      // Dark slate for card backs if needed
val CardBackPattern = Color(0xFFE74C3C)   // Deep red pattern

// Suit Colors
val SuitBlack = Color(0xFF1A1A2E)         // Dark Navy/Black for Spades/Clubs
val SuitRed = Color(0xFFC0392B)           // Deep Red for Hearts/Diamonds

// Text Colors
val TextDark = Color(0xFF2C3E50)          // General dark text
val TextLight = Color(0xFFECF0F1)         // Light text on dark backgrounds
val LabelWhite = Color(0xFFFFFFFF)        // Playing surface text

// State Colors
val ActivePlayerGlow = Color(0xFFF1C40F)  // Bright Gold Glow
val ValidMoveHighlight = Color(0x40FFFFFF) // Subtle white highlight
val ErrorShake = Color(0xFFE74C3C)        // Red for errors

// Legacy Support (Mapping to new colors where appropriate or keeping if unique)
val DarkNavy = SuitBlack
val DeepGreen = GameTableTeal
val GoldAccent = PlayerBadgeGold
val ErrorRed = ErrorShake
val HeartRed = Color(0xFFE74C3C)          // Heart suit red / accent red
val DarkSurface = Color(0xFF1A2634)       // Dark card/surface background
val TextPrimary = Color(0xFFECF0F1)       // Primary text on dark backgrounds
val TextMuted = Color(0xFF7F8C8D)         // Muted/secondary text
val CardSurface = Color(0xFF2C3E50)       // Card surface background
val SuccessGreen = Color(0xFF27AE60)      // Easy difficulty / success
val WarningYellow = Color(0xFFF39C12)     // Medium difficulty / warning
val ScoreBarBg = Color(0xFF1A2634)        // Score bar background
val TextSecondary = TextMuted              // Secondary text alias
val HeartsBrokenRed = Color(0xFFC0392B)    // Red for broken hearts indicator
val GoldDark = Color(0xFFB48C3C)           // Darker gold for gradients

