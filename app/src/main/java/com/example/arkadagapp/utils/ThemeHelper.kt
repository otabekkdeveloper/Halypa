package com.example.arkadagapp.utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.arkadagapp.R

object ThemeHelper {

    private const val THEME_PREF = "theme_pref"
    private const val THEME_KEY = "theme"

    const val THEME_DARK = "dark"
    const val THEME_LIGHT = "light"

    // Сохранить тему
    fun setTheme(context: Context, theme: String) {
        val prefs = context.getSharedPreferences(THEME_PREF, Context.MODE_PRIVATE)
        prefs.edit().putString(THEME_KEY, theme).apply()

        // Применить режим
        applyThemeMode(theme)
    }

    // Получить сохраненную тему
    fun getTheme(context: Context): String {
        val prefs = context.getSharedPreferences(THEME_PREF, Context.MODE_PRIVATE)
        return prefs.getString(THEME_KEY, THEME_DARK) ?: THEME_DARK
    }

    // Применить тему
    fun applyTheme(context: Context) {
        val theme = getTheme(context)
        applyThemeMode(theme)
    }

    private fun applyThemeMode(theme: String) {
        when (theme) {
            THEME_LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            THEME_DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    // Проверить темную ли тема
    fun isDarkTheme(context: Context): Boolean {
        return getTheme(context) == THEME_DARK
    }
}