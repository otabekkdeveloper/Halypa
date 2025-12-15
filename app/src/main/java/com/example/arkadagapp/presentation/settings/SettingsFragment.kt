package com.example.arkadagapp.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.arkadagapp.R
import com.example.arkadagapp.model.BookProgress
import com.example.arkadagapp.presentation.pdfReader.ReadingProgressAdapter
import com.example.arkadagapp.presentation.reader.PdfReaderFragment
import com.example.arkadagapp.utils.BookmarkManager

class SettingsFragment : Fragment() {

    private lateinit var progressRecycler: RecyclerView
    private lateinit var languageSpinner: Spinner
    private lateinit var themeSwitch: SwitchCompat
    private lateinit var bookmarkManager: BookmarkManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        progressRecycler = view.findViewById(R.id.progress_recycler)
        languageSpinner = view.findViewById(R.id.language_spinner)
        themeSwitch = view.findViewById(R.id.theme_switch)
        bookmarkManager = BookmarkManager(requireContext())

        progressRecycler.layoutManager = LinearLayoutManager(context)

        setupLanguageSpinner()
        loadReadingProgress()

        return view
    }

    private fun setupLanguageSpinner() {
        val languages = arrayOf("Türkmen", "Русский", "English")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, languages)
        languageSpinner.adapter = adapter
    }

    private fun loadReadingProgress() {
        val allProgress = bookmarkManager.getAllBooksProgress()
            .filter { it.progress > 0 } // Pokazat' tolko nachavshiesya knigi
            .sortedByDescending { it.progress } // Sortirovka po progressu

        val adapter = ReadingProgressAdapter(allProgress) { bookProgress ->
            openBookFromProgress(bookProgress)
        }
        progressRecycler.adapter = adapter
    }

    private fun openBookFromProgress(bookProgress: BookProgress) {
        // Otkryt' knigu s sohranennym progressom
        val pdfFragment = PdfReaderFragment.newInstance(
            pdfPath = bookProgress.pdfPath,
            bookTitle = bookProgress.title,
            bookId = bookProgress.bookId,
            bookCover = bookProgress.coverImage
        )
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, pdfFragment)
            .addToBackStack(null)
            .commit()
    }
}