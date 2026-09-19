package com.example.circlepop.game

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/** Wraps the system vibrator with the three feedback intensities this game needs. */
class HapticsController(context: Context) {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        manager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    fun lightPop() = vibrateOneShot(durationMs = 12, amplitude = 90)

    fun strongMiss() = vibrateOneShot(durationMs = 45, amplitude = 220)

    fun successPulse() {
        val v = vibrator ?: return
        if (!v.hasVibrator()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timings = longArrayOf(0, 30, 60, 30)
            val amplitudes = intArrayOf(0, 150, 0, 220)
            v.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
        } else {
            @Suppress("DEPRECATION")
            v.vibrate(longArrayOf(0, 30, 60, 30), -1)
        }
    }

    private fun vibrateOneShot(durationMs: Long, amplitude: Int) {
        val v = vibrator ?: return
        if (!v.hasVibrator()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(durationMs, amplitude))
        } else {
            @Suppress("DEPRECATION")
            v.vibrate(durationMs)
        }
    }
}
