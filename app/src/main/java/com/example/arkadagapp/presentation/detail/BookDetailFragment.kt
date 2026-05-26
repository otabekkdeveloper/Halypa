package com.example.arkadagapp.presentation.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.arkadagapp.R
import com.example.arkadagapp.model.Book
import com.example.arkadagapp.model.BookTranslation
import com.example.arkadagapp.presentation.pdfReader.PdfReaderFragment
import com.example.arkadagapp.utils.PdfUtils

class BookDetailFragment : Fragment() {

    private lateinit var book: Book
    private var selectedTranslation: BookTranslation? = null
    private var selectedVolumeIndex: Int = 0

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
        selectedTranslation = book.translations.firstOrNull()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_book_detail, container, false)

        val backButton = view.findViewById<ImageButton>(R.id.back_button)
        val bookCover = view.findViewById<ImageView>(R.id.detail_book_cover)
        val bookTitle = view.findViewById<TextView>(R.id.detail_book_title)
        val bookAuthor = view.findViewById<TextView>(R.id.detail_author)
        val bookYear = view.findViewById<TextView>(R.id.detail_year)
        val bookPages = view.findViewById<TextView>(R.id.detail_pages)

        val languageSection = view.findViewById<LinearLayout>(R.id.language_section)
        val languageText = view.findViewById<TextView>(R.id.language_text)
        val languageSpinner = view.findViewById<Spinner>(R.id.language_spinner)

        val tomSection = view.findViewById<LinearLayout>(R.id.tom_section)
        val tomSpinner = view.findViewById<Spinner>(R.id.tom_spinner)

        val readButton = view.findViewById<Button>(R.id.read_button)

        bookTitle.text = book.title
        bookAuthor.text = book.author
        bookYear.text = book.year

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        setupLanguage(languageSection, languageText, languageSpinner, bookCover, bookPages, tomSection, tomSpinner)

        setupVolumes(tomSection, tomSpinner, bookCover, bookPages)

        readButton.setOnClickListener {
            openPdfReader()
        }

        return view
    }

    private fun setupLanguage(
        languageSection: LinearLayout,
        languageText: TextView,
        languageSpinner: Spinner,
        bookCover: ImageView,
        bookPages: TextView,
        tomSection: LinearLayout,
        tomSpinner: Spinner
    ) {
        val translations = book.translations

        if (translations.size == 1) {
            // Odin yazyk - prosto tekst
            languageText.visibility = View.VISIBLE
            languageSpinner.visibility = View.GONE
            languageText.text = translations[0].language
            selectedTranslation = translations[0]

            // Pokazat' cover
            bookCover.setImageResource(selectedTranslation!!.coverImage)

            // Pokazat' pages
            updatePages(bookPages)
        } else {
            // Neskolko yazykov - dropdown
            languageText.visibility = View.GONE
            languageSpinner.visibility = View.VISIBLE

            val langNames = translations.map { it.language }
            val adapter = ArrayAdapter(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, langNames)
            languageSpinner.adapter = adapter

            languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                    selectedTranslation = translations[pos]
                    selectedVolumeIndex = 0

                    // Izmenit' cover
                    bookCover.setImageResource(selectedTranslation!!.coverImage)

                    // Izmenit' pages
                    updatePages(bookPages)

                    // Obnovit' toma
                    setupVolumes(tomSection, tomSpinner, bookCover, bookPages)
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            selectedTranslation = translations[0]
            bookCover.setImageResource(selectedTranslation!!.coverImage)
            updatePages(bookPages)
        }
    }

    private fun setupVolumes(
        tomSection: LinearLayout,
        tomSpinner: Spinner,
        bookCover: ImageView,
        bookPages: TextView
    ) {
        val volumes = selectedTranslation?.volumes

        if (volumes != null && volumes.isNotEmpty()) {
            // Est' toma - pokazat' dropdown
            tomSection.visibility = View.VISIBLE

            val volumeNames = volumes.map { it.title }
            val adapter = ArrayAdapter(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, volumeNames)
            tomSpinner.adapter = adapter

            tomSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                    selectedVolumeIndex = pos

                    // Izmenit' cover na cover toma
                    bookCover.setImageResource(volumes[pos].coverImage)

                    // Izmenit' pages NA STRANITSY TOMA
                    val pages = PdfUtils.getPdfPageCount(
                        requireContext(),
                        volumes[pos].pdfPath
                    )

                    bookPages.text = pages.toString()
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            // Postavit' pervyy tom po umolchaniyu
            bookCover.setImageResource(volumes[0].coverImage)

            val firstVolumePages = PdfUtils.getPdfPageCount(
                requireContext(),
                volumes[0].pdfPath
            )

            bookPages.text = firstVolumePages.toString()

        } else {
            // Net tomov - skryt' i pokazat' stranitsy perevoda
            tomSection.visibility = View.GONE

            val pdfPath = selectedTranslation?.pdfPath

            if (pdfPath != null) {
                val pages = PdfUtils.getPdfPageCount(requireContext(), pdfPath)
                bookPages.text = pages.toString()
            } else {
                bookPages.text = "-"
            }
        }
    }


    private fun updatePages(bookPages: TextView) {
        val translation = selectedTranslation ?: return

        if (translation.volumes != null && translation.volumes.isNotEmpty()) {

            val totalPages = translation.volumes.sumOf { volume ->
                PdfUtils.getPdfPageCount(requireContext(), volume.pdfPath)
            }

            bookPages.text = totalPages.toString()

        } else {

            val pdfPath = translation.pdfPath

            if (pdfPath != null) {
                val pages = PdfUtils.getPdfPageCount(requireContext(), pdfPath)
                bookPages.text = pages.toString()
            } else {
                bookPages.text = "-"
            }
        }
    }

    private fun openPdfReader() {
        val translation = selectedTranslation ?: return

        val pdfPath = if (translation.volumes != null && translation.volumes.isNotEmpty()) {
            translation.volumes[selectedVolumeIndex].pdfPath
        } else {
            translation.pdfPath ?: return
        }

        val coverImage = if (translation.volumes != null && translation.volumes.isNotEmpty()) {
            translation.volumes[selectedVolumeIndex].coverImage
        } else {
            translation.coverImage
        }

        val pdfFragment = PdfReaderFragment.newInstance(
            pdfPath = pdfPath,
            bookTitle = book.title,
            bookId = book.id,
            bookCover = coverImage
        )

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, pdfFragment)
            .addToBackStack(null)
            .commit()
    }
}