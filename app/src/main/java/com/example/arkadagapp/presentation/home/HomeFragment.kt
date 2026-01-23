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
import com.example.arkadagapp.model.BookTranslation
import com.example.arkadagapp.model.Volume
import com.example.arkadagapp.presentation.detail.BookDetailFragment
import com.example.arkadagapp.presentation.home.adapter.BookAdapter
import com.example.arkadagapp.utils.SearchManager

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
            title = "Älem Içre At Gezer",
            author = "Gurbanguly Berdimuhamedow",
            year = "2021",
            coverImage = R.drawable.banner_1,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    pages = "830",
                    coverImage = R.drawable.banner_1,
                    pdfPath = "books/1/tm_1.pdf"
                )
            )        ))

        // PRIMER 2: Kniga S TOMAMI (neskolko tomov)
        books.add(Book(
            id = 2,
            title = "Türkmenistan – Bitaraplygyň Mekany",
            author = "Gurbanguly Berdimuhamedow",
            year = "2021",
            coverImage = R.drawable.banner_2,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    pages = "830",
                    coverImage = R.drawable.banner_2,
                    pdfPath = "books/2/tm_2.pdf"
                )
            )
        ))

        // PRIMER 3: Kniga S PEREVODAMI (raznyye yazyki)
        books.add(Book(
            id = 3,
            title = "Ahalteke Bedewi - Biziň Buýsanjymyz we Şöhratymyz",
            author = "Saparmyrat Nyýazow",
            year = "2001",
            coverImage = R.drawable.banner_3,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    pages = "830",
                    coverImage = R.drawable.banner_3,
                    pdfPath = "books/3/tm_3.pdf"
                )
            )
        ))


        // KNIGA 4: Neskolko yazykov I RAZNYE TOMA
        books.add(Book(
            id = 4,
            title = "Ösüşiň Täze Belentliklerine Tarap",
            author = "Gurbanguly Berdimuhamedow",
            year = "2023",
            coverImage = R.drawable.banner_4,
            translations = listOf(
                // Turkmenskiy - 2 toma
                BookTranslation(
                    language = "Türkmen",
                    coverImage = R.drawable.banner_4,
                    pages = "830", // 450 + 380
                    volumes = listOf(
                        Volume(1, "Tom 1", "450", R.drawable.four_4_1, "books/4/tm/four_4_1.pdf"),
                        Volume(2, "Tom 2", "450", R.drawable.four_4_2, "books/4/tm/four_4_2.pdf"),
                        Volume(3, "Tom 3", "450", R.drawable.four_4_3, "books/4/tm/four_4_3.pdf"),
                        Volume(4, "Tom 4", "450", R.drawable.four_4_4, "books/4/tm/four_4_4.pdf"),
                        Volume(5, "Tom 5", "450", R.drawable.four_4_5, "books/4/tm/four_4_5.pdf"),
                        Volume(6, "Tom 6", "450", R.drawable.four_4_6, "books/4/tm/four_4_6.pdf"),
                        Volume(7, "Tom 7", "450", R.drawable.four_4_7, "books/4/tm/four_4_7.pdf"),
                        Volume(8, "Tom 8", "450", R.drawable.four_4_8, "books/4/tm/four_4_8.pdf"),
                        Volume(9, "Tom 9", "450", R.drawable.four_4_9, "books/4/tm/four_4_9.pdf"),
                        Volume(10, "Tom 10", "450", R.drawable.four_4_10, "books/4/tm/four_4_10.pdf"),
                        Volume(11, "Tom 11", "380", R.drawable.four_4_11, "books/4/tm/four_4_11.pdf")
                    )
                ),
                // Russkiy - 3 toma
                BookTranslation(
                    language = "Русский",
                    translator = "Gurbanguly Berdimuhamedow",
                    coverImage = R.drawable.four_ru_4_1,
                    pages = "1160", // 420 + 390 + 350
                    volumes = listOf(
                        Volume(1, "Tom 1", "450", R.drawable.four_ru_4_1, "books/4/ru/1.pdf"),
                        Volume(2, "Tom 2", "450", R.drawable.four_ru_4_2, "books/4/ru/2.pdf"),
                        Volume(3, "Tom 3", "450", R.drawable.four_ru_4_3, "books/4/ru/3.pdf"),
                        Volume(4, "Tom 4", "450", R.drawable.four_ru_4_4, "books/4/ru/4.pdf"),
                        Volume(5, "Tom 5", "450", R.drawable.four_ru_4_5, "books/4/ru/5.pdf"),
                        Volume(6, "Tom 6", "450", R.drawable.four_ru_4_6, "books/4/ru/6.pdf"),
                        Volume(7, "Tom 7", "450", R.drawable.four_ru_4_7, "books/4/ru/7.pdf"),
                        Volume(8, "Tom 8", "450", R.drawable.four_ru_4_8, "books/4/ru/8.pdf"),
                        Volume(9, "Tom 9", "450", R.drawable.four_ru_4_9, "books/4/ru/9.pdf"),
                        Volume(10, "Tom 10", "450", R.drawable.four_ru_4_10, "books/4/ru/10.pdf"),
                        Volume(11, "Tom 11", "380", R.drawable.four_ru_4_11, "books/4/ru/11.pdf")
                    )
                ),
                // Angliyskiy - 2 toma
                BookTranslation(
                    language = "English",
                    translator = "Gurbanguly Berdimuhamedow",
                    coverImage = R.drawable.four_en_4_2,
                    pages = "770", // 400 + 370
                    volumes = listOf(
                        Volume(2, "Tom 2", "450", R.drawable.four_en_4_2, "books/4/en/2.pdf"),
                        Volume(3, "Tom 3", "450", R.drawable.four_en_4_3, "books/4/en/3.pdf"),
                        Volume(4, "Tom 4", "450", R.drawable.four_en_4_4, "books/4/en/4.pdf"),
                        Volume(5, "Tom 5", "450", R.drawable.four_en_4_5, "books/4/en/5.pdf"),
                        Volume(6, "Tom 6", "450", R.drawable.four_en_4_6, "books/4/en/6.pdf"),
                        Volume(7, "Tom 7", "450", R.drawable.four_en_4_7, "books/4/en/7.pdf"),
                        Volume(8, "Tom 8", "450", R.drawable.four_en_4_8, "books/4/en/8.pdf"),
                        Volume(9, "Tom 9", "450", R.drawable.four_en_4_9, "books/4/en/9.pdf"),
                        Volume(10, "Tom 10", "450", R.drawable.four_en_4_10, "books/4/en/10.pdf"),
                        Volume(11, "Tom 11", "380", R.drawable.four_en_4_11, "books/4/en/11.pdf")
                    )
                )
            )

        ))

        books.add(Book(
            id = 5,
            title = "Dowlet gushy",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_5,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    pages = "830",
                    coverImage = R.drawable.banner_5,
                    pdfPath = "books/5/döwlet_guşy.pdf"
                )
            )
        ))
    books.add(Book(
            id = 6,
            title = "Omrumin manysy",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_6,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    pages = "830",
                    coverImage = R.drawable.banner_6,
                    pdfPath = "books/6/omrümiň_manysy.pdf"
                )
            )
        ))





