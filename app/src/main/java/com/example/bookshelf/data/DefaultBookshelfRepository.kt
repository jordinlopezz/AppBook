package com.example.bookshelf.data

import com.example.bookshelf.model.Book
import com.example.bookshelf.network.BookshelfApiService
import kotlinx.coroutines.delay

/**
 * Default Implementation of repository that retrieves volumes data from underlying data source.
 */
class DefaultBookshelfRepository(
    private val bookshelfApiService: BookshelfApiService
) : BookshelfRepository {

    override suspend fun getBooks(query: String): List<Book>? {
        return try {
            val res = bookshelfApiService.getBooks(query)
            if (res.isSuccessful) {
                res.body()?.items ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun getBook(id: String): Book? {
        var attempt = 0
        val maxAttempts = 3  // Número máximo de reintentos

        while (attempt < maxAttempts) {
            try {
                val res = bookshelfApiService.getBook(id)
                if (res.isSuccessful) {
                    println("Book fetched successfully: ${res.body()}")
                    return res.body()  // Retornar el libro
                } else {
                    println("Failed to fetch book, response code: ${res.code()}")
                    if (res.code() == 429) {
                        attempt++  // Incrementar el intento solo si falla
                        delay(2000) // Esperar 2 segundos antes de volver a intentar
                    } else {
                        return null  // Si no es un 429, retornar null
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return null  // Retornar null en caso de excepción
            }
        }
        println("Exceeded maximum attempts to fetch the book.")
        return null  // Retornar null si no se obtiene el libro después de los intentos
    }

}