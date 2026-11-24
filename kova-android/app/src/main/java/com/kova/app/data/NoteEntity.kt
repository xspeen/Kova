package com.kova.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A single note in Kova. All fields are stored locally in Room; nothing ever
 * leaves the device.
 */
@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String = "",
    val body: String = "",
    /** Index into the color palette: 0 = default surface, 1..8 = palette colors. */
    val colorIndex: Int = 0,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val isTrashed: Boolean = false,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val label: String = ""
)
