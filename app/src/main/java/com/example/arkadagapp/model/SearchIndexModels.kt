package com.example.arkadagapp.model

data class SearchIndex(
    val index: List<BookIndex>
)

data class BookIndex(
    val bookId: Int,
    val bookTitle: String,
    val translation: String,
    val pdfPath: String,
    val coverImage: String,
    val entries: List<TextEntry>
)

data class TextEntry(
    val page: Int,
    val title: String,
    val text: String,
    val keywords: List<String>
)