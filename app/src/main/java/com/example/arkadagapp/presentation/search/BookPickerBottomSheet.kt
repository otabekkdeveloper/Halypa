package com.example.arkadagapp.presentation.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.arkadagapp.R
import com.example.arkadagapp.model.BookForPicker
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BookPickerBottomSheet : BottomSheetDialogFragment() {

    private var books: List<BookForPicker> = emptyList()
    private var selectedBookId: Int? = null
    private var onBookSelected: ((BookForPicker) -> Unit)? = null

    companion object {
        fun newInstance(
            books: List<BookForPicker>,
            selectedBookId: Int?,
            onBookSelected: (BookForPicker) -> Unit
        ): BookPickerBottomSheet {
            return BookPickerBottomSheet().apply {
                this.books = books
                this.selectedBookId = selectedBookId
                this.onBookSelected = onBookSelected
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottom_sheet_book_picker, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.books_recycler)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = BookPickerAdapter(books, selectedBookId) { book ->
            onBookSelected?.invoke(book)
            dismiss()
        }
    }
}