package xyz.teamgravity.pinlockcomposedemo

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import timber.log.Timber
import xyz.teamgravity.pin_lock_compose.ChangePinLock
import xyz.teamgravity.pin_lock_compose.PinLock
import xyz.teamgravity.pinlockcomposedemo.ui.theme.PinLockComposeDemoTheme

class MainActivity : ComponentActivity() {

    private companion object {
        const val TAG = "PinLock"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            @Suppress("DEPRECATION")
            window.navigationBarColor = "#6650A4".toColorInt()
        }

        setContent {
            PinLockComposeDemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var navigation by rememberSaveable { mutableStateOf(Screen.Authenticate) }

                    when (navigation) {
                        Screen.Authenticate -> {
                            PinLock(
                                title = { pinExists ->
                                    Text(
                                        text = if (pinExists) "Enter your pin" else "Create pin",
                                        color = Color.White,
                                        fontSize = 22.sp
                                    )
                                },
                                color = MaterialTheme.colorScheme.primary,
                                onPinCorrect = {
                                    // pin is correct, navigate or hide pin lock
                                    Timber.tag(TAG).d("Pin is correct")
                                    navigation = Screen.Main
                                },
                                onPinIncorrect = {
                                    // pin is incorrect, show error
                                    Timber.tag(TAG).d("Pin is incorrect")
                                },
                                onPinCreated = {
                                    // pin created for the first time, navigate or hide pin lock
                                    Timber.tag(TAG).d("Pin is created for the first time")
                                    navigation = Screen.Main
                                }
                            )
                        }

                        Screen.ChangePin -> {
                            ChangePinLock(
                                title = { authenticated ->
                                    Text(
                                        text = if (authenticated) "Enter new pin" else "Enter your pin",
                                        color = Color.White,
                                        fontSize = 22.sp
                                    )
                                },
                                color = MaterialTheme.colorScheme.primary,
                                onPinIncorrect = {
                                    // pin is incorrect, show error
                                    Timber.tag(TAG).d("Pin is incorrect")
                                },
                                onPinChanged = {
                                    // pin changed, navigate or hide pin lock
                                    Timber.tag(TAG).d("Pin is changed")
                                    navigation = Screen.Main
                                }
                            )
                        }

                        Screen.Main -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(
                                    space = 16.dp,
                                    alignment = Alignment.CenterVertically
                                ),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = "You entered pin correctly!"
                                )
                                Button(
                                    onClick = {
                                        navigation = Screen.Authenticate
                                    }
                                ) {
                                    Text(
                                        text = "Authenticate"
                                    )
                                }
                                Button(
                                    onClick = {
                                        navigation = Screen.ChangePin
                                    }
                                ) {
                                    Text(
                                        text = "Change Pin"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private enum class Screen {
        Authenticate,
        ChangePin,
        Main;
    }
}