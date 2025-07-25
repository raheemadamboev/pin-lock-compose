package xyz.teamgravity.pin_lock_compose

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import xyz.teamgravity.pin_lock_compose.shake.ShakeController
import xyz.teamgravity.pin_lock_compose.shake.rememberShakeController
import xyz.teamgravity.pin_lock_compose.shake.shake

/**
 * Covers the whole screen and displays the PinLock. This composable makes the user enter pin if it already exists. If there is
 * no saved pin, it prompts the user to create pin.
 *
 * @param title
 * Container for title text. It passes the pinExists boolean, so you can decide what title to show. For pinExists value, it passes true
 * if pin already exists, false if pin does not exist.
 * @param color
 * Background color of container.
 * @param onPinCorrect
 * Gets called when entered pin matches genuine pin.
 * @param onPinIncorrect
 * Gets called when entered pin does not match genuine.
 * @param onPinCreated
 * Gets called when pin gets saved.
 */
@Composable
fun PinLock(
    title: @Composable (pinExists: Boolean) -> Unit,
    color: Color,
    onPinCorrect: () -> Unit,
    onPinIncorrect: () -> Unit,
    onPinCreated: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val controller = rememberShakeController()
    val numbers = rememberMutableStateListOf<Int>()
    val pinExists = remember { runBlocking { PinManager.pinExists() } }

    BasePinLock(
        title = {
            title(pinExists)
        },
        color = color,
        controller = controller,
        numbers = numbers,
        onNumberChange = { number ->
            scope.launch {
                if (numbers.size < PinConst.PIN_LENGTH) numbers.add(number)

                val immutableNumbers = numbers.toList()
                if (immutableNumbers.size == PinConst.PIN_LENGTH) {
                    if (PinManager.pinExists()) {
                        if (PinManager.checkPin(immutableNumbers)) {
                            onPinCorrect()
                            numbers.clear()
                        } else {
                            controller.incorrect()
                            onPinIncorrect()
                            numbers.clear()
                        }
                    } else {
                        PinManager.savePin(immutableNumbers)
                        onPinCreated()
                        numbers.clear()
                    }
                }
            }
        },
        onBackspace = {
            numbers.removeLastOrNull()
        }
    )
}

/**
 * Covers the whole screen and displays the PinLock. This composable first makes the user enter original pin. Once user successfully
 * authenticates with his pin, it prompts the user to create new pin. It replaces the old pin. This composable is meant to be used for
 * changing pin when there is already saved pin. Don't use this composable if there is no saved pin yet.
 * Use [xyz.teamgravity.pin_lock_compose.PinLock] instead for creating pin for the first time.
 *
 * @param title
 * Container for title text. It passes the authenticated boolean, so you can decide what title to show. For authenticated value, it passes
 * true once user authenticates using his pin. It passes false if user is not authenticated yet.
 * @param color
 * Background color of container.
 * @param onPinIncorrect
 * Gets called when entered pin does not match genuine pin.
 * @param onPinChanged
 * Gets called when pin is changed successfully.
 *
 */
@Composable
fun ChangePinLock(
    title: @Composable (authenticated: Boolean) -> Unit,
    color: Color,
    onPinIncorrect: () -> Unit,
    onPinChanged: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val controller = rememberShakeController()
    val numbers = rememberMutableStateListOf<Int>()
    var authenticated by rememberSaveable { mutableStateOf(false) }

    BasePinLock(
        title = {
            title(authenticated)
        },
        color = color,
        controller = controller,
        numbers = numbers,
        onNumberChange = { number ->
            scope.launch {
                if (numbers.size < PinConst.PIN_LENGTH) numbers.add(number)

                val immutableNumbers = numbers.toList()
                if (immutableNumbers.size == PinConst.PIN_LENGTH) {
                    if (authenticated) {
                        PinManager.savePin(immutableNumbers)
                        onPinChanged()
                        numbers.clear()
                    } else {
                        if (PinManager.checkPin(immutableNumbers)) {
                            numbers.clear()
                            authenticated = true
                        } else {
                            controller.incorrect()
                            onPinIncorrect()
                            numbers.clear()
                        }
                    }
                }
            }
        },
        onBackspace = {
            numbers.removeLastOrNull()
        }
    )
}

/**
 * This is base pin lock composable. This is dumb composable that only displays PinLock.
 *
 * @param title
 * Container for title text.
 * @param color
 * Background color of container.
 * @param controller
 * ShakeController that allows to animate incorrect animation.
 * @param numbers
 * List of int that represents entered pin.
 * @param onNumberChange
 * Gets called whenever number buttons are clicked. It also passes what number is clicked.
 * @param onBackspace
 * Get called whenever backspace button is clicked.
 */
@Composable
private fun BasePinLock(
    title: @Composable () -> Unit,
    color: Color,
    controller: ShakeController,
    numbers: List<Int>,
    onNumberChange: (number: Int) -> Unit,
    onBackspace: () -> Unit
) {
    val configuration = LocalConfiguration.current
    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> BasePinLockLandscape(
            title = title,
            color = color,
            controller = controller,
            numbers = numbers,
            onNumberChange = onNumberChange,
            onBackspace = onBackspace
        )

        else -> BasePinLockPortrait(
            title = title,
            color = color,
            controller = controller,
            numbers = numbers,
            onNumberChange = onNumberChange,
            onBackspace = onBackspace
        )
    }
}

