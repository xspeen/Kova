import Foundation

/// Codable mirror of a note used for JSON backup export/import.
/// Field names intentionally match the Android build's export format so
/// backups move freely between both platforms.
struct NoteDTO: Codable {
    var title: String = ""
    var body: String = ""
    var colorIndex: Int = 0
    var isPinned: Bool = false
    var isArchived: Bool = false
    var isTrashed: Bool = false
    var createdAt: Date = Date()
    var updatedAt: Date = Date()
}

/// Builds and parses the Kova JSON backup format:
/// {"version":1,"notes":[{...}]}. Dates are encoded as epoch milliseconds.
enum NoteExport {

    private static let encoder: JSONEncoder = {
        let e = JSONEncoder()
        e.dateEncodingStrategy = .millisecondsSince1970
        e.outputFormatting = [.prettyPrinted, .sortedKeys]
        return e
    }()

    private static let decoder: JSONDecoder = {
        let d = JSONDecoder()
        d.dateDecodingStrategy = .millisecondsSince1970
        return d
    }()

    // MARK: Encoding

    static func dto(from note: Note) -> NoteDTO {
        NoteDTO(
            title: note.title,
            body: note.body,
            colorIndex: note.colorIndex,
            isPinned: note.isPinned,
            isArchived: note.isArchived,
            isTrashed: note.isTrashed,
            createdAt: note.createdAt,
            updatedAt: note.updatedAt
        )
    }

    /// JSON for a full backup of the given notes. Returns "" when empty.
    static func json(for notes: [Note]) -> String {
        guard !notes.isEmpty else { return "" }
        let payload = BackupPayload(
            version: 1,
            notes: notes.map { dto(from: $0) }
        )
        guard let data = try? encoder.encode(payload) else { return "" }
        return String(data: data, encoding: .utf8) ?? ""
    }

    /// JSON for a single note (per-note export from the card menu).
    static func singleNoteJSON(for note: Note) -> String {
        json(for: [note])
    }

    // MARK: Decoding

    /// Tolerant parse of a backup document: ignores blank entries and
    /// returns an empty array on any structural failure.
    static func notes(from data: Data) -> [NoteDTO] {
        guard
            let payload = try? decoder.decode(BackupPayload.self, from: data)
        else { return [] }
        return payload.notes.filter {
            !$0.title.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty ||
            !$0.body.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
        }
    }

    static func notes(from string: String) -> [NoteDTO] {
        guard let data = string.data(using: .utf8) else { return [] }
        return notes(from: data)
    }

    private struct BackupPayload: Codable {
        var version: Int
        var notes: [NoteDTO]
    }
}
