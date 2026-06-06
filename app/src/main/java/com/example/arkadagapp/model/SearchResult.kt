package com.example.arkadagapp.model

data class SearchResult(
    val bookId: Int,
    val bookTitle: String,
    val bookCover: Int,
    val snippet: String,
    val pageNumber: Int,
    val pdfPath: String,
    val highlightedText: String
)