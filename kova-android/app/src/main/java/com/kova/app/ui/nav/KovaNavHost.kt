package com.kova.app.ui.nav

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
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
        enterTransition = { fadeIn(animationSpec = tween(280)) },
        exitTransition = { fadeOut(animationSpec = tween(220)) },
        popEnterTransition = { fadeIn(animationSpec = tween(220)) },
        popExitTransition = { fadeOut(animationSpec = tween(220)) }
    ) {
        composable(
            route = Routes.SPLASH,
            exitTransition = { fadeOut(animationSpec = tween(220)) }
        ) {
            SplashScreen(
                onContinue = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Routes.HOME,
            enterTransition = {
                fadeIn(animationSpec = tween(320)) +
                    scaleIn(
                        animationSpec = tween(320),
                        initialScale = 1.08f
                    )
            }
        ) {
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
                fadeIn(animationSpec = tween(260)) +
                    slideInVertically(
                        animationSpec = tween(260),
                        initialOffsetY = { it / 6 }
                    )
            },
            exitTransition = { fadeOut(animationSpec = tween(220)) },
            popEnterTransition = { fadeIn(animationSpec = tween(260)) },
            popExitTransition = {
                fadeOut(animationSpec = tween(260)) +
                    slideOutVertically(
                        animationSpec = tween(260),
                        targetOffsetY = { it / 6 }
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

        composable(
            route = Routes.SETTINGS,
            enterTransition = {
                fadeIn(animationSpec = tween(300)) +
                    slideInHorizontally(
                        animationSpec = tween(300),
                        initialOffsetX = { it }
                    )
            },
            exitTransition = { fadeOut(animationSpec = tween(220)) },
            popEnterTransition = { fadeIn(animationSpec = tween(260)) },
            popExitTransition = {
                fadeOut(animationSpec = tween(300)) +
                    slideOutHorizontally(
                        animationSpec = tween(300),
                        targetOffsetX = { it }
                    )
            }
        ) {
            SettingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
