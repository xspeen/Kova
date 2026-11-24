package com.kova.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query(
        "SELECT * FROM notes " +
            "WHERE isTrashed = 0 AND isArchived = 0 " +
            "ORDER BY isPinned DESC, updatedAt DESC"
    )
    fun observeActiveNotes(): Flow<List<NoteEntity>>

    @Query(
        "SELECT * FROM notes " +
            "WHERE isTrashed = 0 AND isArchived = 1 " +
            "ORDER BY updatedAt DESC"
    )
    fun observeArchivedNotes(): Flow<List<NoteEntity>>

    @Query(
        "SELECT * FROM notes " +
            "WHERE isTrashed = 1 " +
            "ORDER BY updatedAt DESC"
    )
    fun observeTrashedNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id")
    fun observeNoteById(id: Long): Flow<NoteEntity?>

    @Query("SELECT * FROM notes")
    suspend fun getAllForExport(): List<NoteEntity>

    @Upsert
    suspend fun upsert(note: NoteEntity)

    @Delete
    suspend fun delete(note: NoteEntity)

    @Query("DELETE FROM notes WHERE isTrashed = 1")
    suspend fun deleteAllTrashed()

    @Query("DELETE FROM notes")
    suspend fun deleteAll()
}
