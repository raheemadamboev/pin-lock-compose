package xyz.teamgravity.pinlockcomposedemo

import android.app.Application
import timber.log.Timber
import xyz.teamgravity.coresdkandroid.crypto.CryptoManager
import xyz.teamgravity.coresdkandroid.preferences.Preferences
import xyz.teamgravity.pin_lock_compose.PinManager

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        // initialize pin manager in application
        PinManager.initialize(
            context = this,
            preferences = Preferences(
                crypto = CryptoManager(),
                context = this
            )
        )
    }
}