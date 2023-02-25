package xyz.teamgravity.pinlockcomposedemo

import android.app.Application
import xyz.teamgravity.pin_lock_compose.PinManager

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // initialize pin manager in application
        PinManager.initialize(this)
    }
}