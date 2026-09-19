# 🎯 Circle Pop

<p align="center">
  <b>Circle Pop</b> is a fast-paced reflex arcade game for Android built with Kotlin and Jetpack Compose.
  <br />
  Test your reaction speed, build high score streaks, and pop colorful targets before time runs out!
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-green.svg" alt="Platform" />
  <img src="https://img.shields.io/badge/Language-Kotlin-purple.svg" alt="Language" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-blue.svg" alt="Jetpack Compose" />
  <img src="https://img.shields.io/badge/Design-Material%203-orange.svg" alt="Material 3" />
  <img src="https://img.shields.io/badge/Min%20SDK-24%2B-brightgreen.svg" alt="Min SDK" />
  <img src="https://img.shields.io/badge/Target%20SDK-37-blue.svg" alt="Target SDK" />
</p>

---

## ✨ Features

- ⚡ **Fast Reflex Gameplay** — Circle targets spawn in random locations and shrink over time. Tap them before they vanish!
- 📈 **Adaptive Difficulty** — Target lifetime decreases and sizes shrink as your score rises, ramping up the challenge naturally.
- 🔥 **Streak System** — Keep your momentum going by popping consecutive targets to increase your streak multiplier.
- 🏆 **Local Stats Persistence** — Saved high scores, best streaks, and total games played backed by Jetpack DataStore Preferences.
- 🎨 **Material 3 Custom Themes** — Smooth switching between Light Mode, Dark Mode, and System Default options.
- 🔊 **Audio & Haptic Feedback** — Custom sound effects for pops, misses, and new records alongside tactile haptic feedback.
- ✈️ **Fully Offline** — No internet permissions required, lightweight, fast startup, and privacy-first.
- 📱 **Modern Android Standard** — Pure Jetpack Compose UI with edge-to-edge layout, coroutine flow animations, and state management.

---

## 🎮 How to Play

1. Tap **Play** on the home screen to start a run.
2. A colored circle target appears on screen with a shrinking timer indicator.
3. Tap the circle target before time runs out to earn points and advance your streak.
4. If time expires before you tap the target, the game ends!
5. Beat your high score and unlock celebratory animations when setting a new record!

---

## 🛠️ Tech Stack & Architecture

- **Language:** [Kotlin](https://kotlinlang.org/)
- **UI Toolkit:** [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material 3 components
- **Architecture:** Unidirectional Data Flow (UDF) with MVVM pattern
  - `CirclePopViewModel` managing game state, sound, haptics, and repositories
  - Coroutines & `StateFlow` / `SharedFlow` for reactive game loops and single-shot events
- **Navigation:** [Jetpack Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
- **Data Persistence:** [Jetpack DataStore Preferences](https://developer.android.com/topic/libraries/architecture/datastore) for settings and statistics
- **Audio & Haptics:** Android `SoundPool` for ultra-low latency sound triggers & `Vibrator` API for tactile feedback

---

## 📁 Project Structure

```text
com.example.circlepop/
├── data/               # Local persistence (SettingsRepository, StatsRepository, ThemeMode)
├── game/               # Core game engine, logic, difficulty scaling, haptics & sound manager
│   ├── CirclePopViewModel.kt
│   ├── CirclePopGameState.kt
│   ├── Difficulty.kt
│   ├── HapticsController.kt
│   └── SoundManager.kt
├── navigation/         # Compose Navigation graph & route definitions
├── ui/
│   ├── components/     # Reusable UI elements (TargetCircle, PopEffectView, PlayButton, etc.)
│   ├── screens/        # Game screens (SplashScreen, HomeScreen, GameScreen, ResultScreen, SettingsScreen, AboutScreen)
│   └── theme/          # Color schemes, typography, and Material3 theme configuration
└── MainActivity.kt     # Single Activity entry point with edge-to-edge integration
```

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio:** Ladybug (2024.2.1) or newer recommended
- **JDK:** Java 11 or higher
- **Android Device / Emulator:** API level 24 (Android 7.0) or higher

### Building & Running

1. Clone the repository:
   ```bash
   git clone https://github.com/abdullah-ateeq/Circle-pop.git
   cd Circle-pop
   ```

2. Open the project in **Android Studio**.

3. Let Gradle sync dependencies.

4. Build and run the app on an emulator or physical device:
   ```bash
   ./gradlew installDebug
   ```

---

## 📄 License

```text
Copyright 2025 Abdullah Ateeq

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
