package com.example.arkadagapp.presentation.search


import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.arkadagapp.R
import com.example.arkadagapp.model.SearchResult

class SearchResultsAdapter(
    private val results: List<SearchResult>,
    private val searchQuery: String,
    private val onItemClick: (SearchResult) -> Unit
) : RecyclerView.Adapter<SearchResultsAdapter.ResultViewHolder>() {

    class ResultViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bookCover: ImageView = view.findViewById(R.id.result_book_cover)
        val bookTitle: TextView = view.findViewById(R.id.result_book_title)
        val subtitle: TextView = view.findViewById(R.id.result_subtitle)
        val snippet: TextView = view.findViewById(R.id.result_snippet)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_result, parent, false)
        return ResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val result = results[position]

        holder.bookCover.setImageResource(result.bookCover)
        holder.bookTitle.text = result.snippet // Nazvanie otryvka
        holder.subtitle.text = result.bookTitle // Nazvanie knigi

        // Vydelit' iskannoe slovo v snippete
        val highlightedSnippet = highlightText(result.highlightedText, searchQuery)
        holder.snippet.text = highlightedSnippet

        holder.itemView.setOnClickListener {
            onItemClick(result)
        }
    }

    override fun getItemCount() = results.size

    // Vydelit' tekst zhёltym fonom
    private fun highlightText(text: String, query: String): SpannableString {
        val spannable = SpannableString(text)

        if (query.isEmpty()) return spannable

        val lowerText = text.lowercase()
        val lowerQuery = query.lowercase()
        var startIndex = lowerText.indexOf(lowerQuery)

        while (startIndex >= 0) {
            val endIndex = startIndex + query.length

            // Zhёltyy fon
            spannable.setSpan(
                BackgroundColorSpan(0xFFFFEB3B.toInt()),
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // Bold
            spannable.setSpan(
                StyleSpan(Typeface.BOLD),
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            startIndex = lowerText.indexOf(lowerQuery, endIndex)
        }

        return spannable
    }
}