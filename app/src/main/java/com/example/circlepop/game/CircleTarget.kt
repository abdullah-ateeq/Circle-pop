package com.example.circlepop.game

/**
 * A single spawned target. [id] is unique per spawn so a stale lifetime
 * coroutine can never affect a target that replaced it.
 */
data class CircleTarget(
    val id: Long,
    val xFraction: Float,
    val yFraction: Float,
    val sizeDp: Float,
    val colorIndex: Int
)
