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

        PDFBoxResourceLoader.init(context)

        val results = mutableListOf<PdfSearchResult>()

        try {
            val inputStream: InputStream = context.assets.open(pdfPath)
            val document = PDDocument.load(inputStream)
            val stripper = PDFTextStripper()

            for (pageNum in 1..document.numberOfPages) {
                stripper.startPage = pageNum
                stripper.endPage = pageNum

                val pageText = stripper.getText(document)

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

    private fun extractSnippet(text: String, query: String, contextLength: Int = 100): String {
        val lowerText = text.lowercase()
        val lowerQuery = query.lowercase()

        val index = lowerText.indexOf(lowerQuery)
        if (index == -1) return ""

        val start = maxOf(0, index - contextLength)
        val end = minOf(text.length, index + query.length + contextLength)

        var snippet = text.substring(start, end).trim()

        if (start > 0) snippet = "...$snippet"
        if (end < text.length) snippet = "$snippet..."

        return snippet
    }
}