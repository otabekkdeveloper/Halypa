package com.example.arkadagapp.presentation.pdfReader

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.arkadagapp.R
import com.example.arkadagapp.model.BookProgress
import com.example.arkadagapp.presentation.settings.PieChartView

class ReadingProgressAdapter(
    private val progressList: List<BookProgress>,
    private val onItemClick: (BookProgress) -> Unit
) : RecyclerView.Adapter<ReadingProgressAdapter.ProgressViewHolder>() {

    class ProgressViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bookCover: ImageView = view.findViewById(R.id.book_cover_small)
        val bookTitle: TextView = view.findViewById(R.id.book_title_progress)
        val bookPages: TextView = view.findViewById(R.id.book_pages_progress)
        val progressPercentage: TextView = view.findViewById(R.id.progress_percentage)
        val pieChart: PieChartView = view.findViewById(R.id.pie_chart)
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

        // Установить progress в pie chart
        holder.pieChart.setProgress(bookProgress.progress)

        // ИСПРАВЛЕНИЕ: Проверка и fallback на placeholder
        try {
            holder.bookCover.setImageResource(bookProgress.coverImage)
        } catch (e: Exception) {
            // Если ошибка - ставим placeholder
            holder.bookCover.setImageResource(R.drawable.placeholder)
        }

        holder.itemView.setOnClickListener {
            onItemClick(bookProgress)
        }
    }

    override fun getItemCount() = progressList.size
}