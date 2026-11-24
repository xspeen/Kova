package com.kova.app.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.RestoreFromTrash
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kova.app.data.NoteEntity
import com.kova.app.ui.theme.KovaIndigo
import com.kova.app.ui.theme.NotePalette
import com.kova.app.ui.theme.OnNoteColor
import com.kova.app.ui.theme.noteContainerColor

/**
 * A colored, Keep-style note card. Long-press or the overflow button opens a
 * bottom sheet with pin/unpin, archive/unarchive, export, color change and
 * delete actions. In trash the sheet offers restore and delete-forever.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCard(
    note: NoteEntity,
    inTrash: Boolean = false,
    inArchive: Boolean = false,
    onOpen: () -> Unit,
    onTogglePin: () -> Unit,
    onToggleArchive: () -> Unit,
    onDelete: () -> Unit,
    onDeleteForever: () -> Unit,
    onRestore: () -> Unit,
    onColorChange: (Int) -> Unit,
    onExport: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    var sheetOpen by remember { mutableStateOf(false) }
    var confirmDeleteForever by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val isPastel = note.colorIndex in 1..NotePalette.size
    val container = noteContainerColor(note.colorIndex, MaterialTheme.colorScheme.surface)
    val content = if (isPastel) OnNoteColor else MaterialTheme.colorScheme.onSurface
    val subtle = content.copy(alpha = 0.72f)

    Card(
        modifier = modifier.pressScale(pressed),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = container)
    ) {
        Column(
            modifier = Modifier
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = LocalIndication.current,
                    onClick = onOpen,
                    onLongClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        sheetOpen = true
                    }
                )
                .padding(14.dp)
        ) {
            if (note.label.isNotBlank()) {
                Text(
                    text = note.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = subtle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (note.title.isNotBlank()) {
                    Text(
                        text = note.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = content,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
                if (note.isPinned) {
                    Icon(
                        imageVector = Icons.Rounded.PushPin,
                        contentDescription = "Pinned",
                        tint = subtle,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                IconButton(onClick = { sheetOpen = true }) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = "More options",
                        tint = subtle
                    )
                }
            }
            if (note.body.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = note.body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (note.title.isBlank()) content else subtle,
                    maxLines = 8,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

    if (sheetOpen) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { sheetOpen = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (inTrash) {
                    SheetActionRow(
                        label = "Restore",
                        icon = Icons.Outlined.RestoreFromTrash,
                        onClick = {
                            sheetOpen = false
                            onRestore()
                        }
                    )
                    SheetActionRow(
                        label = "Export",
                        icon = Icons.Outlined.FileDownload,
                        onClick = {
                            sheetOpen = false
                            onExport()
                        }
                    )
                    SheetActionRow(
                        label = "Delete permanently",
                        icon = Icons.Outlined.Delete,
                        isError = true,
                        onClick = {
                            sheetOpen = false
                            confirmDeleteForever = true
                        }
                    )
                } else {
                    SheetActionRow(
                        label = if (note.isPinned) "Unpin" else "Pin",
                        icon = Icons.Rounded.PushPin,
                        onClick = {
                            sheetOpen = false
                            onTogglePin()
                        }
                    )
                    SheetActionRow(
                        label = if (inArchive || note.isArchived) "Unarchive" else "Archive",
                        icon = Icons.Outlined.Inventory2,
                        onClick = {
                            sheetOpen = false
                            onToggleArchive()
                        }
                    )
                    SheetActionRow(
                        label = "Export",
                        icon = Icons.Outlined.FileDownload,
                        onClick = {
                            sheetOpen = false
                            onExport()
                        }
                    )
                    SheetActionRow(
                        label = "Delete",
                        icon = Icons.Outlined.Delete,
                        isError = true,
                        onClick = {
                            sheetOpen = false
                            onDelete()
                        }
                    )
                }
                if (!inTrash) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Color",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    ColorSwatchRow(
                        selected = note.colorIndex,
                        onSelected = onColorChange,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }

    if (confirmDeleteForever) {
        AlertDialog(
            onDismissRequest = { confirmDeleteForever = false },
            title = { Text("Delete forever?") },
            text = { Text("This note will be gone for good.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmDeleteForever = false
                    onDeleteForever()
                }) {
                    Text(
                        text = "Delete forever",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDeleteForever = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}

@Composable
private fun SheetActionRow(
    label: String,
    icon: ImageVector,
    isError: Boolean = false,
    onClick: () -> Unit
) {
    val accent = if (isError) MaterialTheme.colorScheme.error else KovaIndigo
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(accent.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )
    }
}

/** Row of the default surface swatch plus the eight palette colors. */
@Composable
fun ColorSwatchRow(
    selected: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Swatch(
            index = 0,
            color = MaterialTheme.colorScheme.surfaceVariant,
            selected = selected == 0,
            onSelected = onSelected
        )
        NotePalette.forEachIndexed { position, color ->
            Swatch(
                index = position + 1,
                color = color,
                selected = selected == position + 1,
                onSelected = onSelected
            )
        }
    }
}

@Composable
private fun Swatch(
    index: Int,
    color: Color,
    selected: Boolean,
    onSelected: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) KovaIndigo else MaterialTheme.colorScheme.outline,
                shape = CircleShape
            )
            .clickable { onSelected(index) }
            .padding(12.dp)
    )
}