//
//        books.add(Book(
//            id = 1,
//            title = "Beýik Özgertmeleriň Ýyl Ýazgylary",
//            author = "Gurbanguly Berdimuhamedow",
//            year = "2021",
//            pages = "344",
//            language = "Türkmen",
//            coverImage = R.drawable.placeholder,
//            pdfPath = "books/tm_1.pdf"
//        ))
//
//        // PRIMER 2: Kniga S TOMAMI (neskolko tomov)
//        books.add(Book(
//            id = 2,
//            title = "Türkmenistanyň dermanlyk ösümlikleri",
//            author = "Gurbanguly Berdimuhamedow",
//            year = "2021",
//            pages = "1200",
//            language = "Türkmen",
//            coverImage = R.drawable.placeholder,
//            volumes = listOf(
//                Volume(1, "Tom 1", "400", "books/tm_1.pdf"),
//                Volume(2, "Tom 2", "400", "books/tm_1.pdf"),
//                Volume(3, "Tom 3", "400", "books/tm_1.pdf")
//            )
//        ))
//
//        // PRIMER 3: Kniga S PEREVODAMI (raznyye yazyki)
//        books.add(Book(
//            id = 3,
//            title = "Ruhnama",
//            author = "Saparmyrat Nyýazow",
//            year = "2001",
//            pages = "400",
//            language = "Türkmen",
//            coverImage = R.drawable.placeholder,
//            translations = listOf(
//                Translation("Türkmen", null, "books/tm_1.pdf"),
//                Translation("Русский", "Переводчик А.", "books/tm_1.pdf"),
//                Translation("English", "Translator B.", "books/tm_1.pdf")
//            )
//        ))
//
//        // PRIMER 4: Kniga S TOMAMI I PEREVODAMI (oba!)
//        books.add(Book(
//            id = 4,
//            title = "Täze eýýamyň bosagasynda",
//            author = "Gurbanguly Berdimuhamedow",
//            year = "2023",
//            pages = "800",
//            language = "Türkmen",
//            coverImage = R.drawable.placeholder,
//            volumes = listOf(
//                Volume(1, "Tom 1", "400", "books/tm_1.pdf"),
//                Volume(2, "Tom 2", "400", "books/tm_1.pdf")
//            ),
//            translations = listOf(
//                Translation("Türkmen", null, "books/tm_1.pdf"),
//                Translation("Русский", null, "books/tm_1.pdf")
//            )
//        ))
//
//
//        books.add(Book(
//            id = 1,
//            title = "Ak şäherim Aşgabat",
//            author = "Gurbanguly Berdimuhamedow",
//            year = "2021",
//            pages = "344",
//            language = "Türkmen",
//            coverImage = R.drawable.placeholder,
//            pdfPath = "books/tm_1.pdf"
//        ))
//
//        // PRIMER 2: Kniga S TOMAMI (neskolko tomov)
//        books.add(Book(
//            id = 2,
//            title = "Türkmenistanyň dermanlyk ösümlikleri",
//            author = "Gurbanguly Berdimuhamedow",
//            year = "2021",
//            pages = "1200",
//            language = "Türkmen",
//            coverImage = R.drawable.placeholder,
//            volumes = listOf(
//                Volume(1, "Tom 1", "400", "books/tm_1.pdf"),
//                Volume(2, "Tom 2", "400", "books/tm_1.pdf"),
//                Volume(3, "Tom 3", "400", "books/tm_1.pdf")
//            )
//        ))
//
//        // PRIMER 3: Kniga S PEREVODAMI (raznyye yazyki)
//        books.add(Book(
//            id = 3,
//            title = "Ruhnama",
//            author = "Saparmyrat Nyýazow",
//            year = "2001",
//            pages = "400",
//            language = "Türkmen",
//            coverImage = R.drawable.placeholder,
//            translations = listOf(
//                Translation("Türkmen", null, "books/tm_1.pdf"),
//                Translation("Русский", "Переводчик А.", "books/tm_1.pdf"),
//                Translation("English", "Translator B.", "books/tm_1.pdf")
//            )
//        ))
//
//        // PRIMER 4: Kniga S TOMAMI I PEREVODAMI (oba!)
//        books.add(Book(
//            id = 4,
//            title = "Täze eýýamyň bosagasynda",
//            author = "Gurbanguly Berdimuhamedow",
//            year = "2023",
//            pages = "800",
//            language = "Türkmen",
//            coverImage = R.drawable.placeholder,
//            volumes = listOf(
//                Volume(1, "Tom 1", "400", "books/tm_1.pdf"),
//                Volume(2, "Tom 2", "400", "books/tm_1.pdf")
//            ),
//            translations = listOf(
//                Translation("Türkmen", null, "books/tm_1.pdf"),
//                Translation("Русский", null, "books/tm_1.pdf")
//            )
//        ))
//
//
//        books.add(Book(
//            id = 1,
//            title = "Ak şäherim Aşgabat",
//            author = "Gurbanguly Berdimuhamedow",
//            year = "2021",
//            pages = "344",
//            language = "Türkmen",
//            coverImage = R.drawable.placeholder,
//            pdfPath = "books/tm_1.pdf"
//        ))
//
//        // PRIMER 2: Kniga S TOMAMI (neskolko tomov)
//        books.add(Book(
//            id = 2,
//            title = "Türkmenistanyň dermanlyk ösümlikleri",
//            author = "Gurbanguly Berdimuhamedow",
//            year = "2021",
//            pages = "1200",
//            language = "Türkmen",
//            coverImage = R.drawable.placeholder,
//            volumes = listOf(
//                Volume(1, "Tom 1", "400", "books/tm_1.pdf"),
//                Volume(2, "Tom 2", "400", "books/tm_1.pdf"),
//                Volume(3, "Tom 3", "400", "books/tm_1.pdf")
//            )
//        ))
//
//        // PRIMER 3: Kniga S PEREVODAMI (raznyye yazyki)
//        books.add(Book(
//            id = 3,
//            title = "Ruhnama",
//            author = "Saparmyrat Nyýazow",
//            year = "2001",
//            pages = "400",
//            language = "Türkmen",
//            coverImage = R.drawable.placeholder,
//            translations = listOf(
//                Translation("Türkmen", null, "books/tm_1.pdf"),
//                Translation("Русский", "Переводчик А.", "books/tm_1.pdf"),
//                Translation("English", "Translator B.", "books/tm_1.pdf")
//            )
//        ))
//
//        // PRIMER 4: Kniga S TOMAMI I PEREVODAMI (oba!)
//        books.add(Book(
//            id = 4,
//            title = "Täze eýýamyň bosagasynda",
//            author = "Gurbanguly Berdimuhamedow",
//            year = "2023",
//            pages = "800",
//            language = "Türkmen",
//            coverImage = R.drawable.placeholder,
//            volumes = listOf(
//                Volume(1, "Tom 1", "400", "books/tm_1.pdf"),
//                Volume(2, "Tom 2", "400", "books/tm_1.pdf")
//            ),
//            translations = listOf(
//                Translation("Türkmen", null, "books/tm_1.pdf"),
//                Translation("Русский", null, "books/tm_1.pdf")
//            )
//        ))
//
//
//        books.add(Book(
//            id = 1,
//            title = "Ak şäherim Aşgabat",
//            author = "Gurbanguly Berdimuhamedow",
//            year = "2021",
//            pages = "344",
//            language = "Türkmen",
//            coverImage = R.drawable.placeholder,
//            pdfPath = "books/tm_1.pdf"
//        ))
//
//        // PRIMER 2: Kniga S TOMAMI (neskolko tomov)
//        books.add(Book(
//            id = 2,
//            title = "Türkmenistanyň dermanlyk ösümlikleri",
//            author = "Gurbanguly Berdimuhamedow",
//            year = "2021",
//            pages = "1200",
//            language = "Türkmen",
//            coverImage = R.drawable.placeholder,
//            volumes = listOf(
//                Volume(1, "Tom 1", "400", "books/tm_1.pdf"),
//                Volume(2, "Tom 2", "400", "books/tm_1.pdf"),
//                Volume(3, "Tom 3", "400", "books/tm_1.pdf")
//            )
//        ))
//
//        // PRIMER 3: Kniga S PEREVODAMI (raznyye yazyki)
//        books.add(Book(
//            id = 3,
//            title = "Ruhnama",
//            author = "Saparmyrat Nyýazow",
//            year = "2001",
//            pages = "400",
//            language = "Türkmen",
//            coverImage = R.drawable.placeholder,
//            translations = listOf(
//                Translation("Türkmen", null, "books/tm_1.pdf"),
//                Translation("Русский", "Переводчик А.", "books/tm_1.pdf"),
//                Translation("English", "Translator B.", "books/tm_1.pdf")
//            )
//        ))
//
//        // PRIMER 4: Kniga S TOMAMI I PEREVODAMI (oba!)
//        books.add(Book(
//            id = 4,
//            title = "Täze eýýamyň bosagasynda",
//            author = "Gurbanguly Berdimuhamedow",
//            year = "2023",
//            pages = "800",
//            language = "Türkmen",
//            coverImage = R.drawable.placeholder,
//            volumes = listOf(
//                Volume(1, "Tom 1", "400", "books/tm_1.pdf"),
//                Volume(2, "Tom 2", "400", "books/tm_1.pdf")
//            ),
//            translations = listOf(
//                Translation("Türkmen", null, "books/tm_1.pdf"),
//                Translation("Русский", null, "books/tm_1.pdf")
//            )
//        ))
//
//
//        books.add(Book(
//            id = 1,
//            title = "Ak şäherim Aşgabat",
//            author = "Gurbanguly Berdimuhamedow",
//            year = "2021",
//            pages = "344",
//            language = "Türkmen",
//            coverImage = R.drawable.placeholder,
//            pdfPath = "books/tm_1.pdf"
//        ))
//
//        // PRIMER 2: Kniga S TOMAMI (neskolko tomov)
//        books.add(Book(
//            id = 2,
//            title = "Türkmenistanyň dermanlyk ösümlikleri",
//            author = "Gurbanguly Berdimuhamedow",
//            year = "2021",
//            pages = "1200",
//            language = "Türkmen",
//            coverImage = R.drawable.placeholder,
//            volumes = listOf(
//                Volume(1, "Tom 1", "400", "books/tm_1.pdf"),
//                Volume(2, "Tom 2", "400", "books/tm_1.pdf"),
//                Volume(3, "Tom 3", "400", "books/tm_1.pdf")
//            )
//        ))
//
//        // PRIMER 3: Kniga S PEREVODAMI (raznyye yazyki)
//        books.add(Book(
//            id = 3,
//            title = "Ruhnama",
//            author = "Saparmyrat Nyýazow",
//            year = "2001",
//            pages = "400",
//            language = "Türkmen",
//            coverImage = R.drawable.placeholder,
//            translations = listOf(
//                Translation("Türkmen", null, "books/tm_1.pdf"),
//                Translation("Русский", "Переводчик А.", "books/tm_1.pdf"),
//                Translation("English", "Translator B.", "books/tm_1.pdf")
//            )
//        ))
//
//        // PRIMER 4: Kniga S TOMAMI I PEREVODAMI (oba!)
//        books.add(Book(
//            id = 4,
//            title = "Täze eýýamyň bosagasynda",
//            author = "Gurbanguly Berdimuhamedow",
//            year = "2023",
//            pages = "800",
//            language = "Türkmen",
//            coverImage = R.drawable.placeholder,
//            volumes = listOf(
//                Volume(1, "Tom 1", "400", "books/tm_1.pdf"),
//                Volume(2, "Tom 2", "400", "books/tm_1.pdf")
//            ),
//            translations = listOf(
//                Translation("Türkmen", null, "books/tm_1.pdf"),
//                Translation("Русский", null, "books/tm_1.pdf")
//            )
//        ))
//
//
//
//        books.add(Book(
//            id = 1,
//            title = "Ak şäherim Aşgabat",
//            author = "Gurbanguly Berdimuhamedow",
//            year = "2021",
//            pages = "344",
//            language = "Türkmen",
//            coverImage = R.drawable.placeholder,
//            pdfPath = "books/tm_1.pdf"
//        ))
//
//        // PRIMER 2: Kniga S TOMAMI (neskolko tomov)
//        books.add(Book(
//            id = 2,
//            title = "Türkmenistanyň dermanlyk ösümlikleri",
//            author = "Gurbanguly Berdimuhamedow",
//            year = "2021",
//            pages = "1200",
//            language = "Türkmen",
//            coverImage = R.drawable.placeholder,
//            volumes = listOf(
//                Volume(1, "Tom 1", "400", "books/tm_1.pdf"),
//                Volume(2, "Tom 2", "400", "books/tm_1.pdf"),
//                Volume(3, "Tom 3", "400", "books/tm_1.pdf")
//            )
//        ))
//
//        // PRIMER 3: Kniga S PEREVODAMI (raznyye yazyki)
//        books.add(Book(
//            id = 3,
//            title = "Ruhnama",
//            author = "Saparmyrat Nyýazow",
//            year = "2001",
//            pages = "400",
//            language = "Türkmen",
//            coverImage = R.drawable.placeholder,
//            translations = listOf(
//                Translation("Türkmen", null, "books/tm_1.pdf"),
//                Translation("Русский", "Переводчик А.", "books/tm_1.pdf"),
//                Translation("English", "Translator B.", "books/tm_1.pdf")
//            )
//        ))
//
//        // PRIMER 4: Kniga S TOMAMI I PEREVODAMI (oba!)
//        books.add(Book(
//            id = 4,
//            title = "Täze eýýamyň bosagasynda",
//            author = "Gurbanguly Berdimuhamedow",
//            year = "2023",
//            pages = "800",
//            language = "Türkmen",
//            coverImage = R.drawable.placeholder,
//            volumes = listOf(
//                Volume(1, "Tom 1", "400", "books/tm_1.pdf"),
//                Volume(2, "Tom 2", "400", "books/tm_1.pdf")
//            ),
//            translations = listOf(
//                Translation("Türkmen", null, "books/tm_1.pdf"),
//                Translation("Русский", null, "books/tm_1.pdf")
//            )
//        ))
//
//
//
//        books.add(Book(
//            id = 1,
//            title = "Ak şäherim Aşgabat",
//            author = "Gurbanguly Berdimuhamedow",
//            year = "2021",
//            pages = "344",
//            language = "Türkmen",
//            coverImage = R.drawable.placeholder,
//            pdfPath = "books/tm_1.pdf"
//        ))
//
//        // PRIMER 2: Kniga S TOMAMI (neskolko tomov)
//        books.add(Book(
//            id = 2,
//            title = "Türkmenistanyň dermanlyk ösümlikleri",
//            author = "Gurbanguly Berdimuhamedow",
//            year = "2021",
//            pages = "1200",
//            language = "Türkmen",
//            coverImage = R.drawable.placeholder,
//            volumes = listOf(
//                Volume(1, "Tom 1", "400", "books/tm_1.pdf"),
//                Volume(2, "Tom 2", "400", "books/tm_1.pdf"),
//                Volume(3, "Tom 3", "400", "books/tm_1.pdf")
//            )
//        ))
//
//        // PRIMER 3: Kniga S PEREVODAMI (raznyye yazyki)
//        books.add(Book(
//            id = 3,
//            title = "Ruhnama",
//            author = "Saparmyrat Nyýazow",
//            year = "2001",
//            pages = "400",
//            language = "Türkmen",
//            coverImage = R.drawable.placeholder,
//            translations = listOf(
//                Translation("Türkmen", null, "books/tm_1.pdf"),
//                Translation("Русский", "Переводчик А.", "books/tm_1.pdf"),
//                Translation("English", "Translator B.", "books/tm_1.pdf")
//            )
//        ))
//
//        // PRIMER 4: Kniga S TOMAMI I PEREVODAMI (oba!)
//        books.add(Book(
//            id = 4,
//            title = "Täze eýýamyň bosagasynda",
//            author = "Gurbanguly Berdimuhamedow",
//            year = "2023",
//            pages = "800",
//            language = "Türkmen",
//            coverImage = R.drawable.placeholder,
//            volumes = listOf(
//                Volume(1, "Tom 1", "400", "books/tm_1.pdf"),
//                Volume(2, "Tom 2", "400", "books/tm_1.pdf")
//            ),
//            translations = listOf(
//                Translation("Türkmen", null, "books/tm_1.pdf"),
//                Translation("Русский", null, "books/tm_1.pdf")
//            )
//        ))
        SearchManager.setBooks(books)
    }

    private fun openBookDetail(book: Book) {
        val fragment = BookDetailFragment.newInstance(book)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}