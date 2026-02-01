package com.wakey.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Custom color palette for Wakey app.
 * Updated to match design mockups exactly.
 * 
 * Design Spec Colors:
 * - Background: #FEFEFE
 * - Card: #F4F4F5
 * - Text Primary: #1F1E2C
 * - Text Secondary: #9B9495
 * - Accent Peach: #ED9D6F
 * - Accent Coral: #E37D92
 * - Accent Gold: #F6C57D
 */

// ============================================
// PRIMARY COLORS - Coral/Pink Theme
// ============================================
val PrimaryCoral = Color(0xFFE37D92)         // Main coral/pink from design
val PrimaryCoralDark = Color(0xFFD66B80)     // Darker variant
val PrimaryCoralLight = Color(0xFFF5A5B5)    // Lighter tint

// ============================================
// SECONDARY & ACCENT COLORS
// ============================================
val AccentOrange = Color(0xFFED9D6F)         // Peach/Orange for progress rings
val AccentPeach = Color(0xFFED9D6F)          // Alias - Peach accent
val AccentGold = Color(0xFFF6C57D)           // Gold for highlights
val AccentCoral = Color(0xFFE37D92)          // Coral accent

// ============================================
// GRADIENT COLORS - For circular progress and buttons
// ============================================
val GradientOrangeStart = Color(0xFFED9D6F)  // Gradient start (top) - Peach
val GradientOrangeEnd = Color(0xFFF6C57D)    // Gradient end (bottom) - Gold
val GradientCoralStart = Color(0xFFE37D92)   // Coral gradient start
val GradientCoralEnd = Color(0xFFED9D6F)     // Coral to peach

/**
 * Gradient brush for circular progress indicators
 * Creates a warm orange-to-gold gradient effect
 */
val OrangeGradientBrush = Brush.verticalGradient(
    colors = listOf(GradientOrangeStart, GradientOrangeEnd)
)

/**
 * Gradient brush for primary buttons
 * Creates a coral-to-peach gradient effect
 */
val CoralGradientBrush = Brush.horizontalGradient(
    colors = listOf(GradientCoralStart, GradientCoralEnd)
)

/**
 * Gradient brush for circular time display border
 */
val CircleGradientBrush = Brush.sweepGradient(
    colors = listOf(
        GradientOrangeStart,
        GradientOrangeEnd,
        GradientOrangeStart.copy(alpha = 0.3f)
    )
)

// ============================================
// BACKGROUND COLORS (Light Theme Only)
// ============================================
val BackgroundLight = Color(0xFFFEFEFE)      // Almost white background (design spec)
val SurfaceLight = Color(0xFFFFFFFF)         // Pure white for cards
val SurfaceVariant = Color(0xFFF4F4F5)       // Card background (design spec)
val CardBackground = Color(0xFFF4F4F5)       // Explicit card background

// ============================================
// TEXT COLORS
// ============================================
val TextPrimary = Color(0xFF1F1E2C)          // Dark text (design spec)
val TextSecondary = Color(0xFF9B9495)        // Gray for subtitles (design spec)
val TextTertiary = Color(0xFFB0B0B0)         // Lighter gray for hints
val TextDisabled = Color(0xFFD0D0D0)         // Disabled text

// ============================================
// SWITCH/TOGGLE COLORS
// ============================================
val SwitchOnColor = Color(0xFFE37D92)        // Coral when ON
val SwitchOnTrack = Color(0xFFE37D92)        // Track color when ON
val SwitchOffColor = Color(0xFFE0E0E0)       // Light gray when OFF
val SwitchThumbColor = Color(0xFFFFFFFF)     // White thumb

// ============================================
// TASK TYPE COLORS
// ============================================
val StepsTaskColor = Color(0xFFED9D6F)       // Orange - Steps
val VerticalTaskColor = Color(0xFFE37D92)    // Coral - Hold Vertical
val DelayTaskColor = Color(0xFFF6C57D)       // Gold - Time Delay
val MathTaskColor = Color(0xFF64B5F6)        // Blue - Math Problem
val PhotoTaskColor = Color(0xFF81C784)       // Green - Take Photo
val VoiceTaskColor = Color(0xFFE37D92)       // Coral - Voice/Speak

// ============================================
// ALARM STATE COLORS
// ============================================
val AlarmActiveColor = Color(0xFFE37D92)     // Coral when active
val AlarmInactiveColor = Color(0xFFD0D0D0)   // Light gray when inactive

// ============================================
// UI ELEMENT COLORS
// ============================================
val ProgressIndicator = Color(0xFFED9D6F)    // Orange progress
val ProgressTrack = Color(0xFFEEEEEE)        // Light gray track
val ProgressTrackLight = Color(0xFFF0F0F0)   // Very light track
val DividerColor = Color(0xFFF0F0F0)         // Subtle dividers
val OutlineLight = Color(0xFFE8E8E8)         // Light borders
val ShadowColor = Color(0x0A000000)          // ~4% black for shadows

// ============================================
// CARD COLORS
// ============================================
val CardActive = Color(0xFFFFFFFF)           // White cards
val CardInactive = Color(0xFFF8F8F8)         // Slightly gray for inactive
val CardBorder = Color(0xFFF0F0F0)           // Subtle border
val CardElevated = Color(0xFFFFFFFF)         // Elevated card

// ============================================
// BUTTON COLORS
// ============================================
val ButtonPrimary = Color(0xFFE37D92)        // Primary button (coral)
val ButtonSecondary = Color(0xFFF4F4F5)      // Secondary button (light gray)
val ButtonDisabled = Color(0xFFE0E0E0)       // Disabled button
val ButtonDanger = Color(0xFFE37D92)         // Danger/Emergency button

// ============================================
// SPECIAL COLORS
// ============================================
val ErrorColor = Color(0xFFE37D92)           // Coral for errors
val SuccessColor = Color(0xFF4CAF50)         // Green for success
val WarningColor = Color(0xFFED9D6F)         // Orange for warnings
val InfoColor = Color(0xFF64B5F6)            // Blue for info
val EmergencyColor = Color(0xFFE37D92)       // Emergency dismiss

// ============================================
// ICON COLORS
// ============================================
val IconPrimary = Color(0xFF1F1E2C)          // Dark icons
val IconSecondary = Color(0xFF9B9495)        // Gray icons
val IconAccent = Color(0xFFED9D6F)           // Orange accent icons

// ============================================
// LEGACY COMPATIBILITY (Backwards compat)
// ============================================
val PrimaryGold = AccentGold                 // Alias for gold
val SecondaryPeach = AccentOrange            // Alias for orange
val TertiaryRose = PrimaryCoral              // Alias for coral

// Background aliases for dark theme (not used now, but kept for future)
val BackgroundDark = Color(0xFF1F1E2C)
val SurfaceDark = Color(0xFF2B2A38)
val SurfaceVariantDark = Color(0xFF3A3948)
val OnBackgroundLight = TextPrimary
val OnBackgroundDark = Color(0xFFFEFEFE)
val OnSurfaceLight = TextPrimary
val OnSurfaceDark = Color(0xFFFEFEFE)
val TextSecondaryLight = TextSecondary
val TextSecondaryDark = Color(0xFF9B9495)
val OutlineDark = Color(0xFF3A3948)
val SnoozeButtonColor = Color(0xFFD0D0D0)
val LockedButtonColor = Color(0xFFB0B0B0)
