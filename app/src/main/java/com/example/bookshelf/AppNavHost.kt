@file:Suppress("SpellCheckingInspection", "KotlinConstantConditions")

package com.example.bookshelf

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.bookshelf.ui.screens.components.FavoriteScreen
import com.example.bookshelf.ui.screens.detail_screen.DetailScreen
import com.example.bookshelf.ui.screens.detail_screen.DetailsUiState
import com.example.bookshelf.ui.screens.detail_screen.DetailsViewModel
import com.example.bookshelf.ui.screens.menu_screen.MenuScreen
import com.example.bookshelf.ui.screens.query_screen.QueryScreen
import com.example.bookshelf.ui.screens.query_screen.QueryViewModel

@Composable
fun BookshelfNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val queryViewModel: QueryViewModel = viewModel(factory = QueryViewModel.Factory)

    NavHost(
        navController = navController,
        startDestination = AppDestinations.MenuScreen.name,
        modifier = modifier
    ) {
        composable(route = AppDestinations.MenuScreen.name) {
            MenuScreen(
                onSearchClick = {
                    navController.navigate(AppDestinations.QueryScreen.name)
                },
                onFavClick = {
                    navController.navigate(AppDestinations.FavoriteScreen.name)
                }
            )
        }

        composable(route = AppDestinations.QueryScreen.name) {
            QueryScreen(
                viewModel = queryViewModel,
                retryAction = { queryViewModel.getBooks() },
                onDetailsClick = {
                    queryViewModel.selectedBookId = it.id
                    navController.navigate(AppDestinations.DetailScreen.name)
                },
            )
        }

        composable(route = AppDestinations.DetailScreen.name) {
            val detailViewModel: DetailsViewModel = viewModel(factory = DetailsViewModel.Factory)
            val selectedBookId = queryViewModel.selectedBookId

            // Ejecutar getBook solo cuando se carga por primera vez o cuando cambia el id
            LaunchedEffect(selectedBookId) {
                detailViewModel.getBook(selectedBookId)
            }

            // Acceder al estado del libro cargado
            val uiState = detailViewModel.uiStateDetail.collectAsStateWithLifecycle().value

            // Mostrar el título basado en el estado actual
            val screenTitle = when (uiState) {
                is DetailsUiState.Success -> "${AppDestinations.DetailScreen.title}${uiState.book.volumeInfo.title}"
                else -> AppDestinations.DetailScreen.title // Título por defecto mientras se carga o en error
            }

            DetailScreen(
                viewModel = detailViewModel,
                retryAction = { detailViewModel.getBook(selectedBookId) },
                title = screenTitle, // Pasamos el título para la UI
                onFavoriteClick = { // Aquí definimos la acción para el botón de favorito
                    detailViewModel.toggleFavorite(selectedBookId) // Llama a la función toggleFavorite
                }
            )
        }

        composable(route = AppDestinations.FavoriteScreen.name) {
            FavoriteScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                viewModel = queryViewModel // Compartimos el mismo ViewModel
            )
        }
    }
}