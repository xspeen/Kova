import SwiftUI

/// Landing experience: a full-screen, swipeable hero pager with three
/// slides (brand, library, privacy), an animated page indicator and a
/// single "Get started" action. Users slide to browse; no taps needed.
struct SplashView: View {

    let onContinue: () -> Void

    @State private var page = 0
    @State private var appeared = false

    var body: some View {
        ZStack {
            Color(.systemBackground).ignoresSafeArea()

            VStack(spacing: 0) {
                TabView(selection: $page) {
                    brandSlide.tag(0)
                    librarySlide.tag(1)
                    privacySlide.tag(2)
                }
                .tabViewStyle(.page(indexDisplayMode: .never))
                .animation(.spring(response: 0.5, dampingFraction: 0.85), value: page)

                VStack(spacing: 22) {
                    pageIndicator
                    getStartedButton
                }
                .padding(.horizontal, 32)
                .padding(.bottom, 26)
            }
        }
        .onAppear {
            withAnimation(.spring(response: 0.6, dampingFraction: 0.8)) {
                appeared = true
            }
        }
    }

    // MARK: Slide 1 - Brand

    private var brandSlide: some View {
        SlideLayout {
            FloatingBrandMark(size: 132)
                .opacity(appeared ? 1 : 0)
                .scaleEffect(appeared ? 1 : 0.86)

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
        }
    }

    // MARK: Slide 2 - Library

    private var librarySlide: some View {
        SlideLayout {
            LibraryShelfIllustration()
                .frame(height: 260)
                .opacity(appeared ? 1 : 0)
                .scaleEffect(appeared ? 1 : 0.94)

            Text("A shelf for every thought")
                .font(.title2.weight(.bold))
                .padding(.top, 26)
                .opacity(appeared ? 1 : 0)
                .offset(y: appeared ? 0 : 10)

            Text("Everything you save rests on your own shelf — ready even when you are offline.")
                .font(.subheadline)
                .foregroundStyle(.secondary)
                .multilineTextAlignment(.center)
                .padding(.top, 6)
                .padding(.horizontal, 36)
                .opacity(appeared ? 1 : 0)
                .offset(y: appeared ? 0 : 10)
        }
    }

    // MARK: Slide 3 - Privacy

    private var privacySlide: some View {
        SlideLayout {
            PrivacyShieldIllustration()
                .frame(height: 260)
                .opacity(appeared ? 1 : 0)
                .scaleEffect(appeared ? 1 : 0.94)

            Text("Private by design")
                .font(.title2.weight(.bold))
                .padding(.top, 26)
                .opacity(appeared ? 1 : 0)
                .offset(y: appeared ? 0 : 10)

            Text("Your notes never leave this device. There is no account, no server, no exception.")
                .font(.subheadline)
                .foregroundStyle(.secondary)
                .multilineTextAlignment(.center)
                .padding(.top, 6)
                .padding(.horizontal, 36)
                .opacity(appeared ? 1 : 0)
                .offset(y: appeared ? 0 : 10)
        }
    }

    // MARK: Indicator + action

    private var pageIndicator: some View {
        HStack(spacing: 8) {
            ForEach(0..<3, id: \.self) { index in
                Capsule()
                    .fill(index == page ? KovaTheme.accent : KovaTheme.accent.opacity(0.25))
                    .frame(width: index == page ? 26 : 8, height: 8)
                    .animation(.spring(response: 0.4, dampingFraction: 0.8), value: page)
            }
        }
    }

    private var getStartedButton: some View {
        Button {
            onContinue()
        } label: {
            Text("Get started")
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
        .opacity(appeared ? 1 : 0)
        .animation(.easeInOut(duration: 0.45).delay(0.4), value: appeared)
    }
}

// MARK: - Shared slide layout

/// Vertical layout used by every hero slide: content centered in the
/// full slide height, weighted so illustrations sit slightly high.
private struct SlideLayout<Content: View>: View {

    @ViewBuilder let content: Content

    var body: some View {
        VStack(spacing: 0) {
            Spacer(minLength: 0)
                .frame(maxHeight: 999)
            content
            Spacer(minLength: 0)
                .frame(maxHeight: 999)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .padding(.horizontal, 32)
    }
}

// MARK: - Brand mark with float loop

/// Brand tile that gently bobs up and down on an infinite loop, sitting
/// inside a soft radial halo.
private struct FloatingBrandMark: View {

    var size: CGFloat

    @State private var floating = false

    var body: some View {
        ZStack {
            Circle()
                .fill(
                    RadialGradient(
                        colors: [KovaTheme.accent.opacity(0.22), .clear],
                        center: .center,
                        startRadius: size * 0.35,
                        endRadius: size * 0.95
                    )
                )
                .frame(width: size * 2.2, height: size * 2.2)

            BrandMark(size: size)
                .offset(y: floating ? -8 : 8)
        }
        .onAppear {
            withAnimation(
                .easeInOut(duration: 1.6).repeatForever(autoreverses: true)
            ) {
                floating = true
            }
        }
    }
}

// MARK: - Library shelf illustration

/// A shelf of pastel books, one tilted and one lying flat, drawn with
/// plain shapes — the "captured thoughts as library books" metaphor.
private struct LibraryShelfIllustration: View {

    // Book spines: (width, height above shelf, color)
    private let books: [(CGFloat, CGFloat, Color)] = [
        (30, 84, Color(red: 0.73, green: 0.84, blue: 0.97)),
        (34, 94, Color(red: 0.95, green: 0.89, blue: 0.55)),
        (26, 76, Color(red: 0.77, green: 0.91, blue: 0.78)),
        (30, 80, Color(red: 0.97, green: 0.76, blue: 0.72)),
        (32, 88, Color(red: 0.85, green: 0.75, blue: 0.94)),
        (28, 72, Color(red: 0.96, green: 0.78, blue: 0.86))
    ]

    var body: some View {
        GeometryReader { proxy in
            let shelfY = proxy.size.height * 0.86
            let shelfX = proxy.size.width * 0.16

            ZStack(alignment: .bottomLeading) {
                // Ground shadow
                Ellipse()
                    .fill(Color.black.opacity(0.08))
                    .frame(width: proxy.size.width * 0.6, height: 12)
                    .position(
                        x: proxy.size.width * 0.5,
                        y: shelfY + 16
                    )

                // Standing books
                HStack(alignment: .bottom, spacing: 4) {
                    ForEach(Array(books.enumerated()), id: \.offset) { index, book in
                        if index == 3 {
                            TiltedBook(width: book.0, height: book.1, color: book.2)
                        } else {
                            standingBook(book)
                        }
                    }
                }
                .position(
                    x: shelfX + bookRowWidth / 2 + 6,
                    y: shelfY - 6
                )

                // Flat book resting on top
                RoundedRectangle(cornerRadius: 4, style: .continuous)
                    .fill(Color(red: 0.73, green: 0.84, blue: 0.97))
                    .frame(width: 52, height: 10)
                    .position(
                        x: shelfX + bookRowWidth * 0.78,
                        y: shelfY - 108
                    )

                // Wooden shelf
                RoundedRectangle(cornerRadius: 5, style: .continuous)
                    .fill(Color(red: 0.66, green: 0.45, blue: 0.31))
                    .frame(width: proxy.size.width * 0.68, height: 11)
                    .position(x: proxy.size.width * 0.5, y: shelfY)
            }
        }
        .frame(maxWidth: 320)
    }

    private var bookRowWidth: CGFloat {
        books.reduce(0) { $0 + $1.0 } + CGFloat(books.count - 1) * 4
    }

    private func standingBook(_ book: (CGFloat, CGFloat, Color)) -> some View {
        ZStack(alignment: .top) {
            RoundedRectangle(cornerRadius: 4, style: .continuous)
                .fill(book.2)
                .frame(width: book.0, height: book.1)
                .overlay(alignment: .bottom) {
                    // Decorative spine band
                    Rectangle()
                        .fill(.white.opacity(0.3))
                        .frame(width: book.0 - 10, height: 5)
                        .padding(.bottom, 14)
                }

            if book.1 == 94 {
                // Indigo bookmark ribbon on the tallest book
                BookmarkRibbon()
                    .frame(width: 8, height: 32)
                    .offset(y: 6)
            }
        }
    }
}

private struct TiltedBook: View {
    let width: CGFloat
    let height: CGFloat
    let color: Color

    var body: some View {
        RoundedRectangle(cornerRadius: 4, style: .continuous)
            .fill(color)
            .frame(width: width, height: height)
            .rotationEffect(.degrees(8), anchor: .bottomTrailing)
    }
}

private struct BookmarkRibbon: View {
    var body: some View {
        KBookmarkShape()
            .fill(KovaTheme.accent)
    }
}

private struct KBookmarkShape: Shape {
    func path(in rect: CGRect) -> Path {
        var path = Path()
        path.move(to: CGPoint(x: 0, y: 0))
        path.addLine(to: CGPoint(x: rect.width, y: 0))
        path.addLine(to: CGPoint(x: rect.width, y: rect.height))
        path.addLine(to: CGPoint(x: rect.width / 2, y: rect.height - 7))
        path.addLine(to: CGPoint(x: 0, y: rect.height))
        path.closeSubpath()
        return path
    }
}

// MARK: - Privacy shield illustration

/// Soft shield with an outlined core and a solid lock, with floating
/// ambient dots — the "private by design" hero visual.
private struct PrivacyShieldIllustration: View {

    @State private var breathing = false

    var body: some View {
        ZStack {
            // Soft filled shield silhouette
            ShieldShape()
                .fill(KovaTheme.accent.opacity(0.16))
                .frame(width: 220, height: 260)

            // Outlined shield core
            ShieldShape()
                .stroke(KovaTheme.accent, lineWidth: 4)
                .frame(width: 168, height: 200)

            // Lock
            VStack(spacing: 0) {
                LockShackle()
                    .stroke(KovaTheme.accent, style: StrokeStyle(lineWidth: 5, lineCap: .round))
                    .frame(width: 20, height: 14)
                RoundedRectangle(cornerRadius: 4, style: .continuous)
                    .fill(KovaTheme.accent)
                    .frame(width: 34, height: 30)
                    .overlay {
                        Circle()
                            .fill(.white)
                            .frame(width: 11, height: 11)
                            .offset(y: -3)
                        RoundedRectangle(cornerRadius: 1.5)
                            .fill(.white)
                            .frame(width: 4, height: 11)
                            .offset(y: 5)
                    }
                    .offset(y: -1)
            }
            .scaleEffect(breathing ? 1.04 : 1)
            .animation(.easeInOut(duration: 1.8).repeatForever(autoreverses: true), value: breathing)

            // Ambient dots
            Circle()
                .fill(Color(red: 0.95, green: 0.89, blue: 0.55).opacity(0.7))
                .frame(width: 22, height: 22)
                .offset(x: 74, y: -102)

            Circle()
                .fill(Color(red: 0.85, green: 0.75, blue: 0.94).opacity(0.7))
                .frame(width: 14, height: 14)
                .offset(x: -84, y: 78)

            // Dotted arc hints
            ArcDash()
                .offset(x: 62, y: -84)
            ArcDash()
                .rotationEffect(.degrees(160))
                .offset(x: -66, y: 92)
        }
        .onAppear { breathing = true }
    }
}

private struct ShieldShape: Shape {
    func path(in rect: CGRect) -> Path {
        var path = Path()
        let w = rect.width
        let h = rect.height
        path.move(to: CGPoint(x: w * 0.5, y: 0))
        path.addLine(to: CGPoint(x: w, y: h * 0.2))
        path.addLine(to: CGPoint(x: w, y: h * 0.52))
        path.addCurve(
            to: CGPoint(x: w * 0.5, y: h),
            control1: CGPoint(x: w, y: h * 0.76),
            control2: CGPoint(x: w * 0.76, y: h)
        )
        path.addCurve(
            to: CGPoint(x: 0, y: h * 0.52),
            control1: CGPoint(x: w * 0.24, y: h),
            control2: CGPoint(x: 0, y: h * 0.76)
        )
        path.addLine(to: CGPoint(x: 0, y: h * 0.2))
        path.closeSubpath()
        return path
    }
}

private struct LockShackle: Shape {
    func path(in rect: CGRect) -> Path {
        var path = Path()
        path.move(to: CGPoint(x: 0, y: rect.height))
        path.addLine(to: CGPoint(x: 0, y: rect.height * 0.35))
        path.addArc(
            center: CGPoint(x: rect.width / 2, y: rect.height * 0.35),
            radius: rect.width / 2,
            startAngle: .degrees(180),
            endAngle: .degrees(0),
            clockwise: false
        )
        path.addLine(to: CGPoint(x: rect.width, y: rect.height))
        return path
    }
}

private struct ArcDash: View {
    var body: some View {
        Circle()
            .trim(from: 0, to: 0.12)
            .stroke(
                KovaTheme.accent.opacity(0.3),
                style: StrokeStyle(lineWidth: 3, lineCap: .round, dash: [1, 9])
            )
            .frame(width: 210, height: 210)
    }
}
