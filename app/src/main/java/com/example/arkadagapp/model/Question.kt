package com.example.arkadagapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Question(
    val id: Int,
    val category: String,
    val question: String,
    val answers: List<String>,
    val correctAnswerIndex: Int,
    val imageRes: Int? = null
) : Parcelable