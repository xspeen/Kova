package com.kova.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kova.app.ui.nav.KovaNavHost
import com.kova.app.ui.theme.KovaTheme
import com.kova.app.viewmodel.NoteViewModel

/**
 * Single activity for the entire app. All screens are Compose destinations.
 * Kova is 100% local-first: no INTERNET permission, no analytics, no ads,
 * no tracking.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: NoteViewModel = viewModel(
                factory = NoteViewModel.factory(applicationContext)
            )
            val themeMode by viewModel.themeMode.collectAsState()
            KovaTheme(themeMode = themeMode) {
                KovaNavHost(viewModel = viewModel)
            }
        }
    }
}
