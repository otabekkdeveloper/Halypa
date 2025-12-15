package com.example.arkadagapp.presentation.reader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.arkadagapp.R
import com.example.arkadagapp.utils.BookmarkManager
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.google.android.material.bottomnavigation.BottomNavigationView

class PdfReaderFragment : Fragment(), OnPageChangeListener {

    private lateinit var pdfView: PDFView
    private lateinit var pageNumber: TextView
    private lateinit var bookmarkButton: ImageButton
    private lateinit var bookmarkManager: BookmarkManager

    private var pdfPath: String = ""
    private var bookTitle: String = ""
    private var bookId: Int = 0
    private var bookCoverImage: Int = 0
    private var currentPage: Int = 0
    private var totalPages: Int = 0
    private var isBookmarked: Boolean = false

    companion object {
        private const val ARG_PDF_PATH = "pdf_path"
        private const val ARG_BOOK_TITLE = "book_title"
        private const val ARG_BOOK_ID = "book_id"
        private const val ARG_BOOK_COVER = "book_cover"

        fun newInstance(pdfPath: String, bookTitle: String, bookId: Int, bookCover: Int): PdfReaderFragment {
            val fragment = PdfReaderFragment()
            val args = Bundle()
            args.putString(ARG_PDF_PATH, pdfPath)
            args.putString(ARG_BOOK_TITLE, bookTitle)
            args.putInt(ARG_BOOK_ID, bookId)
            args.putInt(ARG_BOOK_COVER, bookCover)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pdfPath = arguments?.getString(ARG_PDF_PATH) ?: ""
        bookTitle = arguments?.getString(ARG_BOOK_TITLE) ?: ""
        bookId = arguments?.getInt(ARG_BOOK_ID) ?: 0
        bookCoverImage = arguments?.getInt(ARG_BOOK_COVER) ?: R.drawable.placeholder

        bookmarkManager = BookmarkManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pdf_reader, container, false)

        pdfView = view.findViewById(R.id.pdf_view)
        pageNumber = view.findViewById(R.id.page_number)
        bookmarkButton = view.findViewById(R.id.bookmark_button)

        view.findViewById<TextView>(R.id.book_title).text = bookTitle

        // Back button
        view.findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            saveCurrentProgress()
            parentFragmentManager.popBackStack()
        }

        // Bookmark button
        updateBookmarkIcon()
        bookmarkButton.setOnClickListener {
            toggleBookmark()
        }

        // Zagruzit' sohranennuyu stranitsu
        currentPage = bookmarkManager.getSavedPage(bookId)

        // Sohranit' info knigi
        bookmarkManager.saveBookInfo(bookId, bookTitle, bookCoverImage, pdfPath)

        loadPdfFromAssets()

        return view
    }

    private fun loadPdfFromAssets() {
        try {
            pdfView.fromAsset(pdfPath)
                .defaultPage(currentPage)
                .onPageChange(this)
                .onLoad { nbPages ->
                    totalPages = nbPages
                    // Obnovit' total pages
                    saveCurrentProgress()
                }
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .spacing(10)
                .nightMode(false)
                .load()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "PDF ýüklenip bilmedi", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPageChanged(page: Int, pageCount: Int) {
        currentPage = page
        totalPages = pageCount
        pageNumber.text = "${page + 1}"

        // Avtomaticheski sohranit' progress
        saveCurrentProgress()
        updateBookmarkIcon()
    }

    private fun saveCurrentProgress() {
        bookmarkManager.saveProgress(bookId, currentPage, totalPages)
    }

    private fun toggleBookmark() {
        isBookmarked = !isBookmarked
        updateBookmarkIcon()

        val progress = bookmarkManager.getProgress(bookId)
        val message = if (isBookmarked) {
            "Bellik goşuldy: $progress%"
        } else {
            "Bellik: $progress%"
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun updateBookmarkIcon() {
        val progress = bookmarkManager.getProgress(bookId)
        isBookmarked = progress > 0

        val icon = if (isBookmarked) {
            R.drawable.ic_bookmark_filled
        } else {
            R.drawable.ic_bookmark
        }
        bookmarkButton.setImageResource(icon)
    }

    override fun onPause() {
        super.onPause()
        saveCurrentProgress()
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        saveCurrentProgress()
    }
}