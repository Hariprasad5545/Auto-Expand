# Auto expand v1.3
Android app "Auto expand" (Kotlin) - Accessibility-based auto-expansion of notification shade with configurable delay, app selector, select all/deselect all, and boot receiver.

## Features
- Expands notifications **only** when a new notification arrives (listens for notification events).
- Expands **only when the device is unlocked**.
- Configurable delay (default 1 second) saved in settings.
- App selector: choose which apps trigger expansion. If selection is empty, treated as all apps allowed.
- Select All / Deselect All buttons for convenience.
- BootReceiver included: receives BOOT_COMPLETED (note: Accessibility must still be enabled manually by the user).
- Persistent settings using SharedPreferences.

## Build without Android Studio (Replit)
1. Upload this project ZIP to Replit (Import from ZIP).
2. In Replit shell run: `./gradlew assembleDebug`
3. Download the generated APK at `app/build/outputs/apk/debug/app-debug.apk`

## Safety
- No internet permission, no data exfiltration code.
- Accessibility service only performs a global action to expand the notification shade.
