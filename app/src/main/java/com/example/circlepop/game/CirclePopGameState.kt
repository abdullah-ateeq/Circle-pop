package com.example.circlepop.game

data class CirclePopGameState(
    val score: Int = 0,
    val streak: Int = 0,
    val target: CircleTarget? = null,
    val missedTarget: CircleTarget? = null,
    val lifetimeProgress: Float = 1f,
    val isGameActive: Boolean = false,
    val isNewBest: Boolean = false
) {
    val isCircleActive: Boolean get() = target != null
}
