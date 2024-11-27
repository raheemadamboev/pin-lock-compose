package xyz.teamgravity.pin_lock_compose.shake

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

internal fun Modifier.shake(controller: ShakeController): Modifier = composed {
    return@composed controller.config?.let { config ->
        val shake = remember { Animatable(0F) }

        LaunchedEffect(
            key1 = config,
            block = {
                (0..config.iterations).forEach { iteration ->
                    shake.animateTo(
                        targetValue = if (iteration % 2 == 0) 1F else -1F,
                        animationSpec = spring(
                            stiffness = config.intensity
                        )
                    )
                }
                shake.animateTo(
                    targetValue = 0F
                )
            }
        )

        return@let this
            .rotate(shake.value * config.rotate)
            .graphicsLayer {
                rotationX = shake.value * config.rotateX
                rotationY = shake.value * config.rotateY
            }
            .scale(
                scaleX = 1F + (shake.value * config.scaleX),
                scaleY = 1F + (shake.value * config.scaleY)
            )
            .offset {
                IntOffset(
                    x = (shake.value * config.translateX).roundToInt(),
                    y = (shake.value * config.translateY).roundToInt()
                )
            }
    } ?: this
}