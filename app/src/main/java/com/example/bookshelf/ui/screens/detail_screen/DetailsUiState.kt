@file:Suppress(
    "UNUSED_PARAMETER", "UNREACHABLE_CODE", "RemoveRedundantQualifierName",
    "KotlinRedundantDiagnosticSuppress", "unused", "FunctionName"
)

package com.example.bookshelf.ui.screens.detail_screen

import com.example.bookshelf.model.Book

sealed interface DetailsUiState {

    object Error : DetailsUiState
    object Loading : DetailsUiState
    data class Success(val book: Book) : DetailsUiState

    companion object {
        // Este método acepta dos parámetros y devuelve una instancia de Success
        fun Success(book: Book, bookItem: Book): DetailsUiState.Success {
            return DetailsUiState.Success(book)
        }

        // Corregido: Este método ahora devuelve el objeto Error
        fun Error(s: String): DetailsUiState {
            return DetailsUiState.Error // Devuelve el objeto Error
        }
    }
}