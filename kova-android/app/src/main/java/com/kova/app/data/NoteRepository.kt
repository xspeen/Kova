package com.kova.app.data

import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for notes. Read paths expose cold [Flow]s straight
 * from Room; write paths are simple suspend proxies.
 */
class NoteRepository(private val dao: NoteDao) {

    val activeNotes: Flow<List<NoteEntity>> = dao.observeActiveNotes()

    val archivedNotes: Flow<List<NoteEntity>> = dao.observeArchivedNotes()

    val trashedNotes: Flow<List<NoteEntity>> = dao.observeTrashedNotes()

    fun observeNote(id: Long): Flow<NoteEntity?> = dao.observeNoteById(id)

    suspend fun getAllForExport(): List<NoteEntity> = dao.getAllForExport()

    suspend fun upsert(note: NoteEntity) = dao.upsert(note)

    suspend fun delete(note: NoteEntity) = dao.delete(note)

    suspend fun deleteAllTrashed() = dao.deleteAllTrashed()

    suspend fun deleteAll() = dao.deleteAll()
}
