package com.example.arkadagapp.model


// Лёгкая модель специально для поп-меню выбора книги
// Не трогает твою основную модель Book
data class BookForPicker(
    val id: Int,
    val title: String,
    val coverResId: Int,
    val pdfPath: String        // первый / единственный том (для поиска)
)

