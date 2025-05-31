@file:Suppress("DEPRECATION")

package xyz.teamgravity.pin_lock_compose.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import xyz.teamgravity.coresdkandroid.preferences.Preferences

internal class PreferencesMigration {

    private companion object {
        const val NAME = "xyz.teamgravity.pin_lock_compose"
        const val PIN_LOCK = "pin_lock"
    }

    /**
     * Returns EncryptedSharedPreferences.
     *
     * @param context
     * Application context is preferred.
     *
     * @return EncryptedSharedPreferences.
     */
    private fun initializePreferences(context: Context): SharedPreferences? {
        val key = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        try {
            return EncryptedSharedPreferences.create(
                context,
                NAME,
                key,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Timber.e(e)
            return null
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////

    fun migrate(
        context: Context,
        preferences: Preferences
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val migrated = preferences.getBoolean(PinPreferences.Migrated).first() as Boolean

            if (migrated) {
                Timber.d("migrate(): already migrated. Aborted migrating.")
                return@launch
            }

            val deprecated = initializePreferences(context)
            val pinLock = deprecated?.getString(PIN_LOCK, null)

            if (pinLock != null) {
                preferences.upsertString(
                    key = PinPreferences.PinLock,
                    value = pinLock
                )
                deprecated.edit(commit = true) { remove(PIN_LOCK) }
            }

            preferences.upsertBoolean(
                key = PinPreferences.Migrated,
                value = true
            )
        }
    }
}