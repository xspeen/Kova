package com.kova.app.ui.nav

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kova.app.ui.editor.NoteEditorScreen
import com.kova.app.ui.home.HomeScreen
import com.kova.app.ui.settings.SettingsScreen
import com.kova.app.ui.splash.SplashScreen
import com.kova.app.viewmodel.NoteViewModel

/** Route constants for the single-activity, Compose-only navigation graph. */
object Routes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val EDITOR = "editor/{id}"
    const val SETTINGS = "settings"

    fun editor(noteId: Long): String = "editor/$noteId"
}

@Composable
fun KovaNavHost(
    viewModel: NoteViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        modifier = modifier,
        enterTransition = {
            fadeIn(animationSpec = tween(280)) +
                slideInHorizontally(
                    animationSpec = tween(280),
                    initialOffsetX = { it / 12 }
                )
        },
        exitTransition = { fadeOut(animationSpec = tween(220)) },
        popEnterTransition = { fadeIn(animationSpec = tween(220)) },
        popExitTransition = {
            fadeOut(animationSpec = tween(220)) +
                slideOutHorizontally(
                    animationSpec = tween(220),
                    targetOffsetX = { it / 12 }
                )
        }
    ) {
        composable(route = Routes.SPLASH) {
            SplashScreen(
                onContinue = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Routes.HOME) {
            HomeScreen(
                viewModel = viewModel,
                onEditNote = { noteId -> navController.navigate(Routes.editor(noteId)) },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }

        composable(
            route = Routes.EDITOR,
            arguments = listOf(
                navArgument("id") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            ),
            enterTransition = {
                fadeIn(animationSpec = tween(220)) +
                    scaleIn(
                        animationSpec = tween(220),
                        initialScale = 0.92f
                    )
            },
            exitTransition = { fadeOut(animationSpec = tween(180)) },
            popEnterTransition = { fadeIn(animationSpec = tween(180)) },
            popExitTransition = {
                fadeOut(animationSpec = tween(180)) +
                    scaleOut(
                        animationSpec = tween(180),
                        targetScale = 0.92f
                    )
            }
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("id") ?: -1L
            NoteEditorScreen(
                viewModel = viewModel,
                noteId = noteId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(route = Routes.SETTINGS) {
            SettingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
