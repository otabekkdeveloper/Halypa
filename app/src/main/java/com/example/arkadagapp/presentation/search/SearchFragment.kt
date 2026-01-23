package com.example.arkadagapp.presentation.search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.arkadagapp.R
import com.example.arkadagapp.model.SearchResult
import com.example.arkadagapp.presentation.reader.PdfReaderFragment
import com.example.arkadagapp.utils.SearchIndexManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchFragment : Fragment() {

    private lateinit var searchInput: EditText
    private lateinit var clearIcon: ImageView
    private lateinit var searchIcon: ImageView
    private lateinit var resultsCount: TextView
    private lateinit var resultsRecycler: RecyclerView
    private lateinit var searchContainer: LinearLayout
    private lateinit var resultsAdapter: SearchResultsAdapter

    private val searchResults = mutableListOf<SearchResult>()
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        searchInput = view.findViewById(R.id.search_input)
        clearIcon = view.findViewById(R.id.clear_icon)
        searchIcon = view.findViewById(R.id.search_icon)
        resultsCount = view.findViewById(R.id.results_count)
        resultsRecycler = view.findViewById(R.id.search_results_recycler)
        searchContainer = view.findViewById(R.id.search_container)

        resultsRecycler.layoutManager = LinearLayoutManager(context)

        setupSearch()

        return view
    }

    private fun setupSearch() {
        searchContainer.setOnClickListener {
            searchInput.requestFocus()
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT)
        }

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()

                if (query.isNotEmpty()) {
                    clearIcon.visibility = View.VISIBLE
                    searchIcon.visibility = View.GONE

                    searchJob?.cancel()

                    searchJob = lifecycleScope.launch {
                        delay(500) // Debounce
                        performSearch(query)
                    }
                } else {
                    clearIcon.visibility = View.GONE
                    searchIcon.visibility = View.VISIBLE
                    resultsCount.visibility = View.GONE
                    searchResults.clear()
                    updateResults("")
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        clearIcon.setOnClickListener {
            searchInput.text.clear()
            searchInput.clearFocus()

            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchInput.windowToken, 0)
        }

        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchInput.clearFocus()
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(searchInput.windowToken, 0)
                true
            } else {
                false
            }
        }
    }

    private suspend fun performSearch(query: String) {
        withContext(Dispatchers.Main) {
            resultsCount.visibility = View.VISIBLE
            resultsCount.text = "Gözleg..."
        }

        // 1. Загружаем индекс (только первый раз)
        SearchIndexManager.loadIndex(requireContext())

        // 2. Ищем в фоне
        val results = withContext(Dispatchers.IO) {
            SearchIndexManager.search(query)
        }

        // 3. Обновляем UI
        withContext(Dispatchers.Main) {
            searchResults.clear()
            searchResults.addAll(results)

            val resultText = if (searchResults.isEmpty()) {
                "Gözlenen söz: \"$query\"\nNetije tapylmady"
            } else {
                "Gözlenen söz: \"$query\"\n${searchResults.size} netije tapyldy"
            }
            resultsCount.text = resultText
            updateResults(query)
        }
    }

    private fun updateResults(query: String) {
        resultsAdapter = SearchResultsAdapter(searchResults, query) { result ->
            openBookAtPage(result)
        }
        resultsRecycler.adapter = resultsAdapter
    }

    private fun openBookAtPage(result: SearchResult) {
        // Открываем PDF на нужной странице!
        val pdfFragment = PdfReaderFragment.newInstance(
            pdfPath = result.pdfPath,
            bookTitle = result.bookTitle,
            bookId = result.bookId,
            bookCover = result.bookCover,
            startPage = result.pageNumber // ← МАГИЯ ЗДЕСЬ!
        )

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, pdfFragment)
            .addToBackStack(null)
            .commit()
    }
}