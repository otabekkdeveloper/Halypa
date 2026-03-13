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
import com.example.arkadagapp.utils.LikeManager
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.google.android.material.bottomnavigation.BottomNavigationView

class PdfReaderFragment : Fragment(), OnPageChangeListener {

    private lateinit var pdfView: PDFView
    private lateinit var pageNumber: TextView
    private lateinit var likeButton: ImageButton
    private lateinit var bookmarkManager: BookmarkManager
    private lateinit var likeManager: LikeManager

    private var pdfPath: String = ""
    private var bookTitle: String = ""
    private var bookId: Int = 0
    private var bookCoverImage: Int = 0
    private var currentPage: Int = 0
    private var totalPages: Int = 0

    companion object {
        private const val ARG_PDF_PATH = "pdf_path"
        private const val ARG_BOOK_TITLE = "book_title"
        private const val ARG_BOOK_ID = "book_id"
        private const val ARG_BOOK_COVER = "book_cover"
        private const val ARG_START_PAGE = "start_page"

        fun newInstance(pdfPath: String, bookTitle: String, bookId: Int, bookCover: Int, startPage: Int = 0): PdfReaderFragment {
            val fragment = PdfReaderFragment()
            val args = Bundle()
            args.putString(ARG_PDF_PATH, pdfPath)
            args.putString(ARG_BOOK_TITLE, bookTitle)
            args.putInt(ARG_BOOK_ID, bookId)
            args.putInt(ARG_BOOK_COVER, bookCover)
            args.putInt(ARG_START_PAGE, startPage)
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
        currentPage = arguments?.getInt(ARG_START_PAGE) ?: 0
        bookmarkManager = BookmarkManager(requireContext())
        likeManager = LikeManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pdf_reader, container, false)

        pdfView = view.findViewById(R.id.pdf_view)
        pageNumber = view.findViewById(R.id.page_number)
        likeButton = view.findViewById(R.id.like_button)

        view.findViewById<TextView>(R.id.book_title).text = bookTitle

        // Back button
        view.findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            saveCurrentProgress()
            parentFragmentManager.popBackStack()
        }

        // Like button
        updateLikeIcon()
        likeButton.setOnClickListener {
            toggleLike()
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
        saveCurrentProgress()
    }

    private fun saveCurrentProgress() {
        bookmarkManager.saveProgress(bookId, currentPage, totalPages)
    }

    private fun toggleLike() {
        likeManager.toggleLike(bookId)
        updateLikeIcon()

        val message = if (likeManager.isLiked(bookId)) {
            "Halan kitaplara goşuldy ❤️"
        } else {
            "Halan kitaplardan aýryldy"
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun updateLikeIcon() {
        val icon = if (likeManager.isLiked(bookId)) {
            R.drawable.ic_like_bold
        } else {
            R.drawable.ic_like
        }
        likeButton.setImageResource(icon)
    }

    override fun onPause() {
        super.onPause()
        saveCurrentProgress()
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.GONE
        updateLikeIcon()
    }

    override fun onDestroy() {
        super.onDestroy()
        saveCurrentProgress()
    }
}