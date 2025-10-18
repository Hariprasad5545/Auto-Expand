package com.example.autoexpand

import android.accessibilityservice.AccessibilityService
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class AutoExpandService : AccessibilityService() {

    companion object {
        @Volatile
        var instance: AutoExpandService? = null
    }

    private val mainHandler = Handler(Looper.getMainLooper())

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.d("AutoExpandService", "Service connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        try {
            if (event == null) return
            val type = event.eventType
            if (type == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
                val pkg = event.packageName?.toString() ?: return
                Log.d("AutoExpandService", "Notification event from: $pkg")
                val settings = SettingsManager(this)
                if (!settings.isEnabled()) {
                    Log.d("AutoExpandService", "Feature disabled in settings")
                    return
                }
                val selected = settings.getSelectedApps()
                // If non-empty -> only expand for selected apps
                if (selected.isNotEmpty() && !selected.contains(pkg)) {
                    Log.d("AutoExpandService", "Package not selected: $pkg")
                    return
                }
                // Only expand when device unlocked
                if (!NotificationUtils.isDeviceUnlocked(this)) {
                    Log.d("AutoExpandService", "Device is locked - skipping expand")
                    return
                }
                val delayMs = settings.getDelayMillis()
                mainHandler.postDelayed({
                    try {
                        performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS)
                        Log.d("AutoExpandService", "Expanded notifications after ${'$'}delayMs ms for $pkg")
                    } catch (t: Throwable) {
                        Log.w("AutoExpandService", "Failed to expand: ${'$'}t")
                    }
                }, delayMs)
            }
        } catch (ex: Exception) {
            Log.w("AutoExpandService", "Error in onAccessibilityEvent: ${'$'}ex")
        }
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }
}
