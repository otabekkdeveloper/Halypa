package com.example.arkadagapp.model

data class Quote(
    val id: Int,
    val text: String,
    val source: String,
    var isFavorite: Boolean = false
)