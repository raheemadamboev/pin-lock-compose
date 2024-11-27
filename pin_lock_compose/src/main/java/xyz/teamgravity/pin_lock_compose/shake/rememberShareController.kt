package xyz.teamgravity.pin_lock_compose.shake

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
internal fun rememberShakeController(): ShakeController {
    return remember { ShakeController() }
}