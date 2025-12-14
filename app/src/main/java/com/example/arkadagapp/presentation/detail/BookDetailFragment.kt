package com.example.arkadagapp.presentation.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.arkadagapp.R
import com.example.arkadagapp.model.Book
import com.example.arkadagapp.model.Translation
import com.example.arkadagapp.model.Volume
import com.example.arkadagapp.presentation.pdfReader.PdfReaderFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class BookDetailFragment : Fragment() {

    private lateinit var book: Book
    private var selectedVolume: Volume? = null
    private var selectedTranslation: Translation? = null

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

        // Set basic book data
        view.findViewById<ImageView>(R.id.detail_book_cover).setImageResource(book.coverImage)
        view.findViewById<TextView>(R.id.detail_book_title).text = book.title
        view.findViewById<TextView>(R.id.detail_author).text = book.author
        view.findViewById<TextView>(R.id.detail_year).text = book.year
        view.findViewById<TextView>(R.id.detail_pages).text = book.pages

        val tomSection = view.findViewById<LinearLayout>(R.id.tom_section)
        val tomSpinner = view.findViewById<Spinner>(R.id.tom_spinner)
        val languageText = view.findViewById<TextView>(R.id.language_text)
        val languageSpinner = view.findViewById<Spinner>(R.id.language_spinner)
        val readButton = view.findViewById<Button>(R.id.read_button)

        // LOGIKA 1: TOMA (Jilt)
        val volumes = book.volumes
        if (volumes != null && volumes.isNotEmpty()) {
            // POKAZAT' Tom dropdown
            tomSection.visibility = View.VISIBLE

            val volumeNames = volumes.map { "${it.title}" }
            val volumeAdapter = ArrayAdapter(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, volumeNames)
            tomSpinner.adapter = volumeAdapter

            tomSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                    selectedVolume = volumes[pos]
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            selectedVolume = volumes[0]
        } else {
            // SKRYT' Tom section
            tomSection.visibility = View.GONE
        }

        // LOGIKA 2: YAZYK (Dil)
        val translations = book.translations
        if (translations != null && translations.isNotEmpty()) {
            if (translations.size == 1) {
                // ODIN yazyk - prosto tekst
                languageText.visibility = View.VISIBLE
                languageSpinner.visibility = View.GONE
                languageText.text = translations[0].language
                selectedTranslation = translations[0]
            } else {
                // NESKOLKO yazykov - dropdown
                languageText.visibility = View.GONE
                languageSpinner.visibility = View.VISIBLE

                val langNames = translations.map { it.language }
                val langAdapter = ArrayAdapter(requireContext(),
                    android.R.layout.simple_spinner_dropdown_item, langNames)
                languageSpinner.adapter = langAdapter

                languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                        selectedTranslation = translations[pos]
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }

                selectedTranslation = translations[0]
            }
        } else {
            // NET perevodov - prosto yazyk knigi
            languageText.visibility = View.VISIBLE
            languageSpinner.visibility = View.GONE
            languageText.text = book.language
        }

        // Read Button
        readButton.setOnClickListener {
            val pdfPath = when {
                selectedVolume != null -> selectedVolume!!.pdfPath
                selectedTranslation != null -> selectedTranslation!!.pdfPath
                else -> book.pdfPath
            }

            pdfPath?.let { openPdfReader(it) }
        }

        return view
    }

    private fun openPdfReader(pdfPath: String) {
        // Otkryt' PDF Reader Fragment
        val pdfFragment = PdfReaderFragment.newInstance(pdfPath, book.title)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, pdfFragment)
            .addToBackStack(null)
            .commit()
    }
}


