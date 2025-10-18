package com.example.autoexpand

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            if (intent == null) return
            val action = intent.action
            if (action == Intent.ACTION_BOOT_COMPLETED || action == Intent.ACTION_QUICKBOOT_POWERON) {
                Log.d("BootReceiver", "Device booted - AutoExpand boot receiver triggered")
                // AccessibilityService cannot be started programmatically by apps for security reasons.
                // The user must enable the Accessibility Service in Settings -> Accessibility -> Auto expand.
                // This receiver is present to keep the app aware of boot and for potential future features.
            }
        } catch (ex: Exception) {
            Log.w("BootReceiver", "Error in BootReceiver: ${'$'}ex")
        }
    }
}
