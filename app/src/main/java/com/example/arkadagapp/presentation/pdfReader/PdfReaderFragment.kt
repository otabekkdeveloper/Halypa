package com.example.arkadagapp.presentation.pdfReader

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.arkadagapp.R
import com.example.arkadagapp.utils.BookmarkManager
import com.example.arkadagapp.utils.LikeManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.net.URLEncoder

class PdfReaderFragment : Fragment() {

    private lateinit var webView: WebView

    private lateinit var pageNumber: TextView
    private lateinit var likeButton: ImageButton

    private lateinit var searchButton: ImageButton
    private lateinit var searchLayout: LinearLayout
    private lateinit var searchInput: EditText
    private lateinit var searchCount: TextView
    private lateinit var btnNext: ImageButton
    private lateinit var btnPrev: ImageButton

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

        fun newInstance(
            pdfPath: String,
            bookTitle: String,
            bookId: Int,
            bookCover: Int,
            startPage: Int = 0
        ): PdfReaderFragment {

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

        bookCoverImage =
            arguments?.getInt(ARG_BOOK_COVER)
                ?: R.drawable.placeholder

        currentPage =
            arguments?.getInt(ARG_START_PAGE) ?: 0

        bookmarkManager =
            BookmarkManager(requireContext())

        likeManager =
            LikeManager(requireContext())
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(
            R.layout.fragment_pdf_reader,
            container,
            false
        )

        webView =
            view.findViewById(R.id.pdf_webview)

        pageNumber =
            view.findViewById(R.id.page_number)

        likeButton =
            view.findViewById(R.id.like_button)

        searchButton =
            view.findViewById(R.id.search_button)

        searchLayout =
            view.findViewById(R.id.search_layout)

        searchInput =
            view.findViewById(R.id.search_input)

        searchCount =
            view.findViewById(R.id.search_count)

        btnNext =
            view.findViewById(R.id.btn_next)

        btnPrev =
            view.findViewById(R.id.btn_prev)

        view.findViewById<TextView>(R.id.book_title).text =
            bookTitle

        // BACK

        view.findViewById<ImageButton>(R.id.back_button)
            .setOnClickListener {

                saveCurrentProgress()

                parentFragmentManager.popBackStack()
            }

        // LIKE

        updateLikeIcon()

        likeButton.setOnClickListener {
            toggleLike()
        }

        // SEARCH PANEL

        searchButton.setOnClickListener {

            if (searchLayout.visibility == View.GONE) {

                searchLayout.visibility = View.VISIBLE

            } else {

                searchLayout.visibility = View.GONE
            }
        }

        // SEARCH NEXT

        btnNext.setOnClickListener {

            val query =
                searchInput.text.toString().trim()

            if (query.isNotEmpty()) {

                nextSearch(query)
            }
        }

        // SEARCH PREV

        btnPrev.setOnClickListener {

            val query =
                searchInput.text.toString().trim()

            if (query.isNotEmpty()) {

                prevSearch(query)
            }
        }

        currentPage =
            bookmarkManager.getSavedPage(bookId)

        bookmarkManager.saveBookInfo(
            bookId,
            bookTitle,
            bookCoverImage,
            pdfPath
        )

        setupWebView()

        loadPdf()

        return view
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {

        webView.webViewClient =
            WebViewClient()

        webView.addJavascriptInterface(
            object {

                @android.webkit.JavascriptInterface
                fun onPageChanged(page: Int, total: Int) {

                    activity?.runOnUiThread {

                        currentPage = page
                        totalPages = total

                        pageNumber.text =
                            "${page + 1}"

                        saveCurrentProgress()
                    }
                }

            },
            "Android"
        )

        webView.webChromeClient =
            WebChromeClient()

        val settings: WebSettings =
            webView.settings

        settings.javaScriptEnabled = true
        settings.allowFileAccess = true
        settings.domStorageEnabled = true
        settings.builtInZoomControls = true
        settings.displayZoomControls = false
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.allowUniversalAccessFromFileURLs = true
        settings.allowFileAccessFromFileURLs = true

        webView.setLayerType(
            View.LAYER_TYPE_HARDWARE,
            null
        )
    }

    private fun loadPdf() {

        try {

            val encodedPdf = URLEncoder.encode(
                "file:///android_asset/$pdfPath",
                "UTF-8"
            )

            val url =
                "file:///android_asset/pdfjs/web/viewer.html?file=$encodedPdf#page=${currentPage + 1}"

            webView.loadUrl(url)

            webView.post {

                val js = """

                javascript:(function() {

                    function initPdfEvents() {

                        if (
                            typeof PDFViewerApplication === 'undefined'
                            ||
                            !PDFViewerApplication.eventBus
                        ) {

                            setTimeout(initPdfEvents, 500)
                            return
                        }

                        PDFViewerApplication.eventBus.on(
                            'pagechanging',
                            function(e) {

                                Android.onPageChanged(
                                    e.pageNumber - 1,
                                    PDFViewerApplication.pagesCount
                                )
                            }
                        )

                    }

                    initPdfEvents()

                })()

            """.trimIndent()

                webView.evaluateJavascript(js, null)
            }

        } catch (e: Exception) {

            e.printStackTrace()

            Toast.makeText(
                context,
                "PDF load error",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // SEARCH + YELLOW HIGHLIGHT

    private fun searchText(query: String) {

        val safeQuery =
            query.replace("'", "\\'")

        val js = """
            
            javascript:(function() {
            
                PDFViewerApplication.findController.executeCommand(
                    'find',
                    {
                        query: '$safeQuery',
                        phraseSearch: true,
                        highlightAll: true,
                        caseSensitive: false,
                        findPrevious: false
                    }
                );
            
            })()
            
        """.trimIndent()

        webView.evaluateJavascript(js, null)

        searchCount.text = "Search..."
    }

    private fun nextSearch(query: String) {

        val safeQuery =
            query.replace("'", "\\'")

        val js = """
            
            javascript:(function() {
            
                PDFViewerApplication.findController.executeCommand(
                    'findagain',
                    {
                        query: '$safeQuery',
                        findPrevious: false
                    }
                );
            
            })()
            
        """.trimIndent()

        webView.evaluateJavascript(js, null)
    }

    private fun prevSearch(query: String) {

        val safeQuery =
            query.replace("'", "\\'")

        val js = """
            
            javascript:(function() {
            
                PDFViewerApplication.findController.executeCommand(
                    'findagain',
                    {
                        query: '$safeQuery',
                        findPrevious: true
                    }
                );
            
            })()
            
        """.trimIndent()

        webView.evaluateJavascript(js, null)
    }

    private fun saveCurrentProgress() {

        bookmarkManager.saveProgress(
            bookId,
            currentPage,
            totalPages
        )
    }

    private fun toggleLike() {

        likeManager.toggleLike(bookId)

        updateLikeIcon()

        val message =
            if (likeManager.isLiked(bookId)) {

                "Halan kitaplara goşuldy ❤️"

            } else {

                "Halan kitaplardan aýryldy"
            }

        Toast.makeText(
            context,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun updateLikeIcon() {

        val icon =
            if (likeManager.isLiked(bookId)) {

                R.drawable.ic_like_bold

            } else {

                R.drawable.ic_like
            }

        likeButton.setImageResource(icon)
    }

    override fun onPause() {
        super.onPause()

        saveCurrentProgress()

        activity
            ?.findViewById<BottomNavigationView>(
                R.id.bottom_navigation
            )
            ?.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()

        activity
            ?.findViewById<BottomNavigationView>(
                R.id.bottom_navigation
            )
            ?.visibility = View.GONE

        updateLikeIcon()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        saveCurrentProgress()

        webView.stopLoading()
        webView.destroy()
    }
}