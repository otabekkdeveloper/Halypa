package com.example.arkadagapp.application

import android.app.Application
import com.example.arkadagapp.utils.QuotesManager
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        PDFBoxResourceLoader.init(this)
//        QuotesManager.init(this)
    }
}