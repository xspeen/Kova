import SwiftUI

/// Sidebar destinations shared between MainView and SidebarView.
enum SidebarDestination: String, CaseIterable, Identifiable {
    case notes
    case archive
    case trash
    case settings

    var id: String { rawValue }

    var title: String {
        switch self {
        case .notes: return "Notes"
        case .archive: return "Archive"
        case .trash: return "Trash"
        case .settings: return "Settings"
        }
    }

    var icon: String {
        switch self {
        case .notes: return "note.text"
        case .archive: return "archivebox"
        case .trash: return "trash"
        case .settings: return "gearshape"
        }
    }
}

/// Custom slide-in drawer with brand header and navigation items.
struct SidebarView: View {

    @Binding var destination: SidebarDestination
    let onClose: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            header
                .padding(.bottom, 18)

            ForEach(SidebarDestination.allCases) { item in
                row(item)
            }

            Spacer(minLength: 0)

            HStack(spacing: 8) {
                Image(systemName: "lock.shield")
                    .font(.caption)
                Text("Private by design")
                    .font(.caption)
            }
            .foregroundStyle(.secondary)
            .padding(.bottom, 12)
        }
        .padding(16)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
        .background {
            Color(.secondarySystemBackground).ignoresSafeArea()
        }
    }

    // MARK: Pieces

    private var header: some View {
        HStack(spacing: 12) {
            BrandMark(size: 40)

            VStack(alignment: .leading, spacing: 2) {
                Text("Kova")
                    .font(.headline)
                Text("Private notes")
                    .font(.caption)
                    .foregroundStyle(.secondary)
            }

            Spacer(minLength: 0)
        }
    }

    private func row(_ item: SidebarDestination) -> some View {
        let isSelected = destination == item

        return Button {
            withAnimation(.easeInOut(duration: 0.15)) {
                destination = item
            }
            onClose()
        } label: {
            HStack(spacing: 14) {
                Image(systemName: item.icon)
                    .font(.system(size: 16, weight: .semibold))
                    .frame(width: 26)

                Text(item.title)
                    .font(isSelected ? .subheadline.weight(.semibold) : .subheadline)

                Spacer(minLength: 0)
            }
            .foregroundStyle(isSelected ? KovaTheme.accent : Color.primary)
            .padding(.horizontal, 14)
            .padding(.vertical, 12)
            .background(
                isSelected ? KovaTheme.accent.opacity(0.14) : Color.clear,
                in: RoundedRectangle(cornerRadius: 12, style: .continuous)
            )
        }
        .buttonStyle(.plain)
        .accessibilityLabel(item.title)
    }
}
