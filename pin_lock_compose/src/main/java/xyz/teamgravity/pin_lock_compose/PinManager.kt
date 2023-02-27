package xyz.teamgravity.pin_lock_compose

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object PinManager {

    private const val NAME = "xyz.teamgravity.pin_lock_compose"
    private const val PIN_LOCK = "pin_lock"

    private var preferences: SharedPreferences? = null

    /**
     * Returns EncryptedSharedPreferences.
     *
     * @param context
     * Application context is preferred.
     *
     * @return EncryptedSharedPreferences.
     */
    private fun initializePreferences(context: Context): SharedPreferences {
        val key = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            NAME,
            key,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * Returns non null SharedPreferences.
     *
     * @return non null SharedPreferences.
     *
     * @throws IllegalStateException
     * If SharedPreferences is not initialized yet.
     */
    private fun getPreferences(): SharedPreferences {
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
     * Saves the pin in encrypted SharedPreferences.
     *
     * @param pin
     * List of pin numbers.
     */
    internal fun savePin(pin: List<Int>) {
        getPreferences().edit().putString(PIN_LOCK, fromIntList(pin)).apply()
    }

    /**
     * Checks the passed pin with saved pin.
     *
     * @return true if passed pin matches exactly as saved pin, false if pin does not match saved pin.
     *
     * @param pin
     * List of pin numbers.
     */
    internal fun checkPin(pin: List<Int>): Boolean {
        if (!pinExists()) return false
        val savedPin = getPreferences().getString(PIN_LOCK, null) ?: return false
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
    fun initialize(context: Context) {
        if (preferences == null) preferences = initializePreferences(context)
    }

    /**
     * Checks if there is already saved pin.
     *
     * @return true if there is already saved pin, false if there is no saved pin.
     */
    fun pinExists(): Boolean {
        return getPreferences().contains(PIN_LOCK)
    }
}