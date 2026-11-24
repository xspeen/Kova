package com.kova.app.ui.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Delete
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import com.kova.app.data.NoteEntity
import com.kova.app.ui.components.EmptyState
import com.kova.app.ui.components.NoteCard
import com.kova.app.ui.splash.KovaLogo
import com.kova.app.viewmodel.NoteViewModel
import kotlinx.coroutines.launch

/** Which list the home screen is showing, driven by the navigation drawer. */
enum class HomeSection(val title: String) {
    NOTES("Notes"),
    ARCHIVE("Archive"),
    TRASH("Trash")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: NoteViewModel,
    onEditNote: (Long) -> Unit,
    onOpenSettings: () -> Unit
) {
    var section by rememberSaveable { mutableStateOf(HomeSection.NOTES) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val query by viewModel.query.collectAsState()
    val activeNotes by viewModel.activeNotes.collectAsState()
    val archivedNotes by viewModel.archivedNotes.collectAsState()
    val trashedNotes by viewModel.trashedNotes.collectAsState()

    var showEmptyTrashDialog by remember { mutableStateOf(false) }

    val notes: List<NoteEntity> = when (section) {
        HomeSection.NOTES -> activeNotes
        HomeSection.ARCHIVE -> archivedNotes
        HomeSection.TRASH -> trashedNotes
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            KovaDrawer(
                currentSection = section,
                onSectionSelected = { selected ->
                    section = selected
                    scope.launch { drawerState.close() }
                },
                onSettingsSelected = {
                    scope.launch { drawerState.close() }
                    onOpenSettings()
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Rounded.Menu,
                                contentDescription = "Open navigation drawer"
                            )
                        }
                    },
                    title = {
                        TextField(
                            value = query,
                            onValueChange = viewModel::setQuery,
                            placeholder = {
                                Text(
                                    text = "Search notes",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Search,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyLarge,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    actions = {
                        if (section == HomeSection.TRASH && trashedNotes.isNotEmpty()) {
                            IconButton(onClick = { showEmptyTrashDialog = true }) {
                                Icon(
                                    imageVector = Icons.Outlined.DeleteSweep,
                                    contentDescription = "Empty trash"
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { onEditNote(-1L) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Create note"
                    )
                }
            }
        ) { padding ->
            val searchActive = query.isNotBlank() && section == HomeSection.NOTES
            when {
                notes.isEmpty() && searchActive -> EmptyState(
                    icon = Icons.Outlined.SearchOff,
                    title = "No results",
                    message = "Nothing matches \"$query\".",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                )
                notes.isEmpty() -> EmptyState(
                    icon = when (section) {
                        HomeSection.NOTES -> Icons.AutoMirrored.Filled.Notes
                        HomeSection.ARCHIVE -> Icons.Outlined.Archive
                        HomeSection.TRASH -> Icons.AutoMirrored.Filled.Delete
                    },
                    title = when (section) {
                        HomeSection.NOTES -> "No notes yet"
                        HomeSection.ARCHIVE -> "Nothing archived"
                        HomeSection.TRASH -> "Trash is empty"
                    },
                    message = when (section) {
                        HomeSection.NOTES ->
                            "Tap the plus button to capture your first thought."
                        HomeSection.ARCHIVE ->
                            "Archived notes rest here, out of the way but never lost."
                        HomeSection.TRASH ->
                            "Deleted notes wait here until you remove them forever."
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                )
                else -> LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    contentPadding = PaddingValues(
                        start = 12.dp,
                        end = 12.dp,
                        top = 12.dp,
                        bottom = 96.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalItemSpacing = 12.dp,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    itemsIndexed(
                        items = notes,
                        key = { _, note -> note.id }
                    ) { index, note ->
                        StaggeredAppear(index = index) {
                            NoteCard(
                                note = note,
                                onOpen = { onEditNote(note.id) },
                                onTogglePin = { viewModel.togglePin(note) },
                                onToggleArchive = { viewModel.toggleArchive(note) },
                                onDelete = { viewModel.moveToTrash(note) },
                                onColorChange = { colorIndex ->
                                    viewModel.saveNote(
                                        existing = note,
                                        title = note.title,
                                        body = note.body,
                                        colorIndex = colorIndex,
                                        isPinned = note.isPinned
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showEmptyTrashDialog) {
        AlertDialog(
            onDismissRequest = { showEmptyTrashDialog = false },
            title = { Text("Empty trash?") },
            text = { Text("All notes in trash will be deleted forever. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showEmptyTrashDialog = false
                    viewModel.emptyTrash()
                }) {
                    Text(text = "Empty trash", color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEmptyTrashDialog = false }) {
                    Text(text = "Cancel", color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }
}

/**
 * Card entrance animation: each card fades in and slides up with a small
 * per-index delay, producing a staggered cascade on first appearance.
 */
@Composable
private fun StaggeredAppear(index: Int, content: @Composable () -> Unit) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay((index.coerceAtMost(10) * 40).toLong())
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 260)
        )
    }
    Box(
        modifier = Modifier.graphicsLayer {
            alpha = progress.value
            translationY = (1f - progress.value) * 40f
        }
    ) {
        content()
    }
}

@Composable
private fun KovaDrawer(
    currentSection: HomeSection,
    onSectionSelected: (HomeSection) -> Unit,
    onSettingsSelected: () -> Unit
) {
    ModalDrawerSheet {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                KovaLogo(logoSize = 40.dp)
                Spacer(modifier = Modifier.size(14.dp))
                Column {
                    Text(
                        text = "Kova",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Your thoughts. Yours alone.",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            HorizontalDivider()

            Spacer(modifier = Modifier.height(8.dp))
            DrawerItem(
                label = HomeSection.NOTES.title,
                icon = Icons.AutoMirrored.Filled.Notes,
                selected = currentSection == HomeSection.NOTES,
                onClick = { onSectionSelected(HomeSection.NOTES) }
            )
            DrawerItem(
                label = HomeSection.ARCHIVE.title,
                icon = Icons.Outlined.Archive,
                selected = currentSection == HomeSection.ARCHIVE,
                onClick = { onSectionSelected(HomeSection.ARCHIVE) }
            )
            DrawerItem(
                label = HomeSection.TRASH.title,
                icon = Icons.AutoMirrored.Filled.Delete,
                selected = currentSection == HomeSection.TRASH,
                onClick = { onSectionSelected(HomeSection.TRASH) }
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            DrawerItem(
                label = "Settings",
                icon = Icons.Outlined.Settings,
                selected = false,
                onClick = onSettingsSelected
            )

            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Kova v1.0.0",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
private fun DrawerItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = { Text(text = label) },
        icon = { Icon(imageVector = icon, contentDescription = null) },
        selected = selected,
        onClick = onClick,
        modifier = Modifier.padding(vertical = 2.dp)
    )
}
