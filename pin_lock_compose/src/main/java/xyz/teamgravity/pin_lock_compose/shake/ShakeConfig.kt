package xyz.teamgravity.pin_lock_compose.shake

internal data class ShakeConfig(
    val iterations: Int,
    val intensity: Float = 100_000F,
    val rotate: Float = 0F,
    val rotateX: Float = 0F,
    val rotateY: Float = 0F,
    val scaleX: Float = 0F,
    val scaleY: Float = 0F,
    val translateX: Float = 0F,
    val translateY: Float = 0F,
    val trigger: Long = System.currentTimeMillis()
)