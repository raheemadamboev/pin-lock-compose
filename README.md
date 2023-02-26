<h1 align="center">Pin Lock</h1>

<p align="center">
  <a href="http://developer.android.com/index.html"><img alt="Android" src="https://img.shields.io/badge/platform-android-green.svg"/></a>
  <a href="https://jitpack.io/#raheemadamboev/pin-lock-compose"><img alt="Version" src="https://jitpack.io/v/raheemadamboev/pin-lock-compose.svg"/></a>
  <a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
  <a href="https://android-arsenal.com/api?level=21"><img alt="API" src="https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat"/></a>
</p>

<p align="center">
📻 Light library that is beautiful Pin Lock screen for Jetpack Compose. The library handles saving pin in Encrypted file. Integration is very easy and fast.
</p>

# Setup

Add it in your root **build.gradle** at the end of repositories:
```groovy
allprojects {
  repositories {
    maven { url 'https://jitpack.io' }
  }
}
```  

Include below dependency in build.gradle of application and sync it:
```groovy
implementation 'com.github.raheemadamboev:pin-lock-compose:1.0'
```
# Implementation

To add Pin Lock screen, add the `PinLock` composable to your compose area:
```kotlin
PinLock(
  title = { pinExists ->
    Text(text = if (pinExists) "Enter your pin" else "Create pin")
  },
  color = MaterialTheme.colorScheme.primary,
  onPinCorrect = {
    // pin is correct, navigate or hide pin lock
  },
  onPinIncorrect = {
     // pin is incorrect, show error
  },
  onPinCreated = {
     // pin created for the first time, navigate or hide pin lock
  }
)
```

If there is no saved pin yet, it promtps the user to create pin. If there is saved pin, it promts the user to enter its pin:

<p align="center">
  <img width="296" height="600" src="https://github.com/raheemadamboev/pin-lock-compose/blob/master/banner_1.gif" />
  <img width="296" height="600" src="https://github.com/raheemadamboev/pin-lock-compose/blob/master/banner_2.gif" />
</p>

---

It is also possible to change pin. Just add `ChangePinLock` composable to your compose area:
```kotlin
ChangePinLock(
  title = { authenticated ->
    Text(text = if (authenticated) "Enter new pin" else "Enter your pin")
  },
  color = MaterialTheme.colorScheme.primary,
  onPinCorrect = {
    // pin is incorrect, show error
  },
  onPinChanged = {
    // pin changed, navigate or hide pin lock
  }
)
```
