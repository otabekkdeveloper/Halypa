package com.example.arkadagapp.utils

import android.content.Context
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import java.io.File
import java.io.FileOutputStream

object PdfUtils {

    fun getPdfPageCount(context: Context, assetPath: String): Int {
        return try {
            val file = File(context.cacheDir, "temp.pdf")

            context.assets.open(assetPath).use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }

            val parcelFileDescriptor =
                ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)

            val pdfRenderer = PdfRenderer(parcelFileDescriptor)

            val pageCount = pdfRenderer.pageCount

            pdfRenderer.close()
            parcelFileDescriptor.close()

            pageCount
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }
}