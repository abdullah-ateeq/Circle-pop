package com.example.circlepop.game

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.circlepop.R

/** Lightweight sound effect playback for pop / miss / new-best cues. No background music. */
class SoundManager(context: Context) {

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(4)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val popSoundId = soundPool.load(context, R.raw.pop, 1)
    private val missSoundId = soundPool.load(context, R.raw.miss, 1)
    private val newBestSoundId = soundPool.load(context, R.raw.new_best, 1)

    fun playPop() {
        soundPool.play(popSoundId, 0.7f, 0.7f, 1, 0, 1f)
    }

    fun playMiss() {
        soundPool.play(missSoundId, 0.8f, 0.8f, 1, 0, 1f)
    }

    fun playNewBest() {
        soundPool.play(newBestSoundId, 0.9f, 0.9f, 1, 0, 1f)
    }

    fun release() {
        soundPool.release()
    }
}
