package com.example.arkadagapp.utils

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {

    private const val SELECTED_LANGUAGE = "selected_language"

    // Сохранить выбранный язык
    fun setLocale(context: Context, languageCode: String) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString(SELECTED_LANGUAGE, languageCode).apply()

        updateResources(context, languageCode)
    }

    // Получить сохраненный язык
    fun getLocale(context: Context): String {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return prefs.getString(SELECTED_LANGUAGE, "tk") ?: "tk" // по умолчанию туркменский
    }

    // Применить язык
    fun updateResources(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    // Получить язык для отображения
    fun getLanguageName(languageCode: String): String {
        return when (languageCode) {
            "tk" -> "Türkmen"
            "ru" -> "Русский"
            "en" -> "English"
            else -> "Türkmen"
        }
    }
}