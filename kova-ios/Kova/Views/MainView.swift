import SwiftUI
import SwiftData

/// Root screen: top bar with drawer toggle, live search field, masonry
/// note grid, custom sliding sidebar, FAB and a scale-transition editor
/// overlay.
struct MainView: View {

    @Environment(\.modelContext) private var modelContext

    @Query(sort: [SortDescriptor(\Note.updatedAt, order: .reverse)])
    private var allNotes: [Note]

    @State private var searchText = ""
    @State private var destination: SidebarDestination = .notes
    @State private var showSidebar = false
    @State private var editorNote: Note?

    var body: some View {
        ZStack {
            Color(.systemBackground).ignoresSafeArea()

            VStack(spacing: 0) {
                topBar
                searchField
                contentView
            }

            if showSidebar {
                sidebarLayer
            }
        }
        .overlay(alignment: .bottomTrailing) {
            if showsFAB {
                fab
                    .padding(24)
                    .transition(.scale(scale: 0.6).combined(with: .opacity))
            }
        }
        .overlay {
            if let note = editorNote {
                editorOverlay(for: note)
            }
        }
        .onChange(of: destination) { _, _ in
            searchText = ""
        }
    }

    // MARK: Top bar

    private var topBar: some View {
        HStack(spacing: 14) {
            Button {
                toggleSidebar(!showSidebar)
            } label: {
                Image(systemName: "line.3.horizontal")
                    .font(.title3.weight(.semibold))
                    .foregroundStyle(.primary)
                    .frame(width: 40, height: 40)
            }
            .buttonStyle(ScaleButtonStyle())
            .accessibilityLabel("Open menu")

            Text(destination.title)
                .font(.title2.weight(.bold))

            Spacer(minLength: 0)
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 8)
    }

    private var searchField: some View {
        HStack(spacing: 8) {
            Image(systemName: "magnifyingglass")
                .foregroundStyle(.secondary)

            TextField("Search notes", text: $searchText)
                .autocorrectionDisabled()

            if !searchText.isEmpty {
                Button {
                    withAnimation(.easeInOut(duration: 0.15)) { searchText = "" }
                } label: {
                    Image(systemName: "xmark.circle.fill")
                        .foregroundStyle(.secondary)
                }
                .transition(.scale.combined(with: .opacity))
            }
        }
        .padding(.horizontal, 14)
        .padding(.vertical, 11)
        .background(
            Color(.secondarySystemBackground),
            in: RoundedRectangle(cornerRadius: 14, style: .continuous)
        )
        .padding(.horizontal, 16)
        .padding(.bottom, 8)
    }

    // MARK: Content

    @ViewBuilder
    private var contentView: some View {
        switch destination {
        case .notes:
            noteList(
                notes: sortedNotes,
                emptyIcon: "square.and.pencil",
                emptyTitle: searchText.isEmpty ? "No notes yet" : "No matches",
                emptyMessage: searchText.isEmpty
                    ? "Tap the plus button to capture your first thought."
                    : "Try a different search term."
            )
        case .archive:
            noteList(
                notes: archiveNotes,
                emptyIcon: "archivebox",
                emptyTitle: "Nothing archived",
                emptyMessage: "Archive a note to tuck it away without deleting it."
            )
        case .trash:
            TrashView()
        case .settings:
            SettingsView()
        }
    }

    private func noteList(notes: [Note], emptyIcon: String, emptyTitle: String, emptyMessage: String) -> some View {
        ScrollView {
            if notes.isEmpty {
                EmptyStateView(icon: emptyIcon, title: emptyTitle, message: emptyMessage)
                    .padding(.top, 90)
                    .padding(.horizontal, 24)
            } else {
                MasonryNoteGrid(notes: notes) { note in
                    openEditor(note)
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 8)
            }
        }
    }

    // MARK: Sidebar

    private var sidebarLayer: some View {
        ZStack(alignment: .leading) {
            Color.black.opacity(0.35)
                .ignoresSafeArea()
                .transition(.opacity)
                .onTapGesture { toggleSidebar(false) }

            SidebarView(destination: $destination) {
                toggleSidebar(false)
            }
            .frame(width: 286)
            .transition(.move(edge: .leading))
        }
        .zIndex(1)
    }

    private func toggleSidebar(_ open: Bool) {
        withAnimation(.spring(response: 0.32, dampingFraction: 0.85)) {
            showSidebar = open
        }
    }

    // MARK: FAB

    private var showsFAB: Bool {
        destination == .notes || destination == .archive
    }

    private var fab: some View {
        Button {
            openEditor(nil)
        } label: {
            Image(systemName: "plus")
                .font(.system(size: 24, weight: .semibold))
                .foregroundStyle(.white)
                .frame(width: 58, height: 58)
                .background(Circle().fill(KovaTheme.accent))
                .shadow(color: KovaTheme.accent.opacity(0.45), radius: 12, y: 6)
        }
        .buttonStyle(ScaleButtonStyle())
        .accessibilityLabel("New note")
    }

    // MARK: Editor overlay

    private func editorOverlay(for note: Note) -> some View {
        NoteEditorView(note: note) {
            withAnimation(.spring(response: 0.32, dampingFraction: 0.86)) {
                editorNote = nil
            }
        }
        .transition(.scale(scale: 0.94).combined(with: .opacity))
        .zIndex(2)
    }

    // MARK: Derived data

    private var scopeNotes: [Note] {
        switch destination {
        case .notes:
            return allNotes.filter { !$0.isTrashed && !$0.isArchived }
        case .archive:
            return allNotes.filter { $0.isArchived && !$0.isTrashed }
        default:
            return []
        }
    }

    private var searchFiltered: [Note] {
        let query = searchText.trimmingCharacters(in: .whitespacesAndNewlines).lowercased()
        guard !query.isEmpty else { return scopeNotes }
        return scopeNotes.filter {
            $0.title.lowercased().contains(query) || $0.body.lowercased().contains(query)
        }
    }

    private var sortedNotes: [Note] {
        searchFiltered.sorted { lhs, rhs in
            if lhs.isPinned != rhs.isPinned { return lhs.isPinned }
            return lhs.updatedAt > rhs.updatedAt
        }
    }

    private var archiveNotes: [Note] {
        searchFiltered.sorted { $0.updatedAt > $1.updatedAt }
    }

    // MARK: Actions

    /// Opens the editor for an existing note, or creates a brand-new note
    /// (kept empty notes are removed by the editor when it closes).
    private func openEditor(_ existing: Note?) {
        let target: Note
        if let existing {
            target = existing
        } else {
            let fresh = Note()
            modelContext.insert(fresh)
            target = fresh
        }
        withAnimation(.spring(response: 0.35, dampingFraction: 0.86)) {
            editorNote = target
        }
    }
}

// MARK: - Masonry grid

/// Masonry-style two-column layout: notes are distributed into the
/// shortest column using an estimated card height.
private struct MasonryNoteGrid: View {

    let notes: [Note]
    let onEdit: (Note) -> Void

    var body: some View {
        let (left, right) = Self.split(notes)

        HStack(alignment: .top, spacing: 12) {
            LazyVStack(spacing: 12) {
                ForEach(left) { note in
                    NoteCard(note: note) { onEdit(note) }
                }
            }
            LazyVStack(spacing: 12) {
                ForEach(right) { note in
                    NoteCard(note: note) { onEdit(note) }
                }
            }
        }
    }

    static func split(_ notes: [Note]) -> ([Note], [Note]) {
        var left: [Note] = []
        var right: [Note] = []
        var leftHeight: CGFloat = 0
        var rightHeight: CGFloat = 0

        for note in notes {
            let height = estimatedHeight(for: note)
            if leftHeight <= rightHeight {
                left.append(note)
                leftHeight += height
            } else {
                right.append(note)
                rightHeight += height
            }
        }
        return (left, right)
    }

    static func estimatedHeight(for note: Note) -> CGFloat {
        var height: CGFloat = 28 // padding
        if !note.title.isEmpty { height += 26 }
        if !note.body.isEmpty {
            let lines = CGFloat(min(8, max(1, Int(CGFloat(note.body.count) / 34.0))))
            height += lines * 19
        }
        height += 24 // footer row
        return height
    }
}
