# Kova

Kova is a premium, local-first notes app for iOS in the spirit of Google Keep, rebranded and rebuilt with a private-by-design philosophy: indigo/violet accent (#6C5CE7), dark/light adaptive design, and smooth animations throughout. All notes live entirely on your device.

- App name: **Kova**
- Bundle ID: **com.kova.app**
- Minimum OS: **iOS 17.0**
- Stack: **Swift 5.9+, SwiftUI, SwiftData (@Model), Xcode 15**

## Features

- **Splash screen on every launch** — geometric "K" logo drawn with SwiftUI shapes, tagline ("Your thoughts. Yours alone."), three feature rows (Offline first, No ads, No tracking), and a Continue button that animates into the app.
- **Notes home** — live-filtering search field, masonry-style two-column grid of colored note cards (title, body preview, date, pin indicator), pinned notes sorted first.
- **Custom sidebar drawer** — slides in from the leading edge with dimmed backdrop: Notes, Archive, Trash, Settings.
- **Note editor** — full-screen scale-in sheet: title, body, horizontal palette of 8 colors + default, pin toggle, archive and delete toolbar actions. Auto-saves on dismiss; empty notes are discarded automatically.
- **Card context menu** — pin/unpin, archive, change color, delete.
- **Trash** — restore, delete forever, and empty trash with confirmation.
- **Settings** — appearance picker (System / Light / Dark via `preferredColorScheme` + AppStorage), clear all data with confirmation, About rows (Version 1.0.0, "Kova — free, private, offline").
- **Animations everywhere** — splash fade-to-app, drawer slide, card appearance, FAB and editor scale transitions.

## Opening the project

### Option A: XcodeGen (recommended)

1. Install XcodeGen if you haven't: `brew install xcodegen`
2. From the `kova-ios` directory run:

   ```bash
   xcodegen generate
   ```

3. Open the generated workspace/project:

   ```bash
   open Kova.xcodeproj
   ```

4. Select the **Kova** scheme and an iOS 17 simulator, then Run.

### Option B: Manual (no XcodeGen)

1. In Xcode: **File > New > Project > iOS > App**.
2. Product name: `Kova`, Interface: **SwiftUI**, Language: **Swift**, minimum deployment **iOS 17.0**. Delete the generated `ContentView.swift` and the sample app file.
3. Drag the `Kova` folder from this repository (containing `KovaApp.swift`, `Models/`, `Views/`, `Theme/`) into the project navigator and add it to the **Kova** target.
4. In **Target > General**, set the Bundle Identifier to `com.kova.app` and Display Name to `Kova`. In **Build Settings**, set `GENERATE_INFOPLIST_FILE = YES` and ensure the Launch Screen is empty (`UILaunchScreen` dictionary with no entries), matching `project.yml`.
5. Build and run on an iOS 17 simulator.

## Project structure

```
kova-ios/
  project.yml                  # XcodeGen spec (target Kova, iOS 17.0)
  Kova/
    KovaApp.swift              # App entry: ModelContainer, splash state, color scheme
    Models/
      Note.swift               # @Model Note (SwiftData)
    Views/
      SplashView.swift         # Launch splash with Continue
      MainView.swift           # Sidebar + search + masonry grid + FAB + editor overlay
      NoteCard.swift           # Colored card with context menu
      NoteEditorView.swift     # Title/body editor, palette, pin, archive, delete
      SidebarView.swift        # Custom drawer (Notes / Archive / Trash / Settings)
      TrashView.swift          # Restore / delete forever / empty trash
      SettingsView.swift       # Appearance, clear data, about
      EmptyStateView.swift     # Reusable empty state
    Theme/
      KovaTheme.swift          # Brand colors, palette, K logo shape, animations
```

## Privacy statement

Kova is **100% local-first**. The app makes no network requests, contains no analytics, no ads, and no tracking of any kind. Your notes are stored on your device only, persisted locally by SwiftData. Clearing all data in Settings permanently removes everything, and there is nothing on any server to begin with.
