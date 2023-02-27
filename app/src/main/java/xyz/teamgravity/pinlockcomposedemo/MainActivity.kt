package xyz.teamgravity.pinlockcomposedemo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
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
import xyz.teamgravity.pin_lock_compose.ChangePinLock
import xyz.teamgravity.pin_lock_compose.PinLock
import xyz.teamgravity.pinlockcomposedemo.ui.theme.PinLockComposeDemoTheme

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "Tate"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.navigationBarColor = android.graphics.Color.parseColor("#6650a4")
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
                                    Log.d(TAG, "Pin is correct")
                                    navigation = Screen.Main
                                },
                                onPinIncorrect = {
                                    // pin is incorrect, show error
                                    Log.d(TAG, "Pin is incorrect")
                                },
                                onPinCreated = {
                                    // pin created for the first time, navigate or hide pin lock
                                    Log.d(TAG, "Pin is created for the first time")
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
                                    Log.d(TAG, "Pin is incorrect")
                                },
                                onPinChanged = {
                                    // pin changed, navigate or hide pin lock
                                    Log.d(TAG, "Pin is changed")
                                    navigation = Screen.Main
                                }
                            )
                        }

                        Screen.Main -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(text = "You entered pin correctly!")
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        navigation = Screen.Authenticate
                                    }
                                ) {
                                    Text(text = "Authenticate")
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        navigation = Screen.ChangePin
                                    }
                                ) {
                                    Text(text = "Change Pin")
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
        Main
    }
}