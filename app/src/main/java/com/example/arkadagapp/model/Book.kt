package com.example.arkadagapp.model

import java.io.Serializable

// OSNOVNAYA KNIGA (bez yazykovoy informatsii)
data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val year: String,
    val coverImage: Int, // Oblozhka glavnoy knigi
    val translations: List<BookTranslation> // Perevody s tomami
) : Serializable

// PEREVOD KNIGI (kazhdyy yazyk imeet svoi toma i cover)
data class BookTranslation(
    val language: String,           // "Türkmen", "Русский", "English"
    val translator: String? = null, // Perevodchik (esli est')
    val coverImage: Int, // Cover dlya etogo yazyka
//    val pages: String,
    val volumes: List<Volume>? = null, // Toma dlya etogo yazyka
    val pdfPath: String? = null,     // PDF (esli net tomov)
    var pageCount: Int = 0
) : Serializable

// TOM
data class Volume(
    val number: Int,      // Nomer toma
    val title: String,    // "Tom 1"
//    val pages: String,    // Kolichestvo stranits
    val coverImage: Int,  // Cover toma
    val pdfPath: String,
    var pageCount: Int = 0
) : Serializable