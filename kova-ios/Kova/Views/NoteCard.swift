import SwiftUI
import SwiftData

/// Colored note card for the masonry grid: title, body preview, date
/// footer and pin indicator, with a full context menu (pin, archive,
/// change color, delete).
struct NoteCard: View {

    let note: Note
    var onEdit: () -> Void = {}

    @Environment(\.modelContext) private var modelContext

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            if !note.title.isEmpty {
                Text(note.title)
                    .font(.headline)
                    .foregroundStyle(.primary)
                    .lineLimit(2)
            }

            if !note.body.isEmpty {
                Text(note.body)
                    .font(.subheadline)
                    .foregroundStyle(.primary.opacity(0.72))
                    .lineLimit(8)
            }

            HStack(spacing: 6) {
                Text(note.updatedAt, style: .date)
                    .font(.caption2)
                    .foregroundStyle(.primary.opacity(0.5))

                Spacer(minLength: 0)

                if note.isPinned {
                    Image(systemName: "pin.fill")
                        .font(.caption2)
                        .foregroundStyle(.primary.opacity(0.6))
                        .accessibilityLabel("Pinned")
                }
            }
        }
        .padding(14)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(
            KovaTheme.color(for: note.colorIndex),
            in: RoundedRectangle(cornerRadius: 18, style: .continuous)
        )
        .overlay {
            RoundedRectangle(cornerRadius: 18, style: .continuous)
                .strokeBorder(.primary.opacity(0.06), lineWidth: 1)
        }
        .shadow(color: .black.opacity(0.05), radius: 5, y: 2)
        .contentShape(RoundedRectangle(cornerRadius: 18, style: .continuous))
        .onTapGesture(perform: onEdit)
        .contextMenu { contextMenu }
        .transition(.scale(scale: 0.9).combined(with: .opacity))
        .accessibilityElement(children: .combine)
    }

    // MARK: Context menu

    @ViewBuilder
    private var contextMenu: some View {
        Section {
            Button {
                withAnimation(KovaTheme.snappySpring) { note.isPinned.toggle() }
            } label: {
                Label(
                    note.isPinned ? "Unpin" : "Pin",
                    systemImage: note.isPinned ? "pin.slash" : "pin"
                )
            }

            Button {
                withAnimation(KovaTheme.snappySpring) { archiveNote() }
            } label: {
                Label("Archive", systemImage: "archivebox")
            }

            Menu {
                ForEach(KovaTheme.palette) { swatch in
                    Button {
                        withAnimation(KovaTheme.snappySpring) {
                            note.colorIndex = swatch.index
                        }
                    } label: {
                        Label {
                            Text(swatch.name)
                        } icon: {
                            Circle()
                                .fill(swatch.color)
                                .frame(width: 14, height: 14)
                                .overlay {
                                    if note.colorIndex == swatch.index {
                                        Image(systemName: "checkmark")
                                            .font(.system(size: 8, weight: .bold))
                                            .foregroundStyle(KovaTheme.checkColor(for: swatch.index))
                                    }
                                }
                        }
                    }
                }
            } label: {
                Label("Note color", systemImage: "paintpalette")
            }
        }

        Section {
            Button(role: .destructive) {
                withAnimation(KovaTheme.snappySpring) { trashNote() }
            } label: {
                Label("Delete", systemImage: "trash")
            }
        }
    }

    // MARK: Actions

    private func archiveNote() {
        note.isArchived = true
        note.isPinned = false
        note.updatedAt = Date()
        try? modelContext.save()
    }

    private func trashNote() {
        note.isTrashed = true
        note.isPinned = false
        note.updatedAt = Date()
        try? modelContext.save()
    }
}
