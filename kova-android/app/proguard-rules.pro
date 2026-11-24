# Kova ProGuard rules.
# The app is fully offline: no network layer, no analytics, no reflection-based
# serialization beyond what Room generates, so only minimal rules are needed.

# Keep Room generated implementations.
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Keep Kotlin metadata for coroutines debug checks.
-dontwarn kotlinx.coroutines.**
