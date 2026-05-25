package com.example.arkadagapp.model

data class BookProgress(
    val bookId: Int,
    val title: String,
    val coverImage: Int,
    val pdfPath: String,
    val currentPage: Int,
    val totalPages: Int,
    val progress: Int,
    val lastReadTime: Long = 0
)