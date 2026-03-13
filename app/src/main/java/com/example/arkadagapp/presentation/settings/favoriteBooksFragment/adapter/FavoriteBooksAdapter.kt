package com.example.arkadagapp.presentation.settings.favoriteBooksFragment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.arkadagapp.R
import com.example.arkadagapp.model.BookProgress

class FavoriteBooksAdapter(
    private val books: List<BookProgress>,
    private val onBookClick: (BookProgress) -> Unit
) : RecyclerView.Adapter<FavoriteBooksAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cover: ImageView = view.findViewById(R.id.book_cover)
        val title: TextView = view.findViewById(R.id.book_title)
        val progress: TextView = view.findViewById(R.id.book_progress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite_book, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = books[position]
        holder.cover.setImageResource(book.coverImage)
        holder.title.text = book.title
        holder.progress.text = "${book.progress}%"
        holder.itemView.setOnClickListener { onBookClick(book) }
    }

    override fun getItemCount() = books.size
}