package com.example.arkadagapp.presentation.settings

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.arkadagapp.R
import com.example.arkadagapp.model.BookProgress
import com.example.arkadagapp.presentation.pdfReader.PdfReaderFragment
import com.example.arkadagapp.presentation.pdfReader.ReadingProgressAdapter
import com.example.arkadagapp.presentation.settings.favoriteBooksFragment.FavoriteBooksFragment
import com.example.arkadagapp.presentation.settings.favoriteQuotesFragment.FavoriteQuotesFragment
import com.example.arkadagapp.utils.BookmarkManager
import com.example.arkadagapp.utils.LocaleHelper
import com.example.arkadagapp.utils.QuotesManager
import com.example.arkadagapp.utils.ThemeHelper

class SettingsFragment : Fragment() {

    private lateinit var progressRecycler: RecyclerView
    private lateinit var languageSpinner: Spinner
    private lateinit var themeSwitch: SwitchCompat
    private lateinit var bookmarkManager: BookmarkManager

    @SuppressLint("MissingInflatedId")
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
        setupThemeSwitch()

        // Favorite Quotes Button
        val favoriteQuotesButton = view.findViewById<LinearLayout>(R.id.favorite_quotes_button)
        favoriteQuotesButton.setOnClickListener {
            val fragment = FavoriteQuotesFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }








        return view
    }

    override fun onResume() {
        super.onResume()

        loadReadingProgress()

        val quotesCount =
            view?.findViewById<TextView>(R.id.quotes_count)

        quotesCount?.text =
            QuotesManager.getFavoriteQuotes()
                .size
                .toString()
    }


    private fun setupThemeSwitch() {
        val isDark = ThemeHelper.isDarkTheme(requireContext())
        themeSwitch.isChecked = isDark

        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            val newTheme = if (isChecked) ThemeHelper.THEME_DARK else ThemeHelper.THEME_LIGHT
            ThemeHelper.setTheme(requireContext(), newTheme)

            // ✅ Перезапустить с анимацией
            requireActivity().recreate()
        }
    }
    private fun setupLanguageSpinner() {
        val languages = arrayOf("Türkmen", "Русский", "English")
        val languageCodes = arrayOf("tk", "ru", "en")

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item,
            languages
        )

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        languageSpinner.adapter = adapter

        // ✅ Установить текущий выбранный язык
        val currentLanguage = LocaleHelper.getLocale(requireContext())
        val position = languageCodes.indexOf(currentLanguage)
        if (position != -1) {
            languageSpinner.setSelection(position)
        }

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            private var isFirstSelection = true // Чтобы не срабатывало при первой загрузке

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (isFirstSelection) {
                    isFirstSelection = false
                    return
                }

                val selectedLanguageCode = languageCodes[position]
                val currentLanguage = LocaleHelper.getLocale(requireContext())

                if (selectedLanguageCode != currentLanguage) {
                    // Сохранить новый язык
                    LocaleHelper.setLocale(requireContext(), selectedLanguageCode)

                    // ✅ Плавная анимация перезапуска
                    val intent = requireActivity().intent
                    requireActivity().finish()
                    startActivity(intent)

                    // ✅ Плавная fade анимация
                    requireActivity().overridePendingTransition(
                        android.R.anim.fade_in,  // входящая анимация
                        android.R.anim.fade_out  // исходящая анимация
                    )
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun loadReadingProgress() {
        val allProgress = bookmarkManager.getAllBooksProgress()
            .filter { it.progress > 0 }
            .sortedByDescending { it.lastReadTime }

        val adapter = ReadingProgressAdapter(allProgress) { bookProgress ->
            openBookFromProgress(bookProgress)
        }
        progressRecycler.adapter = adapter
    }

    private fun openBookFromProgress(bookProgress: BookProgress) {
        val pdfFragment = PdfReaderFragment.newInstance(
            pdfPath = bookProgress.pdfPath,
            bookTitle = bookProgress.title,
            bookId = bookProgress.bookId,
            bookCover = bookProgress.coverImage,
            startPage = bookProgress.currentPage
        )

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, pdfFragment)
            .addToBackStack(null)
            .commit()
    }
}