package xyz.teamgravity.pinlockcomposedemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.teamgravity.pin_lock_compose.PinLock
import xyz.teamgravity.pinlockcomposedemo.ui.theme.PinLockComposeDemoTheme
import xyz.teamgravity.pinlockcomposedemo.ui.theme.Purple40

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.navigationBarColor = android.graphics.Color.parseColor("#6650a4")
        setContent {
            PinLockComposeDemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var authenticated by remember { mutableStateOf(false) }

                    if (!authenticated) {
                        PinLock(
                            title = { pinExists ->
                                Text(
                                    text = if (pinExists) "Enter your pin" else " Create pin",
                                    color = Color.White,
                                    fontSize = 22.sp
                                )
                            },
                            color = MaterialTheme.colorScheme.primary,
                            onPinCorrect = { // pin is correct, navigate or hide pin lock
                                authenticated = true
                            },
                            onPinIncorrect = {
                                // pin is incorrect, show error
                            },
                            onPinCreated = {
                                // pin created for the first time, navigate or hide pin lock
                                authenticated = true
                            },
                            onPinCreateError = {
                                // error occurred when creating pin, show error
                            }
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(text = "You entered pin correctly!")
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    authenticated = false
                                }
                            ) {
                                Text(text = "Show PinLock")
                            }
                        }
                    }
                }
            }
        }
    }
}