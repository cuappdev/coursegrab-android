package com.cornellappdev.coursegrab.ui.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cornellappdev.coursegrab.ui.details.CourseDetailsScreen
import com.cornellappdev.coursegrab.ui.login.LoginScreen
import com.cornellappdev.coursegrab.ui.main.MainScreen
import com.cornellappdev.coursegrab.ui.search.SearchScreen
import com.cornellappdev.coursegrab.ui.settings.SettingsScreen

private const val STUDENT_CENTER_URL = "http://studentcenter.cornell.edu"

@Composable
fun CourseGrabNavHost(
    onReady: () -> Unit,
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val openUrl: (String) -> Unit = { url ->
        context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
    }

    NavHost(navController = navController, startDestination = Route.Login) {
        composable<Route.Login> {
            LoginScreen(
                onReady = onReady,
                onNavigateToMain = {
                    navController.navigate(Route.Home) {
                        popUpTo<Route.Login> { inclusive = true }
                    }
                }
            )
        }

        composable<Route.Home> {
            LaunchedEffect(Unit) { onReady() }
            MainScreen(
                onOpenCourse = { navController.navigate(Route.CourseDetails(it)) },
                onOpenSettings = dropUnlessResumed { navController.navigate(Route.Settings) },
                onOpenSearch = dropUnlessResumed { navController.navigate(Route.Search) },
                onEnroll = { openUrl(STUDENT_CENTER_URL) }
            )
        }

        composable<Route.Search> {
            SearchScreen(
                onOpenCourse = { navController.navigate(Route.CourseDetails(it)) },
                onOpenSettings = dropUnlessResumed { navController.navigate(Route.Settings) },
                onBack = dropUnlessResumed { navController.popBackStack() }
            )
        }

        composable<Route.CourseDetails>(typeMap = Route.CourseDetails.typeMap) { entry ->
            CourseDetailsScreen(
                onBack = { trackingChanged ->
                    if (entry.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                        if (trackingChanged) navController.popBackStack<Route.Home>(inclusive = false)
                        else navController.popBackStack()
                    }
                }
            )
        }

        composable<Route.Settings> {
            SettingsScreen(
                onOpenLink = openUrl,
                onSignedOut = {
                    navController.navigate(Route.Login) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                },
                onBack = dropUnlessResumed { navController.popBackStack() }
            )
        }
    }
}
