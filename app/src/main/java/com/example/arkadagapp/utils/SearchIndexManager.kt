package com.example.arkadagapp.utils

import android.content.Context
import android.util.Log
import com.example.arkadagapp.R
import com.example.arkadagapp.model.BookForPicker
import com.example.arkadagapp.model.SearchResult
import com.example.arkadagapp.repository.BookRepository
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object SearchIndexManager {

    private const val TAG = "SearchIndexManager"
    private const val INDEX_FILE = "search_index_cache.json"
    private const val SNIPPET_LENGTH = 200

    // Индекс в памяти: bookId -> список (страница, текст)
    private val index = mutableMapOf<Int, List<PageEntry>>()

    // Состояние инициализации
    @Volatile private var isReady = false
    @Volatile private var isBuilding = false

    data class PageEntry(val page: Int, val text: String)

    // ──────────────────────────────────────────────
    // Инициализация — вызывать при старте приложения
    // ──────────────────────────────────────────────

    suspend fun initialize(context: Context) {
        if (isReady) return
        if (isBuilding) {
            // Уже строится — ждём
            while (isBuilding) {
                kotlinx.coroutines.delay(100)
            }
            return
        }

        isBuilding = true
        withContext(Dispatchers.IO) {
            try {
                PDFBoxResourceLoader.init(context)
                val cacheFile = File(context.cacheDir, INDEX_FILE)
                if (cacheFile.exists()) {
                    Log.d(TAG, "📂 Загружаем из кэша")
                    loadFromCache(cacheFile)
                } else {
                    Log.d(TAG, "🔨 Строим индекс из PDF...")
                    buildIndexFromPdfs(context)
                    saveToCache(cacheFile)
                }
                isReady = true
                Log.d(TAG, "✅ Индекс готов: ${index.size} книг")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Ошибка инициализации: ${e.message}")
            } finally {
                isBuilding = false
            }
        }
    }

    fun isIndexReady() = isReady
    fun isIndexBuilding() = isBuilding

    suspend fun rebuildIndex(context: Context) {
        index.clear()
        isReady = false
        isBuilding = false
        File(context.cacheDir, INDEX_FILE).delete()
        initialize(context)
    }

    // ──────────────────────────────────────────────
    // Построение индекса из PDF
    // ──────────────────────────────────────────────

    private fun buildIndexFromPdfs(context: Context) {
        val books = BookRepository.getAllBooks(context)
        books.forEach { book ->
            try {
                val pages = extractPagesFromPdf(context, book.pdfPath)
                if (pages.isNotEmpty()) {
                    index[book.id] = pages
                    Log.d(TAG, "📖 ${book.title}: ${pages.size} стр.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ ${book.pdfPath}: ${e.message}")
            }
        }
    }

    private fun extractPagesFromPdf(context: Context, pdfPath: String): List<PageEntry> {
        val pages = mutableListOf<PageEntry>()
        try {
            val inputStream = context.assets.open(pdfPath)
            val document = PDDocument.load(inputStream)
            val stripper = PDFTextStripper()
            val totalPages = document.numberOfPages

            for (pageNum in 1..totalPages) {
                stripper.startPage = pageNum
                stripper.endPage = pageNum
                val text = stripper.getText(document).trim()
                if (text.isNotBlank()) {
                    pages.add(PageEntry(page = pageNum, text = text))
                }
            }
            document.close()
            inputStream.close()
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка PDF $pdfPath: ${e.message}")
        }
        return pages
    }

    // ──────────────────────────────────────────────
    // Поиск
    // ──────────────────────────────────────────────

    fun search(query: String): List<SearchResult> {
        if (!isReady || query.length < 2) return emptyList()
        return searchInternal(query, bookId = null)
    }

    fun searchInBook(query: String, bookId: Int): List<SearchResult> {
        if (!isReady || query.length < 2) return emptyList()
        return searchInternal(query, bookId = bookId)
    }

    private fun searchInternal(query: String, bookId: Int?): List<SearchResult> {
        val results = mutableListOf<SearchResult>()
        val lowerQuery = query.lowercase().trim()

        val booksToSearch = if (bookId != null) {
            index.filterKeys { it == bookId }
        } else {
            index
        }

        booksToSearch.forEach { (id, pages) ->
            pages.forEach { entry ->
                if (entry.text.contains(lowerQuery, ignoreCase = true)) {
                    val bookMeta = BookRepository.getStaticBookById(id)
                        ?: BookForPicker(id, "Книга $id", R.drawable.placeholder, "")
                    results.add(
                        SearchResult(
                            bookId = id,
                            bookTitle = bookMeta.title,
                            bookCover = bookMeta.coverResId,
                            snippet = extractSnippet(entry.text, lowerQuery),
                            pageNumber = entry.page,
                            pdfPath = bookMeta.pdfPath,
                            highlightedText = extractSnippet(entry.text, lowerQuery)
                        )
                    )
                }
            }
        }

        Log.d(TAG, "🔍 '$query': ${results.size} рез.")
        return results.sortedBy { it.pageNumber }
    }

    private fun extractSnippet(text: String, query: String): String {
        val lowerText = text.lowercase()
        val idx = lowerText.indexOf(query)
        if (idx == -1) return text.take(SNIPPET_LENGTH)
        val start = maxOf(0, idx - 80)
        val end = minOf(text.length, idx + query.length + 120)
        val snippet = text.substring(start, end).trim()
        return if (start > 0) "...$snippet" else snippet
    }

    // ──────────────────────────────────────────────
    // Кэш
    // ──────────────────────────────────────────────

    private fun saveToCache(file: File) {
        try {
            val root = JSONObject()
            val booksArray = JSONArray()
            index.forEach { (bookId, pages) ->
                val bookObj = JSONObject()
                bookObj.put("bookId", bookId)
                val pagesArray = JSONArray()
                pages.forEach { entry ->
                    val pageObj = JSONObject()
                    pageObj.put("page", entry.page)
                    pageObj.put("text", entry.text)
                    pagesArray.put(pageObj)
                }
                bookObj.put("pages", pagesArray)
                booksArray.put(bookObj)
            }
            root.put("books", booksArray)
            file.writeText(root.toString())
            Log.d(TAG, "💾 Кэш сохранён: ${file.length() / 1024}KB")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка кэша: ${e.message}")
        }
    }

    private fun loadFromCache(file: File) {
        try {
            val root = JSONObject(file.readText())
            val booksArray = root.getJSONArray("books")
            for (i in 0 until booksArray.length()) {
                val bookObj = booksArray.getJSONObject(i)
                val bookId = bookObj.getInt("bookId")
                val pagesArray = bookObj.getJSONArray("pages")
                val pages = mutableListOf<PageEntry>()
                for (j in 0 until pagesArray.length()) {
                    val pageObj = pagesArray.getJSONObject(j)
                    pages.add(PageEntry(page = pageObj.getInt("page"), text = pageObj.getString("text")))
                }
                index[bookId] = pages
            }
            Log.d(TAG, "📂 Кэш: ${index.size} книг")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка загрузки кэша: ${e.message}")
            file.delete()
        }
    }
}