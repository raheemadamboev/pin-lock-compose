package xyz.teamgravity.pin_lock_compose.preferences

import xyz.teamgravity.coresdkandroid.preferences.PreferencesKey

enum class PinPreferences(
    override val key: String,
    override val default: Any?,
    override val encrypted: Boolean
) : PreferencesKey {
    PinLock(
        key = "xyz.teamgravity.pin_lock_compose.PinLock",
        default = null,
        encrypted = true
    );
}