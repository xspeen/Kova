package com.kova.app.ui.theme

import androidx.compose.ui.graphics.Color

/** Brand accent: bold indigo/violet. */
val KovaIndigo = Color(0xFF6C5CE7)
val KovaIndigoContainer = Color(0xFFE4DEFF)
val KovaIndigoDark = Color(0xFFC5B9FF)

/** Pastel note card palette (indices 1..8 in NoteEntity.colorIndex). */
val NotePalette = listOf(
    Color(0xFFFFF7B0), // Sunlight
    Color(0xFFD6E7FF), // Sky
    Color(0xFFE2F6E3), // Mint
    Color(0xFFFFE0DB), // Coral
    Color(0xFFEADCF8), // Lilac
    Color(0xFFFFE7F0), // Blush
    Color(0xFFFFE3C0), // Peach
    Color(0xFFE3E3E3)  // Stone
)

/** Text color used on top of pastel note colors (light and dark theme alike). */
val OnNoteColor = Color(0xFF1D1B20)

fun noteContainerColor(colorIndex: Int, defaultColor: Color): Color =
    if (colorIndex in 1..NotePalette.size) NotePalette[colorIndex - 1] else defaultColor
