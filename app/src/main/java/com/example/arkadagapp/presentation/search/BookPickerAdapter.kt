package com.example.arkadagapp.presentation.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.arkadagapp.R
import com.example.arkadagapp.model.BookForPicker

class BookPickerAdapter(
    private val books: List<BookForPicker>,
    private val selectedBookId: Int?,
    private val onBookClick: (BookForPicker) -> Unit
) : RecyclerView.Adapter<BookPickerAdapter.BookViewHolder>() {

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cover: ImageView     = itemView.findViewById(R.id.book_cover_image)
        val title: TextView      = itemView.findViewById(R.id.book_title)
        val checkIcon: ImageView = itemView.findViewById(R.id.check_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book_picker, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]

        holder.title.text = book.title
        holder.cover.setImageResource(book.coverResId)

        val isSelected = book.id == selectedBookId
        holder.checkIcon.visibility = if (isSelected) View.VISIBLE else View.GONE
        holder.itemView.isSelected = isSelected

        holder.itemView.setOnClickListener { onBookClick(book) }
    }

    override fun getItemCount() = books.size
}