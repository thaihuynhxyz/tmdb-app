package xyz.thaihuynh.tmdb.ui

import android.content.Context
import android.net.ConnectivityManager
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Movie : Screen("movie/{id}") {
        fun createRoute(id: Int) = "movie/$id"
    }
}

@Composable
fun rememberTmdbAppState(
    navController: NavHostController = rememberNavController(),
    context: Context = LocalContext.current,
) = remember(navController, context) {
    TmdbAppState(navController, context)
}

class TmdbAppState(
    val navController: NavHostController,
    context: Context,
) {
    private val _isOnline = MutableStateFlow(false)
    val isOnline: StateFlow<Boolean>
        get() = _isOnline
    init {
        val cm = ContextCompat.getSystemService(context, ConnectivityManager::class.java)
        cm?.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: android.net.Network) {
                // check current thread
                android.os.Handler(Looper.getMainLooper()).post {
                    _isOnline.value = true
                }
            }

            override fun onLost(network: android.net.Network) {
                android.os.Handler(Looper.getMainLooper()).post {
                    _isOnline.value = false
                }
            }
        })
    }

    fun navigateToMovieDetail(id: Int, from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            navController.navigate(Screen.Movie.createRoute(id))
        }
    }

    fun navigateBack() {
        navController.popBackStack()
    }
}

/**
 * If the lifecycle is not resumed it means this NavBackStackEntry already processed a nav event.
 *
 * This is used to de-duplicate navigation events.
 */
private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED