package com.example.arkadagapp.utils

import android.content.Context
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import java.io.InputStream

data class PdfSearchResult(
    val pageNumber: Int,
    val snippet: String,
    val fullText: String
)

object PdfSearchHelper {

    fun searchInPdf(
        context: Context,
        pdfPath: String,
        query: String
    ): List<PdfSearchResult> {

        if (query.length < 2) return emptyList()

        // Инициализация PDFBox (только один раз)
        PDFBoxResourceLoader.init(context)

        val results = mutableListOf<PdfSearchResult>()

        try {
            val inputStream: InputStream = context.assets.open(pdfPath)
            val document = PDDocument.load(inputStream)
            val stripper = PDFTextStripper()

            // Проходим по каждой странице
            for (pageNum in 1..document.numberOfPages) {
                stripper.startPage = pageNum
                stripper.endPage = pageNum

                val pageText = stripper.getText(document)

                // Ищем запрос в тексте страницы
                if (pageText.contains(query, ignoreCase = true)) {
                    val snippet = extractSnippet(pageText, query)

                    results.add(PdfSearchResult(
                        pageNumber = pageNum,
                        snippet = snippet,
                        fullText = pageText
                    ))
                }
            }

            document.close()
            inputStream.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return results
    }

    // Извлекаем отрывок текста вокруг найденного слова
    private fun extractSnippet(text: String, query: String, contextLength: Int = 100): String {
        val lowerText = text.lowercase()
        val lowerQuery = query.lowercase()

        val index = lowerText.indexOf(lowerQuery)
        if (index == -1) return ""

        val start = maxOf(0, index - contextLength)
        val end = minOf(text.length, index + query.length + contextLength)

        var snippet = text.substring(start, end).trim()

        // Добавляем "..." если обрезали
        if (start > 0) snippet = "...$snippet"
        if (end < text.length) snippet = "$snippet..."

        return snippet
    }
}