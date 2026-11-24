package com.kova.app.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
 * menu with pin/unpin, archive/unarchive, color change and delete.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCard(
    note: NoteEntity,
    onOpen: () -> Unit,
    onTogglePin: () -> Unit,
    onToggleArchive: () -> Unit,
    onDelete: () -> Unit,
    onColorChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val isPastel = note.colorIndex in 1..NotePalette.size
    val container = noteContainerColor(note.colorIndex, MaterialTheme.colorScheme.surface)
    val content = if (isPastel) OnNoteColor else MaterialTheme.colorScheme.onSurface
    val subtle = content.copy(alpha = 0.72f)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = container)
    ) {
        Column(
            modifier = Modifier
                .combinedClickable(
                    onClick = onOpen,
                    onLongClick = { menuExpanded = true }
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
                IconButton(onClick = { menuExpanded = true }) {
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

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(if (note.isPinned) "Unpin" else "Pin") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Rounded.PushPin, contentDescription = null)
                    },
                    onClick = {
                        menuExpanded = false
                        onTogglePin()
                    }
                )
                DropdownMenuItem(
                    text = { Text(if (note.isArchived) "Unarchive" else "Archive") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Outlined.Inventory2, contentDescription = null)
                    },
                    onClick = {
                        menuExpanded = false
                        onToggleArchive()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Delete") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Outlined.Delete, contentDescription = null)
                    },
                    onClick = {
                        menuExpanded = false
                        onDelete()
                    }
                )
                HorizontalDivider()
                Text(
                    text = "Change color",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                ColorSwatchRow(
                    selected = note.colorIndex,
                    onSelected = {
                        menuExpanded = false
                        onColorChange(it)
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }
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
