# Kova

**Kova** is a premium, Google Keep-style notes app for Android, built with
Kotlin and Jetpack Compose. It is 100% local-first: notes never leave your
device.

## Privacy statement

- **No INTERNET permission.** The app cannot send or receive network data.
- **No analytics, no ads, no tracking.** There is no third-party SDK of any kind.
- **No account.** No sign-in, no cloud, no sync. Notes live in a local Room
database (`kova.db`) on your device only.
- "Clear all data" in Settings permanently and locally wipes everything.

## Features

- Splash screen with the Kova logo, tagline ("Your thoughts. Yours alone."),
privacy feature bullets, and a Continue button; auto-advances after ~2s with
a fade+slide transition.
- Home screen with live search filtering, a two-column staggered grid of
colored note cards (title, body preview, pin indicator), and staggered card
entrance animations.
- Navigation drawer (Notes / Archive / Trash / Settings) with logo header and
smooth slide animation.
- Full-screen note editor: title + body, 8-color palette plus default, pin
toggle, delete-to-trash, auto-save on leave.
- Long-press or overflow menu on cards: pin/unpin, archive/unarchive, change
color, delete.
- Trash view with restore, delete-forever, and empty-trash actions.
- Settings: System/Light/Dark theme (persisted in SharedPreferences),
clear-all-data with confirmation, About section.
- Material 3 design with the brand indigo `#6C5CE7`, dark/light themes,
FAB and editor scale+fade transitions, empty states with icons.
- No emoji anywhere in the UI; icons come exclusively from the Material icon set.

## Tech stack

| Component            | Version      |
| -------------------- | ------------ |
| Kotlin               | 1.9.22       |
| Android Gradle Plugin| 8.2.2        |
| Gradle               | 8.2          |
| compileSdk / targetSdk | 34         |
| minSdk               | 26           |
| Compose BOM          | 2024.02.01   |
| Material 3           | via BOM      |
| Navigation Compose   | 2.7.7        |
| Room (KSP)           | 2.6.1 / 1.9.22-1.4.1.6 |
| Lifecycle viewmodel-compose | 2.7.0 |
| kotlinx-coroutines   | 1.7.3        |

Single Activity (`MainActivity`), Compose-only navigation with routes:
`splash`, `home`, `editor/{id}` (`-1` creates a new note), `settings`.

## Building in Android Studio

1. Open Android Studio (Hedgehog 2023.1.1 or newer, bundled JDK 17).
2. **File > Open...** and select the `kova-android` folder.
3. Let Gradle sync (it will download Gradle 8.2 and dependencies on first run).
4. Run the `app` configuration on an emulator or device (API 26+).

Or from the command line:

```bash
gradle assembleDebug
```

> Note: this repository intentionally contains no `gradlew` binaries or
> wrapper JAR. In Android Studio the IDE's bundled Gradle handles the build,
> or generate a wrapper locally with `gradle wrapper --gradle-version 8.2`.

## CI builds

GitHub Actions (see `.github/workflows/android.yml`) builds Kova on every
push using JDK 17 + Gradle 8.2. The debug APK is output at
`app/build/outputs/apk/debug/app-debug.apk` and uploaded as a workflow
artifact; pushing a `v*` tag also attaches the APKs to a GitHub Release.

## Architecture

```
app/src/main/java/com/kova/app/
+-- MainActivity.kt          # single activity, theme + NavHost
+-- data/                    # Room entity, DAO, DB, repository, settings store
+-- ui/
|   +-- theme/               # Material 3 colors (indigo seed), type, themes
|   +-- splash/              # splash screen + Canvas logo
|   +-- home/                # grid, search, drawer, menus, empty states
|   +-- editor/              # note editor with palette + auto-save
|   +-- settings/            # theme, clear data, about
|   +-- nav/                 # Compose navigation graph + transitions
|   +-- components/          # NoteCard, ColorSwatchRow, EmptyState
+-- viewmodel/               # NoteViewModel (Flow-based) + factory
```

Unidirectional data flow: Room `Flow` queries -> repository -> `NoteViewModel`
(`StateFlow`s with live search filtering) -> Compose UI; all mutations go back
through the ViewModel into Room. No network layer exists.
