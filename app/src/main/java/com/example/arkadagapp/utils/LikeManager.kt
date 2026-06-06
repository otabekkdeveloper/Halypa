package com.example.arkadagapp.utils

import android.content.Context
import android.content.SharedPreferences

class LikeManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("likes", Context.MODE_PRIVATE)

    fun toggleLike(bookId: Int) {
        val isLiked = isLiked(bookId)
        prefs.edit().putBoolean("book_${bookId}_liked", !isLiked).apply()
    }

    fun isLiked(bookId: Int): Boolean {
        return prefs.getBoolean("book_${bookId}_liked", false)
    }

    fun getLikedCount(): Int {
        return prefs.all.keys
            .filter { it.endsWith("_liked") && prefs.getBoolean(it, false) }
            .size
    }
}