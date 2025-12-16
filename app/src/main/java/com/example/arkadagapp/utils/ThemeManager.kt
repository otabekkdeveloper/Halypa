package com.example.arkadagapp.utils

import androidx.appcompat.app.AppCompatDelegate

object ThemeManager {

    const val MODE_LIGHT = 0
    const val MODE_DARK = 1
    const val MODE_SYSTEM = 2

    fun applyTheme(mode: Int) {
        when (mode) {
            MODE_LIGHT ->
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
                )

            MODE_DARK ->
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
                )

            MODE_SYSTEM ->
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                )
        }
    }
}
