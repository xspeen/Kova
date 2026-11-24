import SwiftUI

/// Launch splash shown on every launch: geometric "K" logo, app name,
/// tagline and three privacy features, with a Continue button that
/// animates the splash away to reveal MainView.
struct SplashView: View {

    let onContinue: () -> Void

    @State private var appeared = false

    private struct FeatureItem {
        let icon: String
        let title: String
        let caption: String
    }

    private let features: [FeatureItem] = [
        FeatureItem(icon: "wifi.slash", title: "Offline first", caption: "Everything stays on this device."),
        FeatureItem(icon: "hand.raised", title: "No ads", caption: "Nothing to sell, nothing in your way."),
        FeatureItem(icon: "eye.slash", title: "No tracking", caption: "Zero analytics. Zero leaks.")
    ]

    var body: some View {
        ZStack {
            Color(.systemBackground).ignoresSafeArea()

            VStack(spacing: 0) {
                Spacer()

                BrandMark(size: 104)
                    .scaleEffect(appeared ? 1 : 0.82)
                    .opacity(appeared ? 1 : 0)

                Text("Kova")
                    .font(.system(size: 42, weight: .bold, design: .rounded))
                    .padding(.top, 26)
                    .opacity(appeared ? 1 : 0)
                    .offset(y: appeared ? 0 : 10)

                Text("Your thoughts. Yours alone.")
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
                    .padding(.top, 6)
                    .opacity(appeared ? 1 : 0)
                    .offset(y: appeared ? 0 : 10)

                VStack(spacing: 14) {
                    ForEach(Array(features.enumerated()), id: \.offset) { index, feature in
                        featureRow(feature)
                            .opacity(appeared ? 1 : 0)
                            .offset(y: appeared ? 0 : 14)
                            .animation(
                                .spring(response: 0.55, dampingFraction: 0.85)
                                    .delay(0.15 + Double(index) * 0.1),
                                value: appeared
                            )
                    }
                }
                .padding(.top, 40)

                Spacer()

                continueButton
                    .opacity(appeared ? 1 : 0)
                    .animation(.easeInOut(duration: 0.45).delay(0.5), value: appeared)
                    .padding(.bottom, 28)
            }
            .padding(.horizontal, 32)
        }
        .onAppear {
            withAnimation(.spring(response: 0.6, dampingFraction: 0.8)) {
                appeared = true
            }
        }
    }

    // MARK: Pieces

    private func featureRow(_ feature: FeatureItem) -> some View {
        HStack(spacing: 14) {
            Image(systemName: feature.icon)
                .font(.system(size: 16, weight: .semibold))
                .foregroundStyle(KovaTheme.accent)
                .frame(width: 42, height: 42)
                .background(
                    KovaTheme.accent.opacity(0.12),
                    in: RoundedRectangle(cornerRadius: 13, style: .continuous)
                )

            VStack(alignment: .leading, spacing: 2) {
                Text(feature.title)
                    .font(.subheadline.weight(.semibold))
                    .foregroundStyle(.primary)
                Text(feature.caption)
                    .font(.caption)
                    .foregroundStyle(.secondary)
            }

            Spacer(minLength: 0)
        }
    }

    private var continueButton: some View {
        Button {
            onContinue()
        } label: {
            Text("Continue")
                .font(.headline)
                .foregroundStyle(.white)
                .frame(maxWidth: .infinity)
                .padding(.vertical, 16)
                .background(
                    KovaTheme.accent,
                    in: RoundedRectangle(cornerRadius: 16, style: .continuous)
                )
                .shadow(color: KovaTheme.accent.opacity(0.4), radius: 14, y: 6)
        }
        .buttonStyle(ScaleButtonStyle())
    }
}
