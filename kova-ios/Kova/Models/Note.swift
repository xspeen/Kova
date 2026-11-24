import Foundation
import SwiftData

/// The single local-first data model for Kova.
/// Every note lives only on this device; SwiftData persists it via the
/// ModelContainer created in KovaApp.
@Model
final class Note {

    var id: UUID = UUID()
    var title: String = ""
    var body: String = ""
    var colorIndex: Int = 0
    var isPinned: Bool = false
    var isArchived: Bool = false
    var isTrashed: Bool = false
    var createdAt: Date = Date()
    var updatedAt: Date = Date()

    init(
        id: UUID = UUID(),
        title: String = "",
        body: String = "",
        colorIndex: Int = KovaTheme.defaultColorIndex,
        isPinned: Bool = false,
        isArchived: Bool = false,
        isTrashed: Bool = false,
        createdAt: Date = Date(),
        updatedAt: Date = Date()
    ) {
        self.id = id
        self.title = title
        self.body = body
        self.colorIndex = colorIndex
        self.isPinned = isPinned
        self.isArchived = isArchived
        self.isTrashed = isTrashed
        self.createdAt = createdAt
        self.updatedAt = updatedAt
    }

    /// True when the note carries no user content at all.
    var isEmpty: Bool {
        title.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty &&
        body.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
    }

    /// Short single-line preview used by cards and trash rows.
    var previewText: String {
        body
            .trimmingCharacters(in: .whitespacesAndNewlines)
            .replacingOccurrences(of: "\n", with: " ")
    }
}
