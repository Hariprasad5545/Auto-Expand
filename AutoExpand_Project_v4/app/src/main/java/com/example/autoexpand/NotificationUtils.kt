package com.example.autoexpand

import android.app.KeyguardManager
import android.content.Context
import android.os.PowerManager

object NotificationUtils {
    fun isDeviceUnlocked(context: Context): Boolean {
        val km = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        // Device considered unlocked if keyguard is not locked and device is interactive
        return !km.isKeyguardLocked && pm.isInteractive
    }
}
