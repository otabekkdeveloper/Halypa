package com.example.arkadagapp.presentation.home

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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.arkadagapp.R
import com.example.arkadagapp.model.Book
import com.example.arkadagapp.model.Translation
import com.example.arkadagapp.model.Volume
import com.example.arkadagapp.presentation.detail.BookDetailFragment
import com.example.arkadagapp.presentation.home.adapter.BookAdapter

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bookAdapter: BookAdapter
    private lateinit var searchInput: EditText
    private lateinit var searchContainer: LinearLayout
    private lateinit var clearIcon: ImageView
    private lateinit var searchIcon: ImageView

    private var books = mutableListOf<Book>()
    private var filteredBooks = mutableListOf<Book>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize views
        recyclerView = view.findViewById(R.id.books_recycler_view)
        searchInput = view.findViewById(R.id.search_input)
        searchContainer = view.findViewById(R.id.search_container)
        clearIcon = view.findViewById(R.id.clear_icon)
        searchIcon = view.findViewById(R.id.search_icon)

        // Grid Layout s 2 kolonkami
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        // Zagruzhaem knigi (TOLKO RAZ!)
        books.clear()
        loadBooks()
        filteredBooks.addAll(books)

        // Adapter s klikom
        bookAdapter = BookAdapter(filteredBooks) { book ->
            openBookDetail(book)
        }
        recyclerView.adapter = bookAdapter

        // Setup search
        setupSearch()

        return view
    }

    private fun setupSearch() {
        // Klik na container - otkryt' klaviaturu
        searchContainer.setOnClickListener {
            searchInput.requestFocus()
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT)
        }

        // TextWatcher - otslezhivat' izmeneniya teksta
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()

                // Filtr knig
                filterBooks(query)

                // Pokazat'/skryt' close icon
                if (query.isNotEmpty()) {
                    clearIcon.visibility = View.VISIBLE
                    searchIcon.visibility = View.GONE
                } else {
                    clearIcon.visibility = View.GONE
                    searchIcon.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Clear icon - ochistit' tekst
        clearIcon.setOnClickListener {
            searchInput.text.clear()
            searchInput.clearFocus()

            // Zakryt' klaviaturu
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchInput.windowToken, 0)
        }

        // Enter na klaviature
        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchInput.clearFocus()

                // Zakryt' klaviaturu
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(searchInput.windowToken, 0)
                true
            } else {
                false
            }
        }
    }

    private fun filterBooks(query: String) {
        filteredBooks.clear()

        if (query.isEmpty()) {
            // Pustoy poisk - pokazat' vse knigi
            filteredBooks.addAll(books)
        } else {
            // Filter po title i author
            val filtered = books.filter { book ->
                book.title.contains(query, ignoreCase = true) ||
                        book.author.contains(query, ignoreCase = true)
            }
            filteredBooks.addAll(filtered)
        }

        bookAdapter.notifyDataSetChanged()
    }

    private fun loadBooks() {
        // PRIMER 1: Kniga BEZ tomov i BEZ perevodov (prostaya)
        books.add(Book(
            id = 1,
            title = "Ak şäherim Aşgabat",
            author = "Gurbanguly Berdimuhamedow",
            year = "2021",
            pages = "344",
            language = "Türkmen",
            coverImage = R.drawable.placeholder,
            pdfPath = "books/tm.pdf"
        ))

        // PRIMER 2: Kniga S TOMAMI (neskolko tomov)
        books.add(Book(
            id = 2,
            title = "Türkmenistanyň dermanlyk ösümlikleri",
            author = "Gurbanguly Berdimuhamedow",
            year = "2021",
            pages = "1200",
            language = "Türkmen",
            coverImage = R.drawable.placeholder,
            volumes = listOf(
                Volume(1, "Tom 1", "400", "books/tm.pdf"),
                Volume(2, "Tom 2", "400", "books/tm.pdf"),
                Volume(3, "Tom 3", "400", "books/tm.pdf")
            )
        ))

        // PRIMER 3: Kniga S PEREVODAMI (raznyye yazyki)
        books.add(Book(
            id = 3,
            title = "Ruhnama",
            author = "Saparmyrat Nyýazow",
            year = "2001",
            pages = "400",
            language = "Türkmen",
            coverImage = R.drawable.placeholder,
            translations = listOf(
                Translation("Türkmen", null, "books/tm.pdf"),
                Translation("Русский", "Переводчик А.", "books/tm.pdf"),
                Translation("English", "Translator B.", "books/tm.pdf")
            )
        ))

        // PRIMER 4: Kniga S TOMAMI I PEREVODAMI (oba!)
        books.add(Book(
            id = 4,
            title = "Täze eýýamyň bosagasynda",
            author = "Gurbanguly Berdimuhamedow",
            year = "2023",
            pages = "800",
            language = "Türkmen",
            coverImage = R.drawable.placeholder,
            volumes = listOf(
                Volume(1, "Tom 1", "400", "books/tm.pdf"),
                Volume(2, "Tom 2", "400", "books/tm.pdf")
            ),
            translations = listOf(
                Translation("Türkmen", null, "books/tm.pdf"),
                Translation("Русский", null, "books/tm.pdf")
            )
        ))

        books.add(Book(
            id = 1,
            title = "Ak şäherim Aşgabat",
            author = "Gurbanguly Berdimuhamedow",
            year = "2021",
            pages = "344",
            language = "Türkmen",
            coverImage = R.drawable.placeholder,
            pdfPath = "books/tm.pdf"
        ))

        // PRIMER 2: Kniga S TOMAMI (neskolko tomov)
        books.add(Book(
            id = 2,
            title = "Türkmenistanyň dermanlyk ösümlikleri",
            author = "Gurbanguly Berdimuhamedow",
            year = "2021",
            pages = "1200",
            language = "Türkmen",
            coverImage = R.drawable.placeholder,
            volumes = listOf(
                Volume(1, "Tom 1", "400", "books/tm.pdf"),
                Volume(2, "Tom 2", "400", "books/tm.pdf"),
                Volume(3, "Tom 3", "400", "books/tm.pdf")
            )
        ))

        // PRIMER 3: Kniga S PEREVODAMI (raznyye yazyki)
        books.add(Book(
            id = 3,
            title = "Ruhnama",
            author = "Saparmyrat Nyýazow",
            year = "2001",
            pages = "400",
            language = "Türkmen",
            coverImage = R.drawable.placeholder,
            translations = listOf(
                Translation("Türkmen", null, "books/tm.pdf"),
                Translation("Русский", "Переводчик А.", "books/tm.pdf"),
                Translation("English", "Translator B.", "books/tm.pdf")
            )
        ))

        // PRIMER 4: Kniga S TOMAMI I PEREVODAMI (oba!)
        books.add(Book(
            id = 4,
            title = "Täze eýýamyň bosagasynda",
            author = "Gurbanguly Berdimuhamedow",
            year = "2023",
            pages = "800",
            language = "Türkmen",
            coverImage = R.drawable.placeholder,
            volumes = listOf(
                Volume(1, "Tom 1", "400", "books/tm.pdf"),
                Volume(2, "Tom 2", "400", "books/tm.pdf")
            ),
            translations = listOf(
                Translation("Türkmen", null, "books/tm.pdf"),
                Translation("Русский", null, "books/tm.pdf")
            )
        ))


        books.add(Book(
            id = 1,
            title = "Ak şäherim Aşgabat",
            author = "Gurbanguly Berdimuhamedow",
            year = "2021",
            pages = "344",
            language = "Türkmen",
            coverImage = R.drawable.placeholder,
            pdfPath = "books/tm.pdf"
        ))

        // PRIMER 2: Kniga S TOMAMI (neskolko tomov)
        books.add(Book(
            id = 2,
            title = "Türkmenistanyň dermanlyk ösümlikleri",
            author = "Gurbanguly Berdimuhamedow",
            year = "2021",
            pages = "1200",
            language = "Türkmen",
            coverImage = R.drawable.placeholder,
            volumes = listOf(
                Volume(1, "Tom 1", "400", "books/tm.pdf"),
                Volume(2, "Tom 2", "400", "books/tm.pdf"),
                Volume(3, "Tom 3", "400", "books/tm.pdf")
            )
        ))

        // PRIMER 3: Kniga S PEREVODAMI (raznyye yazyki)
        books.add(Book(
            id = 3,
            title = "Ruhnama",
            author = "Saparmyrat Nyýazow",
            year = "2001",
            pages = "400",
            language = "Türkmen",
            coverImage = R.drawable.placeholder,
            translations = listOf(
                Translation("Türkmen", null, "books/tm.pdf"),
                Translation("Русский", "Переводчик А.", "books/tm.pdf"),
                Translation("English", "Translator B.", "books/tm.pdf")
            )
        ))

        // PRIMER 4: Kniga S TOMAMI I PEREVODAMI (oba!)
        books.add(Book(
            id = 4,
            title = "Täze eýýamyň bosagasynda",
            author = "Gurbanguly Berdimuhamedow",
            year = "2023",
            pages = "800",
            language = "Türkmen",
            coverImage = R.drawable.placeholder,
            volumes = listOf(
                Volume(1, "Tom 1", "400", "books/tm.pdf"),
                Volume(2, "Tom 2", "400", "books/tm.pdf")
            ),
            translations = listOf(
                Translation("Türkmen", null, "books/tm.pdf"),
                Translation("Русский", null, "books/tm.pdf")
            )
        ))


        books.add(Book(
            id = 1,
            title = "Ak şäherim Aşgabat",
            author = "Gurbanguly Berdimuhamedow",
            year = "2021",
            pages = "344",
            language = "Türkmen",
            coverImage = R.drawable.placeholder,
            pdfPath = "books/tm.pdf"
        ))

        // PRIMER 2: Kniga S TOMAMI (neskolko tomov)
        books.add(Book(
            id = 2,
            title = "Türkmenistanyň dermanlyk ösümlikleri",
            author = "Gurbanguly Berdimuhamedow",
            year = "2021",
            pages = "1200",
            language = "Türkmen",
            coverImage = R.drawable.placeholder,
            volumes = listOf(
                Volume(1, "Tom 1", "400", "books/tm.pdf"),
                Volume(2, "Tom 2", "400", "books/tm.pdf"),
                Volume(3, "Tom 3", "400", "books/tm.pdf")
            )
        ))

        // PRIMER 3: Kniga S PEREVODAMI (raznyye yazyki)
        books.add(Book(
            id = 3,
            title = "Ruhnama",
            author = "Saparmyrat Nyýazow",
            year = "2001",
            pages = "400",
            language = "Türkmen",
            coverImage = R.drawable.placeholder,
            translations = listOf(
                Translation("Türkmen", null, "books/tm.pdf"),
                Translation("Русский", "Переводчик А.", "books/tm.pdf"),
                Translation("English", "Translator B.", "books/tm.pdf")
            )
        ))

        // PRIMER 4: Kniga S TOMAMI I PEREVODAMI (oba!)
        books.add(Book(
            id = 4,
            title = "Täze eýýamyň bosagasynda",
            author = "Gurbanguly Berdimuhamedow",
            year = "2023",
            pages = "800",
            language = "Türkmen",
            coverImage = R.drawable.placeholder,
            volumes = listOf(
                Volume(1, "Tom 1", "400", "books/tm.pdf"),
                Volume(2, "Tom 2", "400", "books/tm.pdf")
            ),
            translations = listOf(
                Translation("Türkmen", null, "books/tm.pdf"),
                Translation("Русский", null, "books/tm.pdf")
            )
        ))


        books.add(Book(
            id = 1,
            title = "Ak şäherim Aşgabat",
            author = "Gurbanguly Berdimuhamedow",
            year = "2021",
            pages = "344",
            language = "Türkmen",
            coverImage = R.drawable.placeholder,
            pdfPath = "books/tm.pdf"
        ))

        // PRIMER 2: Kniga S TOMAMI (neskolko tomov)
        books.add(Book(
            id = 2,
            title = "Türkmenistanyň dermanlyk ösümlikleri",
            author = "Gurbanguly Berdimuhamedow",
            year = "2021",
            pages = "1200",
            language = "Türkmen",
            coverImage = R.drawable.placeholder,
            volumes = listOf(
                Volume(1, "Tom 1", "400", "books/tm.pdf"),
                Volume(2, "Tom 2", "400", "books/tm.pdf"),
                Volume(3, "Tom 3", "400", "books/tm.pdf")
            )
        ))

        // PRIMER 3: Kniga S PEREVODAMI (raznyye yazyki)
        books.add(Book(
            id = 3,
            title = "Ruhnama",
            author = "Saparmyrat Nyýazow",
            year = "2001",
            pages = "400",
            language = "Türkmen",
            coverImage = R.drawable.placeholder,
            translations = listOf(
                Translation("Türkmen", null, "books/tm.pdf"),
                Translation("Русский", "Переводчик А.", "books/tm.pdf"),
                Translation("English", "Translator B.", "books/tm.pdf")
            )
        ))

        // PRIMER 4: Kniga S TOMAMI I PEREVODAMI (oba!)
        books.add(Book(
            id = 4,
            title = "Täze eýýamyň bosagasynda",
            author = "Gurbanguly Berdimuhamedow",
            year = "2023",
            pages = "800",
            language = "Türkmen",
            coverImage = R.drawable.placeholder,
            volumes = listOf(
                Volume(1, "Tom 1", "400", "books/tm.pdf"),
                Volume(2, "Tom 2", "400", "books/tm.pdf")
            ),
            translations = listOf(
                Translation("Türkmen", null, "books/tm.pdf"),
                Translation("Русский", null, "books/tm.pdf")
            )
        ))


        books.add(Book(
            id = 1,
            title = "Ak şäherim Aşgabat",
            author = "Gurbanguly Berdimuhamedow",
            year = "2021",
            pages = "344",
            language = "Türkmen",
            coverImage = R.drawable.placeholder,
            pdfPath = "books/tm.pdf"
        ))

        // PRIMER 2: Kniga S TOMAMI (neskolko tomov)
        books.add(Book(
            id = 2,
            title = "Türkmenistanyň dermanlyk ösümlikleri",
            author = "Gurbanguly Berdimuhamedow",
            year = "2021",
            pages = "1200",
            language = "Türkmen",
            coverImage = R.drawable.placeholder,
            volumes = listOf(
                Volume(1, "Tom 1", "400", "books/tm.pdf"),
                Volume(2, "Tom 2", "400", "books/tm.pdf"),
                Volume(3, "Tom 3", "400", "books/tm.pdf")
            )
        ))

        // PRIMER 3: Kniga S PEREVODAMI (raznyye yazyki)
        books.add(Book(
            id = 3,
            title = "Ruhnama",
            author = "Saparmyrat Nyýazow",
            year = "2001",
            pages = "400",
            language = "Türkmen",
            coverImage = R.drawable.placeholder,
            translations = listOf(
                Translation("Türkmen", null, "books/tm.pdf"),
                Translation("Русский", "Переводчик А.", "books/tm.pdf"),
                Translation("English", "Translator B.", "books/tm.pdf")
            )
        ))

        // PRIMER 4: Kniga S TOMAMI I PEREVODAMI (oba!)
        books.add(Book(
            id = 4,
            title = "Täze eýýamyň bosagasynda",
            author = "Gurbanguly Berdimuhamedow",
            year = "2023",
            pages = "800",
            language = "Türkmen",
            coverImage = R.drawable.placeholder,
            volumes = listOf(
                Volume(1, "Tom 1", "400", "books/tm.pdf"),
                Volume(2, "Tom 2", "400", "books/tm.pdf")
            ),
            translations = listOf(
                Translation("Türkmen", null, "books/tm.pdf"),
                Translation("Русский", null, "books/tm.pdf")
            )
        ))



        books.add(Book(
            id = 1,
            title = "Ak şäherim Aşgabat",
            author = "Gurbanguly Berdimuhamedow",
            year = "2021",
            pages = "344",
            language = "Türkmen",
            coverImage = R.drawable.placeholder,
            pdfPath = "books/tm.pdf"
        ))

        // PRIMER 2: Kniga S TOMAMI (neskolko tomov)
        books.add(Book(
            id = 2,
            title = "Türkmenistanyň dermanlyk ösümlikleri",
            author = "Gurbanguly Berdimuhamedow",
            year = "2021",
            pages = "1200",
            language = "Türkmen",
            coverImage = R.drawable.placeholder,
            volumes = listOf(
                Volume(1, "Tom 1", "400", "books/tm.pdf"),
                Volume(2, "Tom 2", "400", "books/tm.pdf"),
                Volume(3, "Tom 3", "400", "books/tm.pdf")
            )
        ))

        // PRIMER 3: Kniga S PEREVODAMI (raznyye yazyki)
        books.add(Book(
            id = 3,
            title = "Ruhnama",
            author = "Saparmyrat Nyýazow",
            year = "2001",
            pages = "400",
            language = "Türkmen",
            coverImage = R.drawable.placeholder,
            translations = listOf(
                Translation("Türkmen", null, "books/tm.pdf"),
                Translation("Русский", "Переводчик А.", "books/tm.pdf"),
                Translation("English", "Translator B.", "books/tm.pdf")
            )
        ))

        // PRIMER 4: Kniga S TOMAMI I PEREVODAMI (oba!)
        books.add(Book(
            id = 4,
            title = "Täze eýýamyň bosagasynda",
            author = "Gurbanguly Berdimuhamedow",
            year = "2023",
            pages = "800",
            language = "Türkmen",
            coverImage = R.drawable.placeholder,
            volumes = listOf(
                Volume(1, "Tom 1", "400", "books/tm.pdf"),
                Volume(2, "Tom 2", "400", "books/tm.pdf")
            ),
            translations = listOf(
                Translation("Türkmen", null, "books/tm.pdf"),
                Translation("Русский", null, "books/tm.pdf")
            )
        ))



        books.add(Book(
            id = 1,
            title = "Ak şäherim Aşgabat",
            author = "Gurbanguly Berdimuhamedow",
            year = "2021",
            pages = "344",
            language = "Türkmen",
            coverImage = R.drawable.placeholder,
            pdfPath = "books/tm.pdf"
        ))

        // PRIMER 2: Kniga S TOMAMI (neskolko tomov)
        books.add(Book(
            id = 2,
            title = "Türkmenistanyň dermanlyk ösümlikleri",
            author = "Gurbanguly Berdimuhamedow",
            year = "2021",
            pages = "1200",
            language = "Türkmen",
            coverImage = R.drawable.placeholder,
            volumes = listOf(
                Volume(1, "Tom 1", "400", "books/tm.pdf"),
                Volume(2, "Tom 2", "400", "books/tm.pdf"),
                Volume(3, "Tom 3", "400", "books/tm.pdf")
            )
        ))

        // PRIMER 3: Kniga S PEREVODAMI (raznyye yazyki)
        books.add(Book(
            id = 3,
            title = "Ruhnama",
            author = "Saparmyrat Nyýazow",
            year = "2001",
            pages = "400",
            language = "Türkmen",
            coverImage = R.drawable.placeholder,
            translations = listOf(
                Translation("Türkmen", null, "books/tm.pdf"),
                Translation("Русский", "Переводчик А.", "books/tm.pdf"),
                Translation("English", "Translator B.", "books/tm.pdf")
            )
        ))

        // PRIMER 4: Kniga S TOMAMI I PEREVODAMI (oba!)
        books.add(Book(
            id = 4,
            title = "Täze eýýamyň bosagasynda",
            author = "Gurbanguly Berdimuhamedow",
            year = "2023",
            pages = "800",
            language = "Türkmen",
            coverImage = R.drawable.placeholder,
            volumes = listOf(
                Volume(1, "Tom 1", "400", "books/tm.pdf"),
                Volume(2, "Tom 2", "400", "books/tm.pdf")
            ),
            translations = listOf(
                Translation("Türkmen", null, "books/tm.pdf"),
                Translation("Русский", null, "books/tm.pdf")
            )
        ))

    }

    private fun openBookDetail(book: Book) {
        val fragment = BookDetailFragment.newInstance(book)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}