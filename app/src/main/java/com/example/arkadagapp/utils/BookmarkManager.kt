package com.example.arkadagapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.arkadagapp.R
import com.example.arkadagapp.model.BookProgress

class BookmarkManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("bookmarks", Context.MODE_PRIVATE)

    // Sohranit' progress
    fun saveProgress(bookId: Int, currentPage: Int, totalPages: Int) {
        prefs.edit().apply {
            putInt("book_${bookId}_page", currentPage)
            putInt("book_${bookId}_total", totalPages)

            // ✅ vremya poslednego chteniya
            putLong("book_${bookId}_last_read", System.currentTimeMillis())

            apply()
        }
    }

    // Sohranit' info knigi
    fun saveBookInfo(bookId: Int, title: String, coverImage: Int, pdfPath: String) {
        prefs.edit().apply {
            putString("book_${bookId}_title", title)
            putInt("book_${bookId}_cover", coverImage)
            putString("book_${bookId}_pdf", pdfPath)
            apply()
        }
    }

    // Poluchit' sohranennuyu stranitsu
    fun getSavedPage(bookId: Int): Int {
        return prefs.getInt("book_${bookId}_page", 0)
    }

    // Poluchit' protsent
    fun getProgress(bookId: Int): Int {
        val currentPage = prefs.getInt("book_${bookId}_page", 0)
        val totalPages = prefs.getInt("book_${bookId}_total", 1)
        return if (totalPages > 0) {
            ((currentPage.toFloat() / totalPages) * 100).toInt()
        } else 0
    }

    // Poluchit' nazvanie knigi
    fun getBookTitle(bookId: Int): String {
        return prefs.getString("book_${bookId}_title", "Kitap #$bookId") ?: "Kitap #$bookId"
    }

    // Poluchit' cover knigi
    fun getBookCover(bookId: Int): Int {
        return prefs.getInt("book_${bookId}_cover", R.drawable.placeholder)
    }

    // Poluchit' PDF path
    fun getBookPdfPath(bookId: Int): String {
        return prefs.getString("book_${bookId}_pdf", "") ?: ""
    }

    // Poluchit' vse knigi s progressom
    fun getAllBooksProgress(): List<BookProgress> {
        val allKeys = prefs.all.keys
        val bookIds = allKeys
            .filter { it.contains("_page") }
            .map { it.replace("book_", "").replace("_page", "").toIntOrNull() }
            .filterNotNull()
            .distinct()

        return bookIds.map { bookId ->
            val currentPage = getSavedPage(bookId)
            val lastReadTime = prefs.getLong("book_${bookId}_last_read", 0)
            val totalPages = prefs.getInt("book_${bookId}_total", 0)
            BookProgress(
                bookId = bookId,
                title = getBookTitle(bookId),
                coverImage = getBookCover(bookId),
                pdfPath = getBookPdfPath(bookId),
                currentPage = currentPage,
                totalPages = totalPages,
                progress = getProgress(bookId),
                lastReadTime = lastReadTime
            )
        }
    }
}

