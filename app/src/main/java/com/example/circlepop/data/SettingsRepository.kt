package com.example.circlepop.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "circle_pop_settings")

/** Persists user-facing preferences: theme, sound, and haptic toggles. */
class SettingsRepository(private val context: Context) {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
    }

    val themeMode: Flow<ThemeMode> = context.settingsDataStore.data.map { prefs ->
        val raw = prefs[Keys.THEME_MODE]
        raw?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() } ?: ThemeMode.SYSTEM
    }

    val soundEnabled: Flow<Boolean> = context.settingsDataStore.data.map { prefs ->
        prefs[Keys.SOUND_ENABLED] ?: true
    }

    val hapticsEnabled: Flow<Boolean> = context.settingsDataStore.data.map { prefs ->
        prefs[Keys.HAPTICS_ENABLED] ?: true
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.settingsDataStore.edit { prefs -> prefs[Keys.THEME_MODE] = mode.name }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { prefs -> prefs[Keys.SOUND_ENABLED] = enabled }
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { prefs -> prefs[Keys.HAPTICS_ENABLED] = enabled }
    }

    suspend fun currentThemeModeBlocking(): ThemeMode = themeMode.first()
}
