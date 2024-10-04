@file:Suppress("unused", "TrailingComma")

package com.example.bookshelf.ui.screens.detail_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookshelf.BookshelfApplication
import com.example.bookshelf.data.BookshelfRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class DetailsViewModel(
    private val bookshelfRepository: BookshelfRepository
) : ViewModel() {

    var selectedBookId: String = ""
        private set

    private val _uiStateDetail = MutableStateFlow<DetailsUiState>(DetailsUiState.Loading)
    val uiStateDetail = _uiStateDetail.asStateFlow()

    private val favoriteBooks = mutableSetOf<String>() // Conjunto para almacenar los ID de los libros favoritos

    fun getBook(id: String) {
        selectedBookId = id
        viewModelScope.launch {
            _uiStateDetail.value = DetailsUiState.Loading
            try {
                val book = bookshelfRepository.getBook(id)
                if (book == null) {
                    _uiStateDetail.value = DetailsUiState.Error
                } else {
                    _uiStateDetail.value = DetailsUiState.Success(book)
                }
            } catch (e: IOException) {
                Log.e("DetailsViewModel", "Error fetching book: ${e.message}")
                _uiStateDetail.value = DetailsUiState.Error("Network Error")
            } catch (e: HttpException) {
                Log.e("DetailsViewModel", "HTTP error: ${e.message()}")
                _uiStateDetail.value = DetailsUiState.Error("HTTP Error")
            }
        }
    }

    fun isBookFavorite(bookId: String): Boolean {
        return favoriteBooks.contains(bookId) // Comprueba si el libro está en la lista de favoritos
    }

    fun toggleFavorite(bookId: String) {
        if (favoriteBooks.contains(bookId)) {
            favoriteBooks.remove(bookId) // Eliminar de favoritos
        } else {
            favoriteBooks.add(bookId) // Agregar a favoritos
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BookshelfApplication)
                val bookshelfRepository = application.container.bookshelfRepository
                DetailsViewModel(bookshelfRepository = bookshelfRepository)
            }
        }
    }
}