package com.example.autoexpand

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {
    private val PREFS = "auto_expand_prefs"
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ENABLED = "enabled"
        private const val KEY_DELAY_MS = "delay_ms"
        private const val KEY_SELECTED_APPS = "selected_apps"
        private const val DEFAULT_DELAY_MS = 1000L // 1 second default
    }

    fun isEnabled(): Boolean = prefs.getBoolean(KEY_ENABLED, true)
    fun setEnabled(v: Boolean) = prefs.edit().putBoolean(KEY_ENABLED, v).apply()

    fun getDelayMillis(): Long = prefs.getLong(KEY_DELAY_MS, DEFAULT_DELAY_MS)
    fun setDelayMillis(ms: Long) = prefs.edit().putLong(KEY_DELAY_MS, ms).apply()

    fun getSelectedApps(): Set<String> = prefs.getStringSet(KEY_SELECTED_APPS, emptySet()) ?: emptySet()
    fun setSelectedApps(set: Set<String>) = prefs.edit().putStringSet(KEY_SELECTED_APPS, set).apply()
}