@Composable
private fun BasePinLockLandscape(
    title: @Composable () -> Unit,
    color: Color,
    controller: ShakeController,
    numbers: List<Int>,
    onNumberChange: (number: Int) -> Unit,
    onBackspace: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxSize()
            .background(color)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TitleContainer(
                controller = controller,
                title = title
            )
            Spacer(
                modifier = Modifier.height(16.dp)
            )
            PinIndicatorContainer(
                controller = controller,
                filledCount = numbers.size
            )
        }
        NumberContainer(
            onNumberChange = onNumberChange,
            onBackspace = onBackspace
        )
    }
}

@Composable
private fun BasePinLockPortrait(
    title: @Composable () -> Unit,
    color: Color,
    controller: ShakeController,
    numbers: List<Int>,
    onNumberChange: (number: Int) -> Unit,
    onBackspace: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(color)
    ) {
        TitleContainer(
            controller = controller,
            title = title
        )
        Spacer(
            modifier = Modifier.height(16.dp)
        )
        PinIndicatorContainer(
            controller = controller,
            filledCount = numbers.size
        )
        Spacer(
            modifier = Modifier.height(16.dp)
        )
        NumberContainer(
            onNumberChange = onNumberChange,
            onBackspace = onBackspace
        )
    }
}

@Composable
private fun TitleContainer(
    controller: ShakeController,
    title: @Composable () -> Unit
) {
    Box(
        modifier = Modifier.shake(controller)
    ) {
        title()
    }
}

@Composable
private fun PinIndicatorContainer(
    controller: ShakeController,
    filledCount: Int
) {
    Row(
        modifier = Modifier.shake(controller)
    ) {
        PinIndicator(
            filled = filledCount > 0
        )
        PinIndicator(
            filled = filledCount > 1
        )
        PinIndicator(
            filled = filledCount > 2
        )
        PinIndicator(
            filled = filledCount > 3
        )
    }
}

/**
 * Indicator to display if pin is pressed.
 *
 * @param filled
 * If true, fills the indicator. If false, outlined indicator gets displayed.
 */
@Composable
private fun PinIndicator(
    filled: Boolean
) {
    Box(
        modifier = Modifier
            .padding(5.dp)
            .size(15.dp)
            .clip(CircleShape)
            .background(if (filled) Color.White else Color.Transparent)
            .border(2.dp, Color.White, CircleShape)
    )
}

@Composable
private fun NumberContainer(
    onNumberChange: (number: Int) -> Unit,
    onBackspace: () -> Unit
) {
    val density = LocalDensity.current
    var numbersRowWidthPx by remember { mutableIntStateOf(0) }
    val numbersRowWidthDp by remember { derivedStateOf { with(density) { numbersRowWidthPx.toDp() } } }

    Column {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            NumberButton(
                number = 1,
                onClick = onNumberChange
            )
            NumberButton(
                number = 2,
                onClick = onNumberChange
            )
            NumberButton(
                number = 3,
                onClick = onNumberChange
            )
        }
        Spacer(
            modifier = Modifier.height(10.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            NumberButton(
                number = 4,
                onClick = onNumberChange
            )
            NumberButton(
                number = 5,
                onClick = onNumberChange
            )
            NumberButton(
                number = 6,
                onClick = onNumberChange
            )
        }
        Spacer(
            modifier = Modifier.height(10.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.onGloballyPositioned { coordinate ->
                numbersRowWidthPx = coordinate.size.width
            }
        ) {
            NumberButton(
                number = 7,
                onClick = onNumberChange
            )
            NumberButton(
                number = 8,
                onClick = onNumberChange
            )
            NumberButton(
                number = 9,
                onClick = onNumberChange
            )
        }
        Spacer(
            modifier = Modifier.height(10.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(numbersRowWidthDp)
        ) {
            NumberButton(
                number = 0,
                onClick = {},
                modifier = Modifier.alpha(0F)
            )
            NumberButton(
                number = 0,
                onClick = onNumberChange
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.weight(1F)
            ) {
                IconButton(
                    onClick = onBackspace
                ) {
                    Icon(
                        imageVector = Backspace,
                        tint = Color.White,
                        contentDescription = stringResource(id = R.string.cd_backspace)
                    )
                }
            }
        }
    }
}

/**
 * Button that displays pin numbers.
 *
 * @param number
 * What number to use.
 * @param onClick
 * Gets called when button is clicked, also passes the clicked number.
 * @param modifier
 * Modifier.
 */
@Composable
private fun NumberButton(
    number: Int,
    onClick: (number: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = {
            onClick(number)
        },
        colors = ButtonDefaults.buttonColors(containerColor = NumberButtonBackground),
        contentPadding = PaddingValues(0.dp),
        modifier = modifier
            .size(78.dp)
            .padding(5.dp)
    ) {
        Text(
            text = number.toString(),
            color = Color.White,
            fontSize = 25.sp
        )
    }
}

/**
 * Background color of number button.
 */
private val NumberButtonBackground: Color = Color.White.copy(alpha = 0.3F)