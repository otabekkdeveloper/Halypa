package com.example.arkadagapp.presentation.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.arkadagapp.R
import com.example.arkadagapp.model.Book

class BookDetailFragment : Fragment() {

    private lateinit var book: Book

    companion object {
        private const val ARG_BOOK = "book"

        fun newInstance(book: Book): BookDetailFragment {
            val fragment = BookDetailFragment()
            val args = Bundle()
            args.putSerializable(ARG_BOOK, book as java.io.Serializable)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        book = arguments?.getSerializable(ARG_BOOK) as Book
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_book_detail, container, false)

        // Back button
        view.findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Set book data
        view.findViewById<ImageView>(R.id.detail_book_cover).setImageResource(book.coverImage)
        view.findViewById<TextView>(R.id.detail_book_title).text = book.title
        view.findViewById<TextView>(R.id.detail_language).text = book.language
        view.findViewById<TextView>(R.id.detail_author).text = book.author
        view.findViewById<TextView>(R.id.detail_year).text = book.year
        view.findViewById<TextView>(R.id.detail_pages).text = book.pages

        // Read button
        view.findViewById<Button>(R.id.read_button).setOnClickListener {
            openPdfReader(book.pdfPath)
        }

        return view
    }

    private fun openPdfReader(pdfPath: String?) {
        // Otkroet PDF reader fragment
        // Realizuyte PDFReaderFragment
    }
}
