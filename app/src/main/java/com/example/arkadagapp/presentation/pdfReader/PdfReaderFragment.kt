package com.example.arkadagapp.presentation.pdfReader

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.example.arkadagapp.R
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.google.android.material.bottomnavigation.BottomNavigationView

class PdfReaderFragment : Fragment(), OnPageChangeListener {

    private lateinit var pdfView: PDFView
    private lateinit var pageNumber: TextView
    private var pdfPath: String = ""
    private var bookTitle: String = ""
    private var currentPage: Int = 0

    companion object {
        private const val ARG_PDF_PATH = "pdf_path"
        private const val ARG_BOOK_TITLE = "book_title"

        fun newInstance(pdfPath: String, bookTitle: String): PdfReaderFragment {
            val fragment = PdfReaderFragment()
            val args = Bundle()
            args.putString(ARG_PDF_PATH, pdfPath)
            args.putString(ARG_BOOK_TITLE, bookTitle)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pdfPath = arguments?.getString(ARG_PDF_PATH) ?: ""
        bookTitle = arguments?.getString(ARG_BOOK_TITLE) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pdf_reader, container, false)

        pdfView = view.findViewById(R.id.pdf_view)
        pageNumber = view.findViewById(R.id.page_number)

        view.findViewById<TextView>(R.id.book_title).text = bookTitle

        // Back button
        view.findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Bookmark button (TODO: realize save position)
        view.findViewById<ImageButton>(R.id.bookmark_button).setOnClickListener {
            // Sohranit' tekushchuyu stranitsu
        }

        // Load PDF iz assets
        loadPdfFromAssets()

        return view
    }

    override fun onResume() {
        super.onResume()
        // SKRYT' Bottom Navigation
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        // POKAZAT' Bottom Navigation obratno
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.VISIBLE
    }

    private fun loadPdfFromAssets() {
        try {
            pdfView.fromAsset(pdfPath)
                .defaultPage(currentPage)
                .onPageChange(this)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .spacing(10)
                .nightMode(false)
                .load()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPageChanged(page: Int, pageCount: Int) {
        currentPage = page
        pageNumber.text = "${page + 1}"
    }
}