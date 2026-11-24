# Kova

**Kova** is a free, private, offline-first notes app — a Google Keep-style
experience rebranded and rebuilt from scratch. One product, two fully native
apps:

| Platform | Location | Stack |
| --- | --- | --- |
| Android | [`kova-android/`](kova-android/) | Kotlin 1.9, Jetpack Compose, Material 3, Room |
| iOS | [`kova-ios/`](kova-ios/) | Swift 5.9, SwiftUI, SwiftData (iOS 17+) |

## Privacy

Kova is **100% local-first**. The Android app declares **no INTERNET
permission** — it physically cannot send or receive network data. The iOS
app makes no network requests. No analytics, no ads, no tracking, no
accounts, no cloud. Notes live only on your device.

## Features (both platforms)

- Splash screen on every launch: geometric "K" logo, tagline
  *"Your thoughts. Yours alone."*, privacy feature rows, Continue button
- Notes grid (staggered / masonry) with colored cards and live search
- Sliding navigation drawer: Notes, Archive, Trash, Settings
- Full-screen editor: title, body, 8-color palette + default, pin toggle
- Pin, archive, trash, restore, delete forever, empty trash
- System/Light/Dark theme, clear-all-data with confirmation
- Smooth animations everywhere; icons only (Material icons / SF Symbols),
  zero emoji

## Building

**Android:** open `kova-android/` in Android Studio (JDK 17), or
`gradle -p kova-android assembleDebug`.

**iOS:** in `kova-ios/`, run `xcodegen generate` and open `Kova.xcodeproj`
(Xcode 15+, iOS 17 simulator), or follow the manual setup in
[`kova-ios/README.md`](kova-ios/README.md).

## CI / Releases

GitHub Actions build recipes are provided in `ci-workflows/` (add them to
`.github/workflows/` to enable automatic builds):

- **Android**: debug + release APKs as workflow artifacts. Pushing a tag
  like `v1.0.0` publishes a GitHub Release with the APKs attached.
- **iOS**: builds on a macOS runner and uploads the simulator `.app` bundle.
  Installable device builds require signing with your Apple Developer
  identity in Xcode.

## License

Released for personal and commercial use, free of charge, forever.
