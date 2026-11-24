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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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

    private fun now(): Long = System.currentTimeMillis()

    companion object {
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
