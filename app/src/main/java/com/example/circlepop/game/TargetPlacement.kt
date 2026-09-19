package com.example.circlepop.game

import kotlin.math.hypot
import kotlin.random.Random

/** Computes a safe, randomized normalized position for a new target inside the play area. */
object TargetPlacement {

    /** Inner padding of the rounded play-area container, in dp. */
    const val CONTAINER_PADDING_DP = 20f

    /** Extra breathing room beyond the container padding, in dp. */
    private const val EXTRA_SAFETY_DP = 6f

    /** Minimum normalized distance from the previous target to avoid repeats in place. */
    private const val MIN_SEPARATION_FRACTION = 0.3f

    /**
     * Returns a new (xFraction, yFraction) center position, guaranteed to keep the whole
     * circle within the container bounds, and biased away from [previous] when possible.
     */
    fun randomPosition(
        containerWidthDp: Float,
        containerHeightDp: Float,
        circleSizeDp: Float,
        previous: Pair<Float, Float>?
    ): Pair<Float, Float> {
        val radius = circleSizeDp / 2f
        val margin = radius + CONTAINER_PADDING_DP + EXTRA_SAFETY_DP

        val minX = margin
        val maxX = (containerWidthDp - margin).coerceAtLeast(minX)
        val minY = margin
        val maxY = (containerHeightDp - margin).coerceAtLeast(minY)

        fun sample(): Pair<Float, Float> {
            val x = if (maxX > minX) Random.nextFloat() * (maxX - minX) + minX else containerWidthDp / 2f
            val y = if (maxY > minY) Random.nextFloat() * (maxY - minY) + minY else containerHeightDp / 2f
            val fx = if (containerWidthDp > 0f) (x / containerWidthDp).coerceIn(0f, 1f) else 0.5f
            val fy = if (containerHeightDp > 0f) (y / containerHeightDp).coerceIn(0f, 1f) else 0.5f
            return fx to fy
        }

        if (previous == null) return sample()

        var best = sample()
        var bestDistance = distanceBetween(best, previous)

        repeat(4) {
            if (bestDistance >= MIN_SEPARATION_FRACTION) return best
            val candidate = sample()
            val distance = distanceBetween(candidate, previous)
            if (distance > bestDistance) {
                best = candidate
                bestDistance = distance
            }
        }
        return best
    }

    private fun distanceBetween(a: Pair<Float, Float>, b: Pair<Float, Float>): Float =
        hypot(a.first - b.first, a.second - b.second)
}
