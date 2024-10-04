@file:Suppress("UnusedImport", "unused", "DEPRECATION")

package com.example.bookshelf.ui.screens.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.example.bookshelf.R
import com.example.bookshelf.model.Book
import com.example.bookshelf.ui.screens.query_screen.QueryViewModel

@Composable
fun FavoriteScreen(
    onBackClick: () -> Unit,
    viewModel: QueryViewModel = viewModel()
) {
    val favoriteBooks = viewModel.favoriteBooks

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Mis libros favoritos", modifier = Modifier.padding(16.dp))

        if (favoriteBooks.isEmpty()) {
            Text(text = "No tienes libros favoritos todavía")
        } else {
            LazyColumn {
                items(favoriteBooks) { book ->
                    FavoriteBookItem(book)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onBackClick) {
            Text(text = "Volver")
        }
    }
}

@Composable
fun FavoriteBookItem(book: Book) {
    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        val imagePainter = rememberImagePainter(data = book.volumeInfo.imageLinks?.httpsThumbnail)

        Image(
            painter = imagePainter,
            contentDescription = null,
            modifier = Modifier.size(80.dp).padding(end = 16.dp)
        )

        Column {
            Text(text = book.volumeInfo.title)
            Text(text = book.getPrice())
            Text(text = book.volumeInfo.allAuthorsx)
        }
    }
}