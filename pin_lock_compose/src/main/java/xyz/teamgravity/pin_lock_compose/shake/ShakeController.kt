package xyz.teamgravity.pin_lock_compose.shake

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

internal class ShakeController {
    var config: ShakeConfig? by mutableStateOf(null)
        private set

    fun incorrect() {
        config = ShakeConfig(
            iterations = 4,
            intensity = 2_000F,
            rotateY = 15F,
            translateX = 40F
        )
    }
}