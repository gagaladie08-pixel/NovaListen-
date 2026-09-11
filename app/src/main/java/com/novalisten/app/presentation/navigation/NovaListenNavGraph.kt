// presentation/navigation/NovaListenNavGraph.kt
package com.novalisten.app.presentation.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

/**
 * Destinations de navigation NovaListen.
 * Sera enrichi à chaque phase UI.
 */
sealed class Screen(val route: String) {
    data object Stats          : Screen("stats")
    data object Certifications : Screen("certifications")
    data object Pantheon       : Screen("pantheon")
    data object Billboard      : Screen("billboard")
    data object HallOfFame     : Screen("hall_of_fame")
    data object Records        : Screen("records")
    data object Awards         : Screen("awards")
}

/**
 * Graphe de navigation principal de NovaListen.
 * Placeholder — les vrais écrans seront branchés en Phase 6.
 */
@Composable
fun NovaListenNavGraph(
    modifier: NavHostController = rememberNavController()
) {
    NavHost(
        navController  = modifier,
        startDestination = Screen.Stats.route
    ) {
        composable(Screen.Stats.route) {
            // Placeholder — Écran Stats (Phase 6)
            Text(text = "🎵 NovaListen — Stats")
        }
        composable(Screen.Certifications.route) {
            Text(text = "💎 Certifications")
        }
        composable(Screen.Pantheon.route) {
            Text(text = "👑 Panthéon")
        }
        composable(Screen.Billboard.route) {
            Text(text = "🏆 Billboard")
        }
        composable(Screen.HallOfFame.route) {
            Text(text = "🏛️ Hall of Fame")
        }
        composable(Screen.Records.route) {
            Text(text = "📈 Records")
        }
        composable(Screen.Awards.route) {
            Text(text = "🏅 Nova Awards")
        }
    }
}