package com.example.circlepop.game

/** Maps current score to target lifetime and size. Harder, never frustrating. */
object Difficulty {

    fun lifetimeMillisFor(score: Int): Long = when {
        score <= 5 -> 2200L
        score <= 15 -> 1800L
        score <= 30 -> 1500L
        score <= 50 -> 1250L
        else -> 1000L
    }

    fun targetSizeDpFor(score: Int): Float = when {
        score <= 5 -> 80f
        score <= 15 -> 74f
        score <= 30 -> 68f
        score <= 50 -> 62f
        else -> 58f
    }
}
