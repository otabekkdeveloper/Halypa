package com.example.arkadagapp.presentation.quote.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.arkadagapp.R
import com.example.arkadagapp.model.Quote
import com.example.arkadagapp.utils.QuotesManager

class QuotesAdapter(
    private var quotes: List<Quote>,
    private val onFavoriteChanged: () -> Unit = {}
) : RecyclerView.Adapter<QuotesAdapter.QuoteViewHolder>() {

    class QuoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val quoteText: TextView = view.findViewById(R.id.quote_text)
        val quoteSource: TextView = view.findViewById(R.id.quote_source)
        val btnCopy: ImageView = view.findViewById(R.id.btn_copy)
        val btnLike: ImageView = view.findViewById(R.id.btn_like)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quote, parent, false)
        return QuoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        val quote = quotes[position]

        holder.quoteText.text = quote.text
        holder.quoteSource.text = quote.source

        // Обновить иконку лайка
        updateLikeIcon(holder.btnLike, quote.isFavorite)

        // Copy Button
        holder.btnCopy.setOnClickListener {
            copyToClipboard(holder.itemView.context, quote)
        }

        // Like Button
        holder.btnLike.setOnClickListener {
            QuotesManager.toggleFavorite(quote.id)
            updateLikeIcon(holder.btnLike, quote.isFavorite)
            onFavoriteChanged()

            val message = if (quote.isFavorite) {
                "Halanlarymda goşuldy"
            } else {
                "Halanlarymdan aýryldy"
            }
            Toast.makeText(holder.itemView.context, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount() = quotes.size

    fun updateQuotes(newQuotes: List<Quote>) {
        quotes = newQuotes
        notifyDataSetChanged()
    }

    private fun updateLikeIcon(btnLike: ImageView, isFavorite: Boolean) {
        if (isFavorite) {
            btnLike.setImageResource(R.drawable.ic_heart_filled)
            btnLike.setColorFilter(0xFF3B82F6.toInt()) // Blue
        } else {
            btnLike.setImageResource(R.drawable.ic_heart_outline)
            btnLike.setColorFilter(0xFF71717A.toInt()) // Gray
        }
    }

    private fun copyToClipboard(context: Context, quote: Quote) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Quote", "${quote.text}\n— ${quote.source}")
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Göçürildi", Toast.LENGTH_SHORT).show()
    }
}