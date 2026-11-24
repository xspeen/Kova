import SwiftUI
import SwiftData

/// Full-screen note editor: title + body, horizontal color palette,
/// pin toggle and toolbar actions (close, archive, delete). Changes are
/// written to SwiftData live and saved on dismiss; an empty note is
/// discarded automatically.
struct NoteEditorView: View {

    @Environment(\.modelContext) private var modelContext

    @Bindable var note: Note
    let onClose: () -> Void

    @FocusState private var titleFocused: Bool
    @FocusState private var bodyFocused: Bool

    var body: some View {
        ZStack {
            KovaTheme.color(for: note.colorIndex)
                .ignoresSafeArea()

            VStack(spacing: 0) {
                headerBar
                paletteBar

                ScrollView(showsIndicators: false) {
                    VStack(alignment: .leading, spacing: 10) {
                        TextField("Title", text: $note.title)
                            .font(.system(size: 30, weight: .bold))
                            .focused($titleFocused)
                            .submitLabel(.next)

                        bodyEditor
                    }
                    .padding(.horizontal, 20)
                    .padding(.top, 8)
                    .padding(.bottom, 32)
                }

                Spacer(minLength: 0)
            }
        }
        .onAppear {
            if note.isEmpty {
                titleFocused = true
            }
        }
    }

    // MARK: Header

    private var headerBar: some View {
        HStack(spacing: 10) {
            headerButton("chevron.down", tint: .primary) {
                closeEditor()
            }
            .accessibilityLabel("Close and save")

            Spacer(minLength: 0)

            headerButton(
                note.isPinned ? "pin.fill" : "pin",
                tint: note.isPinned ? KovaTheme.accent : .primary
            ) {
                withAnimation(KovaTheme.snappySpring) {
                    note.isPinned.toggle()
                    note.updatedAt = Date()
                }
            }
            .accessibilityLabel(note.isPinned ? "Unpin" : "Pin")

            headerButton("archivebox", tint: .primary) { archiveNote() }
                .accessibilityLabel("Archive")

            headerButton("trash", tint: .red) { trashNote() }
                .accessibilityLabel("Delete")
        }
        .padding(.horizontal, 14)
        .padding(.vertical, 8)
    }

    private func headerButton(
        _ systemName: String,
        tint: Color,
        action: @escaping () -> Void
    ) -> some View {
        Button {
            action()
        } label: {
            Image(systemName: systemName)
                .font(.system(size: 16, weight: .semibold))
                .foregroundStyle(tint)
                .frame(width: 40, height: 40)
                .background(.primary.opacity(0.06), in: Circle())
        }
        .buttonStyle(ScaleButtonStyle())
    }

    // MARK: Body editor

    private var bodyEditor: some View {
        ZStack(alignment: .topLeading) {
            TextEditor(text: $note.body)
                .font(.body)
                .focused($bodyFocused)
                .scrollContentBackground(.hidden)
                .frame(minHeight: 240)
                .padding(.horizontal, -4)

            if note.body.isEmpty {
                Text("Start writing. It stays on this device.")
                    .font(.body)
                    .foregroundStyle(.primary.opacity(0.35))
                    .padding(.top, 10)
                    .padding(.leading, 6)
                    .allowsHitTesting(false)
            }
        }
        .onSubmit { bodyFocused = true }
    }

    // MARK: Color palette

    private var paletteBar: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 14) {
                ForEach(KovaTheme.palette) { swatch in
                    swatchButton(swatch)
                }
            }
            .padding(.horizontal, 20)
            .padding(.vertical, 8)
        }
    }

    private func swatchButton(_ swatch: KovaTheme.Swatch) -> some View {
        let isSelected = note.colorIndex == swatch.index

        return Button {
            withAnimation(KovaTheme.snappySpring) {
                note.colorIndex = swatch.index
                note.updatedAt = Date()
            }
        } label: {
            Circle()
                .fill(swatch.color)
                .frame(width: 30, height: 30)
                .overlay {
                    Circle().strokeBorder(.primary.opacity(0.15), lineWidth: 1)
                }
                .overlay {
                    if isSelected {
                        Image(systemName: "checkmark")
                            .font(.system(size: 11, weight: .bold))
                            .foregroundStyle(KovaTheme.checkColor(for: swatch.index))
                    }
                }
                .overlay {
                    Circle()
                        .strokeBorder(KovaTheme.accent, lineWidth: 2)
                        .frame(width: 36, height: 36)
                        .opacity(isSelected ? 1 : 0)
                }
        }
        .buttonStyle(ScaleButtonStyle())
        .accessibilityLabel("Color: \(swatch.name)")
    }

    // MARK: Actions

    /// Auto-save on dismiss: empty notes are discarded, everything else
    /// is persisted with an updated timestamp.
    private func closeEditor() {
        if note.isEmpty {
            modelContext.delete(note)
        } else {
            note.updatedAt = Date()
        }
        try? modelContext.save()
        onClose()
    }

    private func archiveNote() {
        note.isArchived = true
        note.isPinned = false
        note.updatedAt = Date()
        try? modelContext.save()
        onClose()
    }

    private func trashNote() {
        note.isTrashed = true
        note.isPinned = false
        note.updatedAt = Date()
        try? modelContext.save()
        onClose()
    }
}
