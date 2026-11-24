import SwiftUI
import SwiftData

/// Settings: appearance picker (System/Light/Dark), storage info,
/// clear-all-data with confirmation, and About rows.
struct SettingsView: View {

    @Environment(\.modelContext) private var modelContext

    /// Shared AppStorage key with KovaApp's preferredColorScheme.
    @AppStorage("kova.appearance") private var appearance: Int = 0

    @Query private var allNotes: [Note]

    @State private var confirmClearData = false

    var body: some View {
        List {
            appearanceSection
            storageSection
            aboutSection
        }
        .listStyle(.insetGrouped)
        .confirmationDialog(
            "Clear all data?",
            isPresented: $confirmClearData,
            titleVisibility: .visible
        ) {
            Button("Delete all notes permanently", role: .destructive) {
                withAnimation(KovaTheme.snappySpring) { clearAllData() }
            }
            Button("Cancel", role: .cancel) {}
        } message: {
            Text("Every note on this device will be removed. This cannot be undone.")
        }
    }

    // MARK: Sections

    private var appearanceSection: some View {
        Section("Appearance") {
            Picker("Theme", selection: $appearance) {
                Text("System").tag(0)
                Text("Light").tag(1)
                Text("Dark").tag(2)
            }
            .pickerStyle(.segmented)

            LabeledContent("Applies instantly", value: appearanceLabel)
                .foregroundStyle(.secondary)
                .font(.footnote)
        }
    }

    private var storageSection: some View {
        Section("Storage") {
            LabeledContent("Notes on device", value: "\(allNotes.count)")

            Button(role: .destructive) {
                confirmClearData = true
            } label: {
                Label("Clear all data", systemImage: "trash.fill")
            }
        }
    }

    private var aboutSection: some View {
        Section("About") {
            LabeledContent("Version", value: "1.0.0")

            LabeledContent("Kova", value: "Free, private, offline")

            HStack(spacing: 10) {
                Image(systemName: "lock.shield")
                    .foregroundStyle(KovaTheme.accent)
                Text("All notes stay on this device. No network access, no analytics, no ads, no tracking.")
                    .font(.footnote)
                    .foregroundStyle(.secondary)
            }
        }
    }

    // MARK: Actions

    private var appearanceLabel: String {
        switch appearance {
        case 1: return "Light"
        case 2: return "Dark"
        default: return "System"
        }
    }

    private func clearAllData() {
        try? modelContext.delete(model: Note.self)
        try? modelContext.save()
    }
}
