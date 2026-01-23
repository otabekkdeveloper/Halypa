package com.example.arkadagapp.utils

import android.content.Context
import com.example.arkadagapp.model.Book
import com.example.arkadagapp.model.SearchResult
import com.example.arkadagapp.utils.PdfSearchHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SearchManager {
    private var allBooks = mutableListOf<Book>()

    fun setBooks(books: List<Book>) {
        allBooks.clear()
        allBooks.addAll(books)
    }

    suspend fun searchInBooks(context: Context, query: String): List<SearchResult> = withContext(Dispatchers.IO) {
        if (query.length < 2) return@withContext emptyList()

        val results = mutableListOf<SearchResult>()

        allBooks.forEach { book ->
            book.translations.forEach { translation ->

                // Поиск в PDF без томов
                if (translation.pdfPath != null) {
                    val pdfResults = PdfSearchHelper.searchInPdf(
                        context,
                        translation.pdfPath!!,
                        query
                    )

                    pdfResults.forEach { pdfResult ->
                        results.add(SearchResult(
                            bookId = book.id,
                            bookTitle = book.title,
                            bookCover = book.coverImage,
                            snippet = "${translation.language} - Сахыпа ${pdfResult.pageNumber}",
                            pageNumber = pdfResult.pageNumber,
                            pdfPath = translation.pdfPath!!,
                            highlightedText = pdfResult.snippet
                        ))
                    }
                }

                // Поиск в PDF с томами
                translation.volumes?.forEach { volume ->
                    val pdfResults = PdfSearchHelper.searchInPdf(
                        context,
                        volume.pdfPath,
                        query
                    )

                    pdfResults.forEach { pdfResult ->
                        results.add(SearchResult(
                            bookId = book.id,
                            bookTitle = "${book.title} - ${volume.title}",
                            bookCover = volume.coverImage ?: book.coverImage,
                            snippet = "${translation.language} - Сахыпа ${pdfResult.pageNumber}",
                            pageNumber = pdfResult.pageNumber,
                            pdfPath = volume.pdfPath,
                            highlightedText = pdfResult.snippet
                        ))
                    }
                }
            }
        }

        return@withContext results
    }
}