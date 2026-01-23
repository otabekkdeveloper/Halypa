package com.example.arkadagapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.arkadagapp.model.Quote
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object QuotesManager {

    private const val PREFS_NAME = "quotes_prefs"
    private const val KEY_FAVORITES = "favorite_quotes"

    private lateinit var prefs: SharedPreferences
    private val gson = Gson()

    private val quotes = listOf(
        Quote(1, "Ozal akan ýerden akarmyş aryk", "Älem içre at gezer"),
        Quote(2, "Ýagşy niýet - ýarym döwlet", "Älem içre at gezer"),
        Quote(3, "Okaň, adam boluň!", "Ak şäherim Aşgabat"),
        Quote(4, "Bilim ulgamy zynjyr ýaly biri-birine sepleşip gitmeli", "Türkmenistanyň dermanlyk ösümlikleri II"),
        Quote(5, "Bir halkanyň üzülen ýerinde onuň zynjyrlygy galmayar", "Türkmenistanyň dermanlyk ösümlikleri II"),
        Quote(6, "Bilimli adam mydama baýdyr", "Türkmenistanyň dermanlyk ösümlikleri II"),
        Quote(7, "Adam eli gyzyl gül", "Älem içre at gezer"),
        Quote(8, "Dost dostuň aýnasydyr", "Älem içre at gezer"),
        Quote(9, "Watan öküze iým ýok", "Älem içre at gezer"),
        Quote(10, "Paýhas çeşmesi", "Älem içre at gezer")
    )

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadFavorites()
    }

    private fun loadFavorites() {
        val favoritesJson = prefs.getString(KEY_FAVORITES, null)
        if (favoritesJson != null) {
            val type = object : TypeToken<List<Int>>() {}.type
            val favoriteIds: List<Int> = gson.fromJson(favoritesJson, type)

            quotes.forEach { quote ->
                quote.isFavorite = favoriteIds.contains(quote.id)
            }
        }
    }

    private fun saveFavorites() {
        val favoriteIds = quotes.filter { it.isFavorite }.map { it.id }
        val json = gson.toJson(favoriteIds)
        prefs.edit().putString(KEY_FAVORITES, json).apply()
    }

    fun toggleFavorite(quoteId: Int) {
        val quote = quotes.find { it.id == quoteId }
        quote?.let {
            it.isFavorite = !it.isFavorite
            saveFavorites()
        }
    }

    fun getAllQuotes(): List<Quote> = quotes

    fun getFavoriteQuotes(): List<Quote> = quotes.filter { it.isFavorite }

    // Поиск ТОЛЬКО по тексту цитаты
    fun searchQuotes(query: String): List<Quote> {
        if (query.isEmpty()) return quotes
        return quotes.filter {
            it.text.contains(query, ignoreCase = true)  // ✅ ТОЛЬКО ТЕКСТ
        }
    }
}