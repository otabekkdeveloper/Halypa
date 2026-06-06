package com.example.arkadagapp.model

import java.io.Serializable

data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val year: String,
    val coverImage: Int,
    val translations: List<BookTranslation>
) : Serializable

data class BookTranslation(
    val language: String,
    val translator: String? = null,
    val coverImage: Int,
    val volumes: List<Volume>? = null,
    val pdfPath: String? = null,
    var pageCount: Int = 0
) : Serializable

data class Volume(
    val number: Int,
    val title: String,
    val coverImage: Int,
    val pdfPath: String,
    var pageCount: Int = 0
) : Serializable