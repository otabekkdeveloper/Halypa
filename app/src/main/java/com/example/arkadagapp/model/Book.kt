package com.example.arkadagapp.model

import java.io.Serializable

data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val year: String,
    val pages: String,
    val language: String,
    val coverImage: Int,
    val volumes: List<Volume>? = null, // ESLI EST' TOMA
    val translations: List<Translation>? = null, // ESLI EST' PEREVODY
    val pdfPath: String? = null // ESLI NET TOMOV - pryamoy PDF
) : Serializable

data class Volume(
    val number: Int,
    val title: String,
    val pages: String,
    val pdfPath: String
) : Serializable

data class Translation(
    val language: String,
    val translator: String? = null,
    val pdfPath: String
) : Serializable