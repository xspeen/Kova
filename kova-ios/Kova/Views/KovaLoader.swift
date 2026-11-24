import SwiftUI

/// Kova's signature loading indicator: two counter-rotating arcs and a
/// breathing core dot, all in the brand accent. Used while data work is
/// in flight so the app never feels frozen.
struct KovaLoader: View {

    var size: CGFloat = 48
    var tint: Color = KovaTheme.accent

    @State private var spinning = false
    @State private var breathing = false

    var body: some View {
        ZStack {
            Circle()
                .stroke(tint.opacity(0.14), lineWidth: size * 0.11)

            Circle()
                .trim(from: 0.0, to: 0.32)
                .stroke(
                    tint,
                    style: StrokeStyle(lineWidth: size * 0.11, lineCap: .round)
                )
                .rotationEffect(.degrees(spinning ? 360 : 0))
                .animation(
                    .linear(duration: 0.85).repeatForever(autoreverses: false),
                    value: spinning
                )

            Circle()
                .trim(from: 0.0, to: 0.16)
                .stroke(
                    tint.opacity(0.55),
                    style: StrokeStyle(lineWidth: size * 0.11, lineCap: .round)
                )
                .rotationEffect(.degrees(spinning ? -360 : 0))
                .animation(
                    .linear(duration: 1.4).repeatForever(autoreverses: false),
                    value: spinning
                )

            Circle()
                .fill(tint)
                .frame(width: size * 0.18, height: size * 0.18)
                .scaleEffect(breathing ? 1.25 : 0.8)
                .opacity(breathing ? 0.9 : 0.5)
                .animation(
                    .easeInOut(duration: 0.8).repeatForever(autoreverses: true),
                    value: breathing
                )
        }
        .frame(width: size, height: size)
        .accessibilityLabel("Loading")
        .onAppear {
            spinning = true
            breathing = true
        }
    }
}

/// Full-screen scrim with the loader centered, used during import work.
struct KovaLoaderOverlay: View {

    var message: String

    var body: some View {
        ZStack {
            Color(.systemBackground).opacity(0.82).ignoresSafeArea()

            VStack(spacing: 18) {
                KovaLoader(size: 54)
                Text(message)
                    .font(.subheadline.weight(.medium))
                    .foregroundStyle(.secondary)
            }
            .padding(32)
            .background(
                RoundedRectangle(cornerRadius: 24, style: .continuous)
                    .fill(Color(.secondarySystemBackground))
                    .shadow(color: .black.opacity(0.12), radius: 22, y: 8)
            )
        }
        .transition(.opacity)
        .zIndex(9)
    }
}
