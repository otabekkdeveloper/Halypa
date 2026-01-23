package com.example.arkadagapp.utils

import android.content.Context
import com.example.arkadagapp.R
import com.example.arkadagapp.model.*
import com.google.gson.Gson
import java.io.InputStreamReader

object SearchIndexManager {

    private var searchIndex: SearchIndex? = null

    /**
     * Загружает JSON файл из assets/data/search_index.json
     * Вызывается ОДИН РАЗ при первом поиске
     */
    fun loadIndex(context: Context) {
        if (searchIndex != null) return // Уже загружен

        try {
            // Читаем JSON из assets
            val inputStream = context.assets.open("data/search_index.json")
            val reader = InputStreamReader(inputStream)

            // Парсим JSON в объект
            searchIndex = Gson().fromJson(reader, SearchIndex::class.java)

            reader.close()
            inputStream.close()

            println("✅ Индекс загружен: ${searchIndex?.index?.size} книг")
        } catch (e: Exception) {
            e.printStackTrace()
            println("❌ Ошибка загрузки индекса: ${e.message}")
        }
    }

    /**
     * Ищет текст по всем книгам
     * Возвращает список результатов
     */
    fun search(query: String): List<SearchResult> {
        if (searchIndex == null || query.length < 2) {
            return emptyList()
        }

        val results = mutableListOf<SearchResult>()
        val lowerQuery = query.lowercase()

        // Проходим по всем книгам
        searchIndex!!.index.forEach { bookIndex ->

            // Проходим по всем записям в книге
            bookIndex.entries.forEach { entry ->

                // Ищем в keywords (БЫСТРО)
                val foundInKeywords = entry.keywords.any { keyword ->
                    keyword.contains(lowerQuery, ignoreCase = true)
                }

                // Ищем в тексте (если не нашли в keywords)
                val foundInText = entry.text.contains(query, ignoreCase = true)

                // Если нашли - добавляем результат
                if (foundInKeywords || foundInText) {

                    val coverResId = getCoverResourceId(bookIndex.coverImage)

                    results.add(SearchResult(
                        bookId = bookIndex.bookId,
                        bookTitle = bookIndex.bookTitle,
                        bookCover = coverResId,
                        snippet = entry.title, // "Sahypa 5"
                        pageNumber = entry.page,
                        pdfPath = bookIndex.pdfPath,
                        highlightedText = entry.text // Текст отрывка
                    ))
                }
            }
        }

        println("🔍 Найдено: ${results.size} результатов для '$query'")
        return results
    }

    /**
     * Конвертирует название картинки в R.drawable.xxx
     */
    private fun getCoverResourceId(imageName: String): Int {
        return when(imageName) {
            "banner_1" -> R.drawable.banner_1
            "banner_2" -> R.drawable.banner_2
            "banner_3" -> R.drawable.banner_3
            "banner_4" -> R.drawable.banner_4
            "four_4_1" -> R.drawable.four_4_1
            "four_4_2" -> R.drawable.four_4_2
            // Добавь остальные картинки...
            else -> R.drawable.banner_1 // По умолчанию
        }
    }
}