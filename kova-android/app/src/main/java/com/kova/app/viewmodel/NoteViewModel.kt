package com.kova.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kova.app.data.KovaDatabase
import com.kova.app.data.NoteEntity
import com.kova.app.data.NoteRepository
import com.kova.app.data.SettingsStore
import com.kova.app.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

/**
 * Central ViewModel for Kova. Exposes live Room queries as StateFlows,
 * applies the live search filter, and forwards all mutations to the
 * repository. No network, ever.
 */
class NoteViewModel(
    private val repository: NoteRepository,
    private val settingsStore: SettingsStore
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val themeMode: StateFlow<ThemeMode> = settingsStore.themeMode

    /**
     * True once the very first Room emission of the active-notes query has
     * arrived. UI shows the skeleton loading grid until then. Collected
     * eagerly so the flip happens even before any screen subscribes.
     */
    val isReady: StateFlow<Boolean> = repository.activeNotes
        .map { true }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val activeNotes: StateFlow<List<NoteEntity>> =
        combine(repository.activeNotes, _query) { notes, q ->
            if (q.isBlank()) {
                notes
            } else {
                notes.filter { note ->
                    note.title.contains(q, ignoreCase = true) ||
                        note.body.contains(q, ignoreCase = true) ||
                        note.label.contains(q, ignoreCase = true)
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val archivedNotes: StateFlow<List<NoteEntity>> =
        repository.archivedNotes
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val trashedNotes: StateFlow<List<NoteEntity>> =
        repository.trashedNotes
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setQuery(value: String) {
        _query.value = value
    }

    fun setThemeMode(mode: ThemeMode) {
        settingsStore.setThemeMode(mode)
    }

    fun observeNote(id: Long): Flow<NoteEntity?> = repository.observeNote(id)

    fun saveNote(
        existing: NoteEntity?,
        title: String,
        body: String,
        colorIndex: Int,
        isPinned: Boolean
    ) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val note = existing?.copy(
                title = title,
                body = body,
                colorIndex = colorIndex,
                isPinned = isPinned,
                updatedAt = now
            ) ?: NoteEntity(
                title = title,
                body = body,
                colorIndex = colorIndex,
                isPinned = isPinned,
                createdAt = now,
                updatedAt = now
            )
            repository.upsert(note)
        }
    }

    fun togglePin(note: NoteEntity) {
        viewModelScope.launch {
            repository.upsert(note.copy(isPinned = !note.isPinned, updatedAt = now()))
        }
    }

    fun toggleArchive(note: NoteEntity) {
        viewModelScope.launch {
            repository.upsert(
                note.copy(
                    isArchived = !note.isArchived,
                    isPinned = false,
                    updatedAt = now()
                )
            )
        }
    }

    fun moveToTrash(note: NoteEntity) {
        viewModelScope.launch {
            repository.upsert(
                note.copy(isTrashed = true, isPinned = false, updatedAt = now())
            )
        }
    }

    fun restoreFromTrash(note: NoteEntity) {
        viewModelScope.launch {
            repository.upsert(note.copy(isTrashed = false, updatedAt = now()))
        }
    }

    fun deleteForever(note: NoteEntity) {
        viewModelScope.launch { repository.delete(note) }
    }

    fun emptyTrash() {
        viewModelScope.launch { repository.deleteAllTrashed() }
    }

    fun clearAllData() {
        viewModelScope.launch { repository.deleteAll() }
    }

    /**
     * Builds a JSON document of every note on this device (active, archived
     * and trashed). Returns "" when there is nothing to export.
     */
    suspend fun buildExportJson(): String {
        val notes = repository.getAllForExport()
        if (notes.isEmpty()) return ""
        val root = JSONObject()
        root.put("version", 1)
        root.put("exportedAt", System.currentTimeMillis())
        val array = JSONArray()
        for (note in notes) {
            array.put(noteToJson(note))
        }
        root.put("notes", array)
        return root.toString(2)
    }

    /**
     * Imports notes from a Kova JSON backup. Tolerant of missing fields,
     * ignores blank entries, always inserts as new notes (Room assigns ids).
     * Returns the number of imported notes, or 0 on any parse failure.
     */
    suspend fun importNotes(json: String): Int = runCatching {
        val root = JSONObject(json)
        val array = root.optJSONArray("notes") ?: JSONArray()
        val now = System.currentTimeMillis()
        var imported = 0
        for (i in 0 until array.length()) {
            val obj = array.optJSONObject(i) ?: continue
            val title = obj.optString("title", "")
            val body = obj.optString("body", "")
            if (title.isBlank() && body.isBlank()) continue
            val note = NoteEntity(
                title = title,
                body = body,
                colorIndex = obj.optInt("colorIndex", 0).coerceIn(0, 8),
                isPinned = obj.optBoolean("isPinned", false),
                isArchived = obj.optBoolean("isArchived", false),
                isTrashed = obj.optBoolean("isTrashed", false),
                createdAt = obj.optLong("createdAt", now),
                updatedAt = obj.optLong("updatedAt", now),
                label = obj.optString("label", "")
            )
            repository.upsert(note)
            imported++
        }
        imported
    }.getOrDefault(0)

    private fun now(): Long = System.currentTimeMillis()

    companion object {
        fun noteToJson(note: NoteEntity): JSONObject = JSONObject().apply {
            put("id", note.id)
            put("title", note.title)
            put("body", note.body)
            put("colorIndex", note.colorIndex)
            put("isPinned", note.isPinned)
            put("isArchived", note.isArchived)
            put("isTrashed", note.isTrashed)
            put("createdAt", note.createdAt)
            put("updatedAt", note.updatedAt)
            put("label", note.label)
        }

        fun factory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val database = KovaDatabase.getInstance(context)
                    val viewModel = NoteViewModel(
                        repository = NoteRepository(database.noteDao()),
                        settingsStore = SettingsStore(context)
                    )
                    @Suppress("UNCHECKED_CAST")
                    return viewModel as T
                }
            }
    }
}
