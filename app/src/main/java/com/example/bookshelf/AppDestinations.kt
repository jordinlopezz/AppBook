package com.example.bookshelf

enum class AppDestinations(val title: String) {
    MenuScreen(title = "Menu"),
    QueryScreen(title = "Bookshelf"),
    FavoriteScreen(title = "My Favorite Books"),
    DetailScreen(title = "Book Details") // Cambié a "Book Details" para que sea más descriptivo
}