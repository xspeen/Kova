import SwiftUI
import SwiftData

/// Trash: restore individual notes, delete them forever, or empty the
/// whole trash with confirmation.
struct TrashView: View {

    @Environment(\.modelContext) private var modelContext

    @Query(
        filter: #Predicate<Note> { $0.isTrashed == true },
        sort: [SortDescriptor(\Note.updatedAt, order: .reverse)]
    )
    private var trashedNotes: [Note]

    @State private var confirmEmptyTrash = false

    var body: some View {
        VStack(spacing: 0) {
            if !trashedNotes.isEmpty {
                emptyTrashBar
            }

            if trashedNotes.isEmpty {
                ScrollView {
                    EmptyStateView(
                        icon: "trash",
                        title: "Trash is empty",
                        message: "Notes you delete will appear here for recovery."
                    )
                    .padding(.top, 80)
                    .padding(.horizontal, 24)
                }
            } else {
                notesList
            }
        }
        .confirmationDialog(
            "Empty the trash?",
            isPresented: $confirmEmptyTrash,
            titleVisibility: .visible
        ) {
            Button("Delete all notes forever", role: .destructive) {
                withAnimation(KovaTheme.snappySpring) { emptyTrash() }
            }
            Button("Cancel", role: .cancel) {}
        } message: {
            Text("This permanently removes every note in the trash. It cannot be undone.")
        }
    }

    // MARK: Pieces

    private var emptyTrashBar: some View {
        HStack {
            Spacer(minLength: 0)
            Button(role: .destructive) {
                confirmEmptyTrash = true
            } label: {
                Label("Empty Trash", systemImage: "trash")
                    .font(.subheadline.weight(.semibold))
            }
            .buttonStyle(ScaleButtonStyle())
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 6)
    }

    private var notesList: some View {
        List {
            ForEach(trashedNotes) { note in
                row(for: note)
            }
        }
        .listStyle(.insetGrouped)
    }

    private func row(for note: Note) -> some View {
        VStack(alignment: .leading, spacing: 4) {
            if !note.title.isEmpty {
                Text(note.title)
                    .font(.headline)
                    .lineLimit(1)
            }

            if !note.body.isEmpty {
                Text(note.previewText)
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
                    .lineLimit(2)
            }

            Text(note.updatedAt, style: .date)
                .font(.caption2)
                .foregroundStyle(.secondary)
        }
        .padding(.vertical, 4)
        .frame(maxWidth: .infinity, alignment: .leading)
        .swipeActions(edge: .trailing, allowsFullSwipe: true) {
            Button(role: .destructive) {
                withAnimation(KovaTheme.snappySpring) { deleteForever(note) }
            } label: {
                Label("Delete", systemImage: "trash")
            }

            Button {
                withAnimation(KovaTheme.snappySpring) { restore(note) }
            } label: {
                Label("Restore", systemImage: "arrow.uturn.backward.circle")
            }
            .tint(KovaTheme.accent)
        }
        .contextMenu {
            Button {
                withAnimation(KovaTheme.snappySpring) { restore(note) }
            } label: {
                Label("Restore", systemImage: "arrow.uturn.backward.circle")
            }

            Button(role: .destructive) {
                withAnimation(KovaTheme.snappySpring) { deleteForever(note) }
            } label: {
                Label("Delete forever", systemImage: "trash")
            }
        }
    }

    // MARK: Actions

    private func restore(_ note: Note) {
        note.isTrashed = false
        note.isArchived = false
        note.updatedAt = Date()
        try? modelContext.save()
    }

    private func deleteForever(_ note: Note) {
        modelContext.delete(note)
        try? modelContext.save()
    }

    private func emptyTrash() {
        try? modelContext.delete(
            model: Note.self,
            where: #Predicate<Note> { $0.isTrashed == true }
        )
        try? modelContext.save()
    }
}
