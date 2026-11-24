import SwiftUI
import UIKit

// MARK: - Brand constants

/// Central place for Kova brand colors, the note color palette and
/// small shared brand components. No hardcoded colors anywhere else.
enum KovaTheme {

    /// Brand accent: indigo/violet #6C5CE7.
    static let accent = Color(red: 108 / 255.0, green: 92 / 255.0, blue: 231 / 255.0)

    static let defaultColorIndex = 0

    // MARK: Palette (default + 8 brand swatches)

    struct Swatch: Identifiable, Equatable {
        let index: Int
        let name: String
        let color: Color
        /// True when text/checkmarks drawn on top of this swatch should be dark.
        let usesDarkText: Bool
        var id: Int { index }
    }

    static let palette: [Swatch] = [
        Swatch(index: 0, name: "Default", color: Color(uiColor: .secondarySystemBackground), usesDarkText: true),
        Swatch(index: 1, name: "Indigo",  color: Color(red: 108 / 255, green: 92 / 255,  blue: 231 / 255), usesDarkText: false),
        Swatch(index: 2, name: "Coral",   color: Color(red: 255 / 255, green: 107 / 255, blue: 107 / 255), usesDarkText: false),
        Swatch(index: 3, name: "Amber",   color: Color(red: 254 / 255, green: 202 / 255, blue: 87 / 255),  usesDarkText: true),
        Swatch(index: 4, name: "Mint",    color: Color(red: 29 / 255,  green: 209 / 255, blue: 161 / 255), usesDarkText: true),
        Swatch(index: 5, name: "Sky",     color: Color(red: 84 / 255,  green: 160 / 255, blue: 255 / 255), usesDarkText: false),
        Swatch(index: 6, name: "Rose",    color: Color(red: 243 / 255, green: 104 / 255, blue: 224 / 255), usesDarkText: true),
        Swatch(index: 7, name: "Teal",    color: Color(red: 0 / 255,   green: 210 / 255, blue: 211 / 255), usesDarkText: true),
        Swatch(index: 8, name: "Slate",   color: Color(red: 87 / 255,  green: 96 / 255,  blue: 111 / 255), usesDarkText: false)
    ]

    static func swatch(for index: Int) -> Swatch {
        palette.first { $0.index == index } ?? palette[0]
    }

    static func color(for index: Int) -> Color {
        swatch(for: index).color
    }

    static func checkColor(for index: Int) -> Color {
        swatch(for: index).usesDarkText ? .black.opacity(0.7) : .white
    }

    // MARK: Shared animation curves

    static let softSpring = Animation.spring(response: 0.35, dampingFraction: 0.85)
    static let snappySpring = Animation.spring(response: 0.28, dampingFraction: 0.72)
}

// MARK: - Geometric "K" mark

/// The Kova logo: a geometric "K" drawn with strokes on a rounded square.
struct KMark: Shape {
    func path(in rect: CGRect) -> Path {
        var path = Path()
        let w = rect.width
        let h = rect.height
        // Vertical stem
        path.move(to: CGPoint(x: w * 0.30, y: h * 0.16))
        path.addLine(to: CGPoint(x: w * 0.30, y: h * 0.84))
        // Upper and lower diagonal arms meeting at the middle
        path.move(to: CGPoint(x: w * 0.72, y: h * 0.16))
        path.addLine(to: CGPoint(x: w * 0.36, y: h * 0.51))
        path.addLine(to: CGPoint(x: w * 0.74, y: h * 0.86))
        return path
    }
}

/// Rounded brand square containing the "K" mark.
struct BrandMark: View {
    var size: CGFloat = 96

    var body: some View {
        RoundedRectangle(cornerRadius: size * 0.26, style: .continuous)
            .fill(KovaTheme.accent)
            .frame(width: size, height: size)
            .overlay {
                KMark()
                    .stroke(
                        .white,
                        style: StrokeStyle(
                            lineWidth: size * 0.11,
                            lineCap: .round,
                            lineJoin: .round
                        )
                    )
                    .padding(size * 0.16)
            }
            .shadow(color: KovaTheme.accent.opacity(0.35), radius: size * 0.14, y: size * 0.06)
    }
}

// MARK: - Press feedback

/// Subtle scale-down press feedback for premium feel.
struct ScaleButtonStyle: ButtonStyle {
    var pressedScale: CGFloat = 0.93

    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .scaleEffect(configuration.isPressed ? pressedScale : 1)
            .animation(.spring(response: 0.25, dampingFraction: 0.6), value: configuration.isPressed)
    }
}
