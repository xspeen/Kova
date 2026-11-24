import SwiftUI

/// Reusable empty state used by Notes, Archive, Trash and search results.
struct EmptyStateView: View {

    let icon: String
    let title: String
    let message: String

    var body: some View {
        VStack(spacing: 14) {
            Image(systemName: icon)
                .font(.system(size: 44, weight: .light))
                .foregroundStyle(KovaTheme.accent)
                .frame(width: 92, height: 92)
                .background(
                    KovaTheme.accent.opacity(0.1),
                    in: Circle()
                )

            Text(title)
                .font(.title3.weight(.semibold))
                .foregroundStyle(.primary)

            Text(message)
                .font(.subheadline)
                .foregroundStyle(.secondary)
                .multilineTextAlignment(.center)
        }
        .padding(32)
        .frame(maxWidth: .infinity)
    }
}
