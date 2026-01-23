package com.example.arkadagapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WrongQuestion(
    val questionNumber: Int,
    val question: Question,
    val userAnswerIndex: Int
) : Parcelable