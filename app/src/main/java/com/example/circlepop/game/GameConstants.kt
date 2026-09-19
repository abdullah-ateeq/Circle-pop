package com.example.circlepop.game

object GameConstants {
    /** Must match the number of colors in ui.theme.TargetPalette. */
    const val TARGET_COLOR_COUNT = 8

    /** How long the "MISSED!" feedback plays before navigating to the Result screen. */
    const val MISS_FEEDBACK_DURATION_MS = 650L

    /** How often the lifetime progress ring is refreshed. */
    const val PROGRESS_TICK_MS = 16L
}
