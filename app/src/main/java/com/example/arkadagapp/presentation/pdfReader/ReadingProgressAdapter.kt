package com.example.arkadagapp.presentation.pdfReader

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.arkadagapp.R
import com.example.arkadagapp.model.BookProgress

class ReadingProgressAdapter(
    private val progressList: List<BookProgress>,
    private val onItemClick: (BookProgress) -> Unit
) : RecyclerView.Adapter<ReadingProgressAdapter.ProgressViewHolder>() {

    class ProgressViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bookCover: ImageView = view.findViewById(R.id.book_cover_small)
        val bookTitle: TextView = view.findViewById(R.id.book_title_progress)
        val bookPages: TextView = view.findViewById(R.id.book_pages_progress)
        val progressPercentage: TextView = view.findViewById(R.id.progress_percentage)
        val progressCircular: ProgressBar = view.findViewById(R.id.progress_circular)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgressViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reading_progress, parent, false)
        return ProgressViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProgressViewHolder, position: Int) {
        val bookProgress = progressList[position]

        holder.bookTitle.text = bookProgress.title
        holder.bookPages.text = "${bookProgress.currentPage + 1} / ${bookProgress.totalPages} sahypa"
        holder.progressPercentage.text = "${bookProgress.progress}%"
        holder.progressCircular.progress = bookProgress.progress
        holder.bookCover.setImageResource(bookProgress.coverImage)

        holder.itemView.setOnClickListener {
            onItemClick(bookProgress)
        }
    }

    override fun getItemCount() = progressList.size
}