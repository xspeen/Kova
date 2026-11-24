import SwiftUI
import SwiftData

@main
struct KovaApp: App {

    /// 0 = system, 1 = light, 2 = dark. Shared with SettingsView.
    @AppStorage("kova.appearance") private var appearance: Int = 0

    /// The splash screen shows on every launch.
    @State private var showSplash = true

    var body: some Scene {
        WindowGroup {
            ZStack {
                MainView()

                if showSplash {
                    SplashView {
                        withAnimation(.easeInOut(duration: 0.5)) {
                            showSplash = false
                        }
                    }
                    .transition(.opacity.combined(with: .scale(scale: 1.04)))
                    .zIndex(10)
                }
            }
            .preferredColorScheme(scheme)
            .modelContainer(for: Note.self)
        }
    }

    private var scheme: ColorScheme? {
        switch appearance {
        case 1: return .light
        case 2: return .dark
        default: return nil
        }
    }
}
