<h1 align="center">Pin Lock</h1>

<p align="center">
  <a href="http://developer.android.com/index.html"><img alt="Android" src="https://img.shields.io/badge/platform-android-green.svg"/></a>
  <a href="https://jitpack.io/#raheemadamboev/pin-lock-compose"><img alt="Version" src="https://jitpack.io/v/raheemadamboev/pin-lock-compose.svg"/></a>
  <a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
  <a href="https://android-arsenal.com/api?level=21"><img alt="API" src="https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat"/></a>
</p>

<p align="center">
🔐 Light library that is beautiful Pin Lock screen for Jetpack Compose. The library handles saving pin in Encrypted file. Integration is very easy and fast.
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

Firstly, initialize the library on your `onCreate` of Application class:
```kotlin
PinManager.initialize(this)
```

---

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
  onPinIncorrect = {
    // pin is incorrect, show error
  },
  onPinChanged = {
    // pin changed, navigate or hide pin lock
  }
)
```
Use this only if there is already saved pin. If there is no saved pin, use simple `PinLock` instead for creating pin for the first time. When using `ChangePinLock`, firstly it prompts the user to enter original pin. After user succesfully authenticates using his original pin, it prompts the user to creat a new pin:

<p align="center">
  <img width="296" height="600" src="https://github.com/raheemadamboev/pin-lock-compose/blob/master/banner_3.gif" />
</p>

# Features

- pin lock screen very easily
- handles encryption and saving pin internally
- change pin lock screen
- customizable background color
- robust to configuration changes
- backspace to remove last entered pin number
- incorrect indicator animation

# Demo

You can install and try demo app. All the features are implemented in the demo from creating pin to changing pin.

<a href="https://github.com/raheemadamboev/pin-lock-compose/blob/master/app-debug.apk">Download demo</a>

<p align="center">
  <img width="296" height="600" src="https://github.com/raheemadamboev/pin-lock-compose/blob/master/screenshot_1.jpg" />
  <img width="296" height="600" src="https://github.com/raheemadamboev/pin-lock-compose/blob/master/screenshot_2.jpg" />
</p>

# Projects using this library

**Notepad** -> 40 000+ downloads in <a href="https://play.google.com/store/apps/details?id=xyz.teamgravity.notepad">Google Play Store</a>

# Licence

```xml
Designed and developed by raheemadamboev (Raheem) 2023.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
