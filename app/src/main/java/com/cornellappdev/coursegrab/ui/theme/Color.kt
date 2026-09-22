package com.cornellappdev.coursegrab.ui.theme

import androidx.compose.ui.graphics.Color

val Teal = Color(0xFF008577)
val TealDark = Color(0xFF00574B)
val Pink = Color(0xFFD81B60)
val RemovalRed = Color(0xFFCA4238)
/** Secondary text on the dark surfaces — the search bar and the notification modal. */
val LightText = Color(0xFF9EA7B3)

/**
 * Secondary text on the white surfaces. [LightText] cannot serve here: it is 2.43:1 against
 * white, below WCAG AA's 4.5:1 for normal text. This is 5.34:1. The two cannot be one color
 * — anything dark enough for white falls below 4.5:1 on [DarkEnough].
 */
val MutedText = Color(0xFF616C7A)
val StatusGreen = Color(0xFF47C753)
val DarkEnough = Color(0xFF1C1F23)

val CardBorder = Color(0xFFD1D5DA)

val PrimaryText = Color(0xDE000000)

object StatusColors {
    val open = StatusGreen
    val closed = RemovalRed
}
