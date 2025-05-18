package xyz.teamgravity.pin_lock_compose

import android.content.Context
import kotlinx.coroutines.flow.first
import xyz.teamgravity.coresdkandroid.preferences.Preferences
import xyz.teamgravity.pin_lock_compose.preferences.PinPreferences
import xyz.teamgravity.pin_lock_compose.preferences.PreferencesMigration

object PinManager {

    private var preferences: Preferences? = null

    private fun getPreferences(): Preferences {
        return preferences ?: throw IllegalStateException("Do you forget to call initialize() first?")
    }

    /**
     * Joins list of int to String.
     *
     * @param pin
     * List of numbers.
     *
     * @return joined pin of string.
     *
     * @throws IllegalStateException
     * If list size does not match required pin length.
     */
    private fun fromIntList(pin: List<Int>): String {
        if (pin.size != PinConst.PIN_LENGTH) throw IllegalStateException("Pin size does not match length. Actual: ${pin.size}. Expected: ${PinConst.PIN_LENGTH}")
        return pin.joinToString { it.toString() }
    }

    ///////////////////////////////////////////////////////////////////////////
    // INTERNAL API
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Saves the pin in encrypted Preferences.
     *
     * @param pin
     * List of pin numbers.
     */
    internal suspend fun savePin(pin: List<Int>) {
        getPreferences().upsertString(
            key = PinPreferences.PinLock,
            value = fromIntList(pin)
        )
    }

    /**
     * Checks the passed pin with saved pin.
     *
     * @return true if passed pin matches exactly as saved pin, false if pin does not match saved pin.
     *
     * @param pin
     * List of pin numbers.
     */
    internal suspend fun checkPin(pin: List<Int>): Boolean {
        if (!pinExists()) return false
        val savedPin = getPreferences().getString(PinPreferences.PinLock, null).first() ?: return false
        return savedPin == fromIntList(pin)
    }

    ///////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Initializes the pin lock. Prefer calling this function inside Application class.
     *
     * @param context
     * Need context to initialize.
     */
    @Synchronized
    fun initialize(
        context: Context,
        preferences: Preferences
    ) {
        this.preferences = preferences
        PreferencesMigration().migrate(
            context = context,
            preferences = getPreferences()
        )
    }

    /**
     * Checks if there is already saved pin.
     *
     * @return true if there is already saved pin, false if there is no saved pin.
     */
    suspend fun pinExists(): Boolean {
        return getPreferences().getString(PinPreferences.PinLock).first() != null
    }

    /**
     * Clears the saved pin. By calling this function, you can clear the saved pin so that user can create a new pin without remembering
     * the saved pin.
     */
    suspend fun clearPin() {
        getPreferences().upsertString(
            key = PinPreferences.PinLock,
            value = null
        )
    }
}