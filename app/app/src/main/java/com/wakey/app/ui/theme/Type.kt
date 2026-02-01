package com.wakey.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Custom Typography for Wakey app.
 * 
 * Design Spec:
 * - Headlines: Montserrat (Light 300, Bold 700)
 * - Body: Inter
 * 
 * Note: Using system fonts as fallback. For production, add font files to res/font/
 * and uncomment the Font() declarations below.
 */

// ============================================
// FONT FAMILIES
// ============================================

/**
 * Montserrat font family for headlines and display text.
 * Clean, modern sans-serif with excellent readability.
 * 
 * To use custom fonts, add these files to res/font/:
 * - montserrat_light.ttf (weight 300)
 * - montserrat_regular.ttf (weight 400)
 * - montserrat_medium.ttf (weight 500)
 * - montserrat_semibold.ttf (weight 600)
 * - montserrat_bold.ttf (weight 700)
 */
// Using SansSerif as fallback until custom fonts are added
// To use custom fonts, add font files to res/font/ and create FontFamily with Font() declarations
val MontserratFontFamily = FontFamily.SansSerif

/**
 * Inter font family for body text and UI elements.
 * Highly legible at small sizes, perfect for mobile.
 * 
 * To use custom fonts, add these files to res/font/:
 * - inter_regular.ttf (weight 400)
 * - inter_medium.ttf (weight 500)
 * - inter_semibold.ttf (weight 600)
 * - inter_bold.ttf (weight 700)
 */
// Using Default as fallback until custom fonts are added
// To use custom fonts, add font files to res/font/ and create FontFamily with Font() declarations
val InterFontFamily = FontFamily.Default

// ============================================
// TYPOGRAPHY DEFINITIONS
// ============================================

/**
 * Complete Typography system for Wakey app.
 * Follows Material 3 guidelines with custom font families.
 */
val Typography = Typography(
    // ========== DISPLAY STYLES ==========
    // Used for very large text, like alarm time display
    displayLarge = TextStyle(
        fontFamily = MontserratFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 72.sp,
        lineHeight = 80.sp,
        letterSpacing = (-0.5).sp
    ),
    displayMedium = TextStyle(
        fontFamily = MontserratFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 56.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displaySmall = TextStyle(
        fontFamily = MontserratFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 44.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    
    // ========== HEADLINE STYLES ==========
    // Used for section headers and titles
    headlineLarge = TextStyle(
        fontFamily = MontserratFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = MontserratFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = MontserratFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    
    // ========== TITLE STYLES ==========
    // Used for card titles, dialog titles
    titleLarge = TextStyle(
        fontFamily = MontserratFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    
    // ========== BODY STYLES ==========
    // Used for main content text
    bodyLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    
    // ========== LABEL STYLES ==========
    // Used for buttons, chips, badges
    labelLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

// ============================================
// CUSTOM TEXT STYLES (for specific use cases)
// ============================================

/**
 * Extra large time display style for alarm ringing screen
 */
val TimeDisplayStyle = TextStyle(
    fontFamily = MontserratFontFamily,
    fontWeight = FontWeight.Light,
    fontSize = 72.sp,
    lineHeight = 80.sp,
    letterSpacing = (-1).sp
)

/**
 * Alarm card time style
 */
val AlarmTimeStyle = TextStyle(
    fontFamily = MontserratFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 48.sp,
    lineHeight = 56.sp,
    letterSpacing = (-0.5).sp
)

/**
 * Section header style (e.g., "WAKE UP MISSION")
 */
val SectionHeaderStyle = TextStyle(
    fontFamily = InterFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 1.sp
)

/**
 * Badge/chip text style
 */
val BadgeTextStyle = TextStyle(
    fontFamily = InterFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.4.sp
)

/**
 * Button text style
 */
val ButtonTextStyle = TextStyle(
    fontFamily = InterFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.5.sp
)
