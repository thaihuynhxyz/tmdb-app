package xyz.thaihuynh.tmdb.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.window.layout.DisplayFeature
import xyz.thaihuynh.tmdb.feature.detail.DetailMovie
import xyz.thaihuynh.tmdb.feature.search.SearchMovies
import xyz.thaihuynh.tmdb.feature.trending.TrendingMovies

@Composable
fun TmdbApp(
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    appState: TmdbAppState = rememberTmdbAppState()
) {
    NavHost(
        navController = appState.navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) { backStackEntry ->
            HomeScreen(
                navigateToPlayer = { movieId ->
                    appState.navigateToMovieDetail(movieId, backStackEntry)
                }
            )
        }
        composable(Screen.Movie.route) { _ ->
            DetailMovie(
                windowSizeClass = windowSizeClass,
                displayFeatures = displayFeatures,
                onUpPress = appState::navigateBack,
            )
        }
    }
}

@Composable
fun HomeScreen(
    navigateToPlayer: (Int) -> Unit
) {
    Box {
        SearchMovies(
            modifier = Modifier.fillMaxWidth(),
            navigateToPlayer = navigateToPlayer,
        )
        TrendingMovies(
            modifier = Modifier.padding(top = 80.dp),
            navigateToPlayer = navigateToPlayer,
        )
    }
}
