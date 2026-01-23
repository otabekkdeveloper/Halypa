package com.example.arkadagapp.model

data class SearchResult(
    val bookId: Int,
    val bookTitle: String,
    val bookCover: Int,
    val snippet: String,        // Otryvok teksta gde nashli slovo
    val pageNumber: Int,         // Stranitsa gde nashli
    val pdfPath: String,
    val highlightedText: String  // Tekst s vydelennym slovom
)