@file:Suppress("UNUSED_PARAMETER", "KotlinConstantConditions", "KotlinConstantConditions",
    "USELESS_ELVIS", "UNNECESSARY_SAFE_CALL", "UnusedImport", "UnusedImport", "UnusedImport"
)

package com.example.bookshelf.ui.screens.detail_screen

import androidx.compose.foundation.layout.*
import com.example.bookshelf.ui.screens.detail_screen.DetailsViewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bookshelf.R
import com.example.bookshelf.model.Book
import com.example.bookshelf.ui.screens.components.ErrorScreen
import com.example.bookshelf.ui.screens.components.LoadingScreen

@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    viewModel: DetailsViewModel,
    retryAction: () -> Unit,
    title: String,
    onFavoriteClick: () -> Unit // Añadido el parámetro onFavoriteClick
) {
    val uiStateDet = viewModel.uiStateDetail.collectAsState().value

    // LaunchedEffect para cargar el libro solo si el estado es Loading
    LaunchedEffect(viewModel.selectedBookId) {
        if (uiStateDet is DetailsUiState.Loading && viewModel.selectedBookId.isNotEmpty()) {
            viewModel.getBook(viewModel.selectedBookId)
        }
    }

    when (uiStateDet) {
        is DetailsUiState.Loading -> {
            LoadingScreen()  // Muestra la pantalla de carga
        }
        is DetailsUiState.Error -> {
            ErrorScreen(retryAction = retryAction)  // Muestra la pantalla de error
        }
        is DetailsUiState.Success -> {
            BookDetails(
                book = uiStateDet.book,
                onFavoriteClick = {
                    viewModel.toggleFavorite(uiStateDet.book.id) // Manejamos el click de favorito
                    onFavoriteClick() // Llamamos a la acción que pasaste
                },
                isBookFavorite = viewModel.isBookFavorite(uiStateDet.book.id) // Verificamos si es favorito
            )  // Muestra los detalles del libro
        }
    }
}

@Composable
fun BookDetails(book: Book, onFavoriteClick: () -> Unit, isBookFavorite: Boolean) {
    val cleanDescription = stripHtml(book.volumeInfo.description ?: "Descripción no disponible")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Título del libro
            Text(
                text = book.volumeInfo.title ?: "Título no disponible",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Imagen del libro
            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(book.volumeInfo.imageLinks?.thumbnail ?: "")
                    .crossfade(true)
                    .build(),
                contentDescription = book.volumeInfo.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                error = painterResource(id = R.drawable.ic_broken_image),
                placeholder = painterResource(id = R.drawable.loading_img),
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Subtítulo del libro
            book.volumeInfo.subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Autores del libro
            val authors = book.volumeInfo.allAuthors().ifEmpty { "Autores no disponibles" }
            Text(
                text = "Autores: $authors",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Precio del libro
            val price = book.saleInfo.getPrice2?.takeIf { it.isNotEmpty() } ?: "Precio no disponible"
            Text(
                text = "Precio: $price",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // País del libro
            Text(
                text = "País: ${book.saleInfo.country ?: "No disponible"}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Descripción del libro
            Text(
                text = cleanDescription,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Botón de favorito
            Button(onClick = onFavoriteClick) {
                Text(text = if (isBookFavorite) "Eliminar de Favoritos" else "Agregar a Favoritos")
            }
        }
    }
}

// Función para eliminar etiquetas HTML de la descripción
fun stripHtml(html: String): String {
    return html.replace(Regex("<.*?>"), "") // Elimina todas las etiquetas HTML
}