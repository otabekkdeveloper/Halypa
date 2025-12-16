package com.example.arkadagapp.utils

import android.content.Context

object ThemePrefs {

    private const val PREFS = "theme_prefs"
    private const val KEY_THEME = "theme_mode"

    fun save(context: Context, mode: Int) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_THEME, mode)
            .apply()
    }

    fun load(context: Context): Int {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getInt(KEY_THEME, ThemeManager.MODE_SYSTEM)
    }
}
