package com.example.arkadagapp.application

import android.app.Application
import com.example.arkadagapp.utils.QuotesManager

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        QuotesManager.init(this)
    }
}