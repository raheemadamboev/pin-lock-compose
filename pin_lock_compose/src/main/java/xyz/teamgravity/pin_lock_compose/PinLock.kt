package xyz.teamgravity.pin_lock_compose

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.*

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
    onPinCreated: () -> Unit,
) {
    var animate by remember { mutableStateOf(false) }

    val numbers = rememberMutableStateListOf<Int>()
    val pinExists = remember { PinManager.pinExists() }

    BasePinLock(
        title = {
            title(pinExists)
        },
        color = color,
        animate = animate,
        numbers = numbers,
        onNumberChange = { number ->
            if (numbers.size < PinConst.PIN_LENGTH) numbers.add(number)

            if (numbers.size == PinConst.PIN_LENGTH) {
                if (PinManager.pinExists()) {
                    if (PinManager.checkPin(numbers)) {
                        onPinCorrect()
                        numbers.clear()
                    } else {
                        animate = !animate
                        onPinIncorrect()
                        numbers.clear()
                    }
                } else {
                    PinManager.savePin(numbers)
                    onPinCreated()
                    numbers.clear()
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
    onPinChanged: () -> Unit,
) {
    var authenticated by rememberSaveable { mutableStateOf(false) }
    var animate by remember { mutableStateOf(false) }

    val numbers = rememberMutableStateListOf<Int>()

    BasePinLock(
        title = {
            title(authenticated)
        },
        color = color,
        animate = animate,
        numbers = numbers,
        onNumberChange = { number ->
            if (numbers.size < PinConst.PIN_LENGTH) numbers.add(number)

            if (numbers.size == PinConst.PIN_LENGTH) {
                if (authenticated) {
                    PinManager.savePin(numbers)
                    onPinChanged()
                    numbers.clear()
                } else {
                    if (PinManager.checkPin(numbers)) {
                        numbers.clear()
                        authenticated = true
                    } else {
                        animate = !animate
                        onPinIncorrect()
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
 * This is base pin lock composable. This is dumb composable that only displays PinLock.
 *
 * @param title
 * Container for title text.
 * @param color
 * Background color of container.
 * @param animate
 * Whenever this value changes, incorrect pin pattern animation gets executed on pin indicator.
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
    animate: Boolean,
    numbers: List<Int>,
    onNumberChange: (number: Int) -> Unit,
    onBackspace: () -> Unit,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
    ) {
        val (titleC, numberC) = createRefs()
        val (oneS, twoS) = createRefs()
        val (oneB, twoB, threeB, fourB, fiveB, sixB, sevenB, eightB, nineB, zeroB, clearB) = createRefs()

        Box(
            modifier = Modifier.constrainAs(titleC) {
                linkTo(start = parent.start, end = parent.end)
            },
        ) {
            title()
        }
        Spacer(
            modifier = Modifier.constrainAs(oneS) {
                height = Dimension.value(16.dp)
                width = Dimension.matchParent
            }
        )
        AnimatedContent(
            targetState = animate,
            transitionSpec = {
                slideInHorizontally(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioHighBouncy,
                        stiffness = Spring.StiffnessHigh
                    )
                ) { 50 }.with(
                    slideOutHorizontally(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioHighBouncy,
                            stiffness = Spring.StiffnessHigh
                        )
                    ) { 0 }
                )
            },
            modifier = Modifier.constrainAs(numberC) {
                width = Dimension.matchParent
            }
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                PinIndicator(filled = numbers.size > 0)
                PinIndicator(filled = numbers.size > 1)
                PinIndicator(filled = numbers.size > 2)
                PinIndicator(filled = numbers.size > 3)
            }
        }
        Spacer(
            modifier = Modifier.constrainAs(twoS) {
                height = Dimension.value(16.dp)
                width = Dimension.matchParent
            }
        )
        NumberButton(
            number = 1,
            onClick = onNumberChange,
            reference = oneB,
        )
        NumberButton(
            number = 2,
            onClick = onNumberChange,
            reference = twoB,
            constrain = {
                linkTo(top = oneB.top, bottom = oneB.bottom)
            }
        )
        NumberButton(
            number = 3,
            onClick = onNumberChange,
            reference = threeB,
            constrain = {
                linkTo(top = oneB.top, bottom = oneB.bottom)
            }
        )
        NumberButton(
            number = 4,
            onClick = onNumberChange,
            reference = fourB,
        )
        NumberButton(
            number = 5,
            onClick = onNumberChange,
            reference = fiveB,
            constrain = {
                linkTo(top = fourB.top, bottom = fourB.bottom)
            }
        )
        NumberButton(
            number = 6,
            onClick = onNumberChange,
            reference = sixB,
            constrain = {
                linkTo(top = fourB.top, bottom = fourB.bottom)
            }
        )
        NumberButton(
            number = 7,
            onClick = onNumberChange,
            reference = sevenB,
        )
        NumberButton(
            number = 8,
            onClick = onNumberChange,
            reference = eightB,
            constrain = {
                linkTo(top = sevenB.top, bottom = sevenB.bottom)
            }
        )
        NumberButton(
            number = 9,
            onClick = onNumberChange,
            reference = nineB,
            constrain = {
                linkTo(top = sevenB.top, bottom = sevenB.bottom)
            }
        )
        NumberButton(
            number = 0,
            onClick = onNumberChange,
            reference = zeroB,
            constrain = {
                linkTo(start = eightB.start, end = eightB.end)
            }
        )
        IconButton(
            onClick = onBackspace,
            modifier = Modifier.constrainAs(clearB) {
                linkTo(start = nineB.start, end = nineB.end)
                linkTo(top = zeroB.top, bottom = zeroB.bottom)
            }
        ) {
            Icon(
                imageVector = Backspace,
                tint = Color.White,
                contentDescription = stringResource(id = R.string.cd_backspace)
            )
        }

        createVerticalChain(titleC, oneS, numberC, twoS, oneB, fourB, sevenB, zeroB, chainStyle = ChainStyle.Packed)
        createHorizontalChain(oneB, twoB, threeB, chainStyle = ChainStyle.Packed)
        createHorizontalChain(fourB, fiveB, sixB, chainStyle = ChainStyle.Packed)
        createHorizontalChain(sevenB, eightB, nineB, chainStyle = ChainStyle.Packed)
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
    filled: Boolean,
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

/**
 * Button that displays pin numbers.
 *
 * @param number
 * What number to use.
 * @param onClick
 * Gets called when button is clicked, also passes the clicked number.
 * @param reference
 * Reference of composable.
 * @param constrain
 * Constrain block to construct composable using ConstraintLayout.
 */
@Composable
private fun ConstraintLayoutScope.NumberButton(
    number: Int,
    onClick: (number: Int) -> Unit,
    reference: ConstrainedLayoutReference,
    constrain: ConstrainScope.() -> Unit = {},
) {
    Button(
        onClick = {
            onClick(number)
        },
        colors = ButtonDefaults.buttonColors(containerColor = NumberButtonBackground),
        modifier = Modifier
            .size(90.dp)
            .padding(10.dp)
            .constrainAs(reference, constrain)
    ) {
        Text(
            text = number.toString(),
            color = Color.White,
        )
    }
}

/**
 * Background color of number button.
 */
private val NumberButtonBackground = Color.White.copy(alpha = 0.3F)