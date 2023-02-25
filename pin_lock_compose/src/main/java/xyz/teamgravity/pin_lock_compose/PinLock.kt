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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.*

/**
 * Covers the whole screen and displays PinLock.
 *
 * @param title
 * Container for title text. It passes the pinExists boolean, so you can decide what title to show. For pinExists value, it passes true
 * if pin already exists, false if pin does not exist.
 * @param color
 * Background color of container.
 * @param onPinCorrect
 * Gets called when pressed pin matches genuine pin.
 * @param onPinIncorrect
 * Gets called when pressed pin does not match genuine.
 * @param onPinCreated
 * Gets called when pin gets saved.
 * @param onPinCreateError
 * Gets called when pin is not saved for some reason.
 */
@Composable
fun PinLock(
    title: @Composable (pinExists: Boolean) -> Unit,
    color: Color,
    onPinCorrect: () -> Unit,
    onPinIncorrect: () -> Unit,
    onPinCreated: () -> Unit,
    onPinCreateError: () -> Unit,
) {
    var animate by remember { mutableStateOf(false) }

    val numbers = remember { mutableStateListOf<Int>() }
    val pinExists = remember { PinManager.pinExists() }
    val numberHandler = remember {
        { number: Int ->
            if (numbers.size < PinConst.PIN_LENGTH) numbers.add(number)

            if (numbers.size == PinConst.PIN_LENGTH) {
                if (PinManager.pinExists()) {
                    if (PinManager.checkPin(numbers)) {
                        onPinCorrect()
                    } else {
                        animate = !animate
                        onPinIncorrect()
                        numbers.clear()
                    }
                } else {
                    if (PinManager.savePin(numbers)) onPinCreated() else onPinCreateError()
                }
            }
        }
    }

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
            title(pinExists)
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
            onClick = numberHandler,
            reference = oneB,
        )
        NumberButton(
            number = 2,
            onClick = numberHandler,
            reference = twoB,
            constrain = {
                linkTo(top = oneB.top, bottom = oneB.bottom)
            }
        )
        NumberButton(
            number = 3,
            onClick = numberHandler,
            reference = threeB,
            constrain = {
                linkTo(top = oneB.top, bottom = oneB.bottom)
            }
        )
        NumberButton(
            number = 4,
            onClick = numberHandler,
            reference = fourB,
        )
        NumberButton(
            number = 5,
            onClick = numberHandler,
            reference = fiveB,
            constrain = {
                linkTo(top = fourB.top, bottom = fourB.bottom)
            }
        )
        NumberButton(
            number = 6,
            onClick = numberHandler,
            reference = sixB,
            constrain = {
                linkTo(top = fourB.top, bottom = fourB.bottom)
            }
        )
        NumberButton(
            number = 7,
            onClick = numberHandler,
            reference = sevenB,
        )
        NumberButton(
            number = 8,
            onClick = numberHandler,
            reference = eightB,
            constrain = {
                linkTo(top = sevenB.top, bottom = sevenB.bottom)
            }
        )
        NumberButton(
            number = 9,
            onClick = numberHandler,
            reference = nineB,
            constrain = {
                linkTo(top = sevenB.top, bottom = sevenB.bottom)
            }
        )
        NumberButton(
            number = 0,
            onClick = numberHandler,
            reference = zeroB,
            constrain = {
                linkTo(start = eightB.start, end = eightB.end)
            }
        )
        IconButton(
            onClick = {
                numbers.removeLastOrNull()
            },
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