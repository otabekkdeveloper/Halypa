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
//                    pages = "830",
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
//                    pages = "830",
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
//                    pages = "830",
                    coverImage = R.drawable.banner_3,
                    pdfPath = "books/3/tm_3.pdf"
                )
            )
        ))


        // KNIGA 4:Ösüşiň Täze Belentliklerine Tarap
//        books.add(Book(
//            id = 4,
//            title = "Ösüşiň Täze Belentliklerine Tarap",
//            author = "Gurbanguly Berdimuhamedow",
//            year = "2023",
//            coverImage = R.drawable.banner_4,
//            translations = listOf(
//                // Turkmenskiy - 2 toma
//                BookTranslation(
//                    language = "Türkmen",
//                    coverImage = R.drawable.banner_4,
//                    pages = "830", // 450 + 380
//                    volumes = listOf(
//                        Volume(1, "Tom 1", "450", R.drawable.four_4_1, "books/4/tm/four_4_1.pdf"),
//                        Volume(2, "Tom 2", "450", R.drawable.four_4_2, "books/4/tm/four_4_2.pdf"),
//                        Volume(3, "Tom 3", "450", R.drawable.four_4_3, "books/4/tm/four_4_3.pdf"),
//                        Volume(4, "Tom 4", "450", R.drawable.four_4_4, "books/4/tm/four_4_4.pdf"),
//                        Volume(5, "Tom 5", "450", R.drawable.four_4_5, "books/4/tm/four_4_5.pdf"),
//                        Volume(6, "Tom 6", "450", R.drawable.four_4_6, "books/4/tm/four_4_6.pdf"),
//                        Volume(7, "Tom 7", "450", R.drawable.four_4_7, "books/4/tm/four_4_7.pdf"),
//                        Volume(8, "Tom 8", "450", R.drawable.four_4_8, "books/4/tm/four_4_8.pdf"),
//                        Volume(9, "Tom 9", "450", R.drawable.four_4_9, "books/4/tm/four_4_9.pdf"),
//                        Volume(10, "Tom 10", "450", R.drawable.four_4_10, "books/4/tm/four_4_10.pdf"),
//                        Volume(11, "Tom 11", "380", R.drawable.four_4_11, "books/4/tm/four_4_11.pdf")
//                    )
//                ),
//                // Russkiy - 3 toma
//                BookTranslation(
//                    language = "Русский",
//                    translator = "Gurbanguly Berdimuhamedow",
//                    coverImage = R.drawable.four_ru_4_1,
//                    pages = "1160", // 420 + 390 + 350
//                    volumes = listOf(
//                        Volume(1, "Tom 1", "450", R.drawable.four_ru_4_1, "books/4/ru/1.pdf"),
//                        Volume(2, "Tom 2", "450", R.drawable.four_ru_4_2, "books/4/ru/2.pdf"),
//                        Volume(3, "Tom 3", "450", R.drawable.four_ru_4_3, "books/4/ru/3.pdf"),
//                        Volume(4, "Tom 4", "450", R.drawable.four_ru_4_4, "books/4/ru/4.pdf"),
//                        Volume(5, "Tom 5", "450", R.drawable.four_ru_4_5, "books/4/ru/5.pdf"),
//                        Volume(6, "Tom 6", "450", R.drawable.four_ru_4_6, "books/4/ru/6.pdf"),
//                        Volume(7, "Tom 7", "450", R.drawable.four_ru_4_7, "books/4/ru/7.pdf"),
//                        Volume(8, "Tom 8", "450", R.drawable.four_ru_4_8, "books/4/ru/8.pdf"),
//                        Volume(9, "Tom 9", "450", R.drawable.four_ru_4_9, "books/4/ru/9.pdf"),
//                        Volume(10, "Tom 10", "450", R.drawable.four_ru_4_10, "books/4/ru/10.pdf"),
//                        Volume(11, "Tom 11", "380", R.drawable.four_ru_4_11, "books/4/ru/11.pdf")
//                    )
//                ),
//                // Angliyskiy - 2 toma
//                BookTranslation(
//                    language = "English",
//                    translator = "Gurbanguly Berdimuhamedow",
//                    coverImage = R.drawable.four_en_4_2,
//                    pages = "770", // 400 + 370
//                    volumes = listOf(
//                        Volume(2, "Tom 2", "450", R.drawable.four_en_4_2, "books/4/en/2.pdf"),
//                        Volume(3, "Tom 3", "450", R.drawable.four_en_4_3, "books/4/en/3.pdf"),
//                        Volume(4, "Tom 4", "450", R.drawable.four_en_4_4, "books/4/en/4.pdf"),
//                        Volume(5, "Tom 5", "450", R.drawable.four_en_4_5, "books/4/en/5.pdf"),
//                        Volume(6, "Tom 6", "450", R.drawable.four_en_4_6, "books/4/en/6.pdf"),
//                        Volume(7, "Tom 7", "450", R.drawable.four_en_4_7, "books/4/en/7.pdf"),
//                        Volume(8, "Tom 8", "450", R.drawable.four_en_4_8, "books/4/en/8.pdf"),
//                        Volume(9, "Tom 9", "450", R.drawable.four_en_4_9, "books/4/en/9.pdf"),
//                        Volume(10, "Tom 10", "450", R.drawable.four_en_4_10, "books/4/en/10.pdf"),
//                        Volume(11, "Tom 11", "380", R.drawable.four_en_4_11, "books/4/en/11.pdf")
//                    )
//                )
//            )
//
//        ))

        books.add(Book(
            id = 4,
            title = "Çaý Melhem hem Ylham",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_4,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_4,
                    pdfPath = "books/4/1.pdf"
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
                    coverImage = R.drawable.banner_5,
                    pdfPath = "books/5/1.pdf"
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
                    coverImage = R.drawable.banner_6,
                    pdfPath = "books/6/1.pdf"
                )
            )
        ))
        books.add(Book(
            id = 7,
            title = "Abadançylygyň röwşen gadamlary",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_7,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_7,
                    pdfPath = "books/7/1.pdf"
                )
            )
        ))

        books.add(Book(
            id = 8,
            title = "Ak Şäherim Aşgabat",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_8,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_8,
                    pdfPath = "books/8/1.pdf"
                )
            )
        ))

        books.add(Book(
            id = 9,
            title = "Änew müňýyllyklardan gözbaş alýan medeniýet",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_9,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_9,
                    pdfPath = "books/9/1.pdf"
                )
            )
        ))

        books.add(Book(
            id = 10,
            title = "Arşyň Nepisligi",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_10,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_10,
                    pdfPath = "books/10/1.pdf"
                )
            )
        ))

        books.add(Book(
            id = 11,
            title = " Bagtyýarlyk Saglykdan Başlanýar",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_11,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_11,
                    pdfPath = "books/11/1.pdf"
                )
            )
        ))

        books.add(Book(
            id = 12,
            title = "Bitarap Türkmenistan",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_12,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_12,
                    pdfPath = "books/12/1.pdf"
                )
            )
        ))

        books.add(Book(
            id = 13,
            title = "Döwlet adam üçindir",
            author = "Gurbanguly Berdimuhamedow",
            year = "2008",
            coverImage = R.drawable.banner_13,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_13,
                    pdfPath = "books/13/1.pdf"
                )
            )
        ))

        books.add(Book(
            id = 14,
            title = "Enä Tagzym - Mukaddeslige Tagzym",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_14,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_14,
                    pdfPath = "books/14/1.pdf"
                )
            )
        ))

        books.add(Book(
            id = 15,
            title = "Ganatly bedewler",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_15,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_15,
                    pdfPath = "books/15/1.pdf"
                )
            )
        ))

        books.add(Book(
            id = 16,
            title = " Garaşsyz,hemişelik Bitarap Türkmenistanyň Harby doktrinasy",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_16,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_16,
                    pdfPath = "books/16/1.pdf"
                )
            )
        ))
        books.add(Book(
            id = 17,
            title = "Garaşsyzlyga guwanmak,Watany,halky söýmek bagtdar",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_17,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_17,
                    pdfPath = "books/17/1.pdf"
                )
            )
        ))
        books.add(Book(
            id = 18,
            title = "Hakyda göwheri",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_18,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_18,
                    pdfPath = "books/18/1.pdf"
                )
            )
        ))

        books.add(Book(
            id = 19,
            title = "Ilе döwlet geler bolsa",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_19,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_19,
                    pdfPath = "books/19/1.pdf"
                )
            )
        ))

        books.add(Book(
            id = 20,
            title = "Ömrümiň manysynyň dowamaty",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_20,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_20,
                    pdfPath = "books/21/1.pdf"
                )
            )
        ))

        books.add(Book(
            id = 21,
            title = "Parahatçylyk Sazy, Dostluk, Doganlyk Sazy",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_21,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_21,
                    pdfPath = "books/21/1.pdf"
                )
            )
        ))

        books.add(Book(
            id = 22,
            title = "Türkmenistan - sagdynlygyň we ruhubelentligiň ýurdy 2007",
            author = "Gurbanguly Berdimuhamedow",
            year = "2007",
            coverImage = R.drawable.banner_22,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_22,
                    pdfPath = "books/22/1.pdf"
                )
            )
        ))

        books.add(Book(
            id = 23,
            title = "Türkmenistan Durnukly Ösüşiň Maksatlaryna Ýetmegiň Ýolunda",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_23,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_23,
                    pdfPath = "books/23/1.pdf"
                )
            )
        ))

        books.add(Book(
            id = 24,
            title = "Türkmenistanda saglygy goraýşy ösdürmediň ylmy esaslary",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_24,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_24,
                    pdfPath = "books/24/1.pdf"
                )
            )
        ))

        books.add(Book(
            id = 25,
            title = "Türkmenistan-sagdynlygyň we ruhubelentligiň ýurdy",
            author = "Gurbanguly Berdimuhamedow",
            year = "2008",
            coverImage = R.drawable.banner_25,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_25,
                    pdfPath = "books/25/1.pdf"
                )
            )
        ))

        books.add(Book(
            id = 26,
            title = "Türkmenistanyň Beýik Galkynyş eýýamynyň Konstitusiýasy hakynda",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_26,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_26,
                    pdfPath = "books/26/1.pdf"
                )
            )
        ))

        books.add(Book(
            id = 27,
            title = "Türkmenistanyň Bitaraplygy parahatçylygyň we ösüşiň syýasaty",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.placeholder,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.placeholder,
                    pdfPath = "books/27/1.pdf"
                )
            )
        ))

        books.add(Book(
            id = 28,
            title = "Türkmenistanyň durmuş-ykdysady ösüşiniň döwlet kadalaşdyrylyşy",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_28_1,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_28_1,
                    volumes = listOf(
                                Volume(1, "Tom 1", R.drawable.banner_28_1, "books/28/1.pdf"),
                                Volume(2, "Tom 2", R.drawable.banner_28_2, "books/28/2.pdf")
                    )
                )
            )
        ))

        books.add(Book(
            id = 29,
            title = "Türkmenistanyň ykdysady strategiýasy halka daýanyp, halkyň hatyrasyna",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_29,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_29,
                    pdfPath = "books/29/1.pdf"
                )
            )
        ))

        books.add(Book(
            id = 30,
            title = "Тürkmenistan- melhemler mekany",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_30,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_30,
                    pdfPath = "books/30/1.pdf"
                )
            )
        ))

        books.add(Book(
            id = 31,
            title = "Atda wepa-da bar, sapa-da",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_31,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_31,
                    pdfPath = "books/31/1.pdf"
                )
            )
        ))

        books.add(Book(
            id = 32,
            title = "Bilim-bagtyýarlyk ruhubelentlik, rowaçlyk",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_32,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_32,
                    pdfPath = "books/32/1.pdf"
                )
            )
        ))

        books.add(Book(
            id = 33,
            title = "Gadamy batly bedew",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_33,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_33,
                    pdfPath = "books/33/1.pdf"
                )
            )
        ))

        books.add(Book(
            id = 34,
            title = "Garaşsyz baky Bitarap Türkmenistan",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_34,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_34,
                    pdfPath = "books/34/1.pdf"
                )
            )
        ))

        books.add(Book(
            id = 35,
            title = "Janly Rowaýat",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_35,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_35,
                    pdfPath = "books/35/1.pdf"
                )
            )
        ))

        books.add(Book(
            id = 36,
            title = "Magtymguly - dünýäniň akyldary",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_36,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_36,
                    pdfPath = "books/36/1.pdf"
                )
            )
        ))

        books.add(Book(
            id = 37,
            title = "Medeniýet halkyň kalbydyr",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_37,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_37,
                    pdfPath = "books/37/1.pdf"
                )
            )
        ))

        books.add(Book(
            id = 38,
            title = "Mert ýigitler gaýrat üçin dogulýar",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_38,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_38,
                    pdfPath = "books/38/1.pdf"
                )
            )
        ))

        books.add(Book(
            id = 39,
            title = "Mertler Watany beýgeldýär",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_39,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_39,
                    pdfPath = "books/39/1.pdf"
                )
            )
        ))

        books.add(Book(
            id = 40,
            title = "Sport Dostluga, Saglyga we Gözellige Tarap Ýoldur",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_40,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_40,
                    pdfPath = "books/40/1.pdf"
                )
            )
        ))

        books.add(Book(
            id = 41,
            title = "Türkmen alabaýy",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_41,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_41,
                    pdfPath = "books/41/1.pdf"
                )
            )
        ))

        books.add(Book(
            id = 42,
            title = "Türkmen medeniýeti",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_42,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_42,
                    pdfPath = "books/42/1.pdf"
                )
            )
        ))

        books.add(Book(
            id = 43,
            title = "Türkmenistan - abadançylygyň we rowaçlygyň ýurdy",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_43,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_43,
                    pdfPath = "books/43/1.pdf"
                )
            )
        ))

        books.add(Book(
            id = 44,
            title = " Türkmenistan - Beýik Ýüpek ýolunyň ýüregi",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_44_1,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_44_1,
                    volumes = listOf(
                        Volume(1, "Tom 1", R.drawable.banner_44_1, "books/44/1.pdf"),
                        Volume(2, "Tom 2", R.drawable.banner_44_2, "books/44/2.pdf")
                    )
                )
            )
        ))


        books.add(Book(
            id = 45,
            title = "Türkmenistan",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_45,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_45,
                    pdfPath = "books/45/1.pdf"
                )
            )
        ))

        books.add(Book(
            id = 46,
            title = "Watan goragy mukaddesdir",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_46,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_46,
                    pdfPath = "books/46/1.pdf"
                )
            )
        ))

        books.add(Book(
            id = 47,
            title = "Ynsan kalbynyň öçmejek nury",
            author = "Gurbanguly Berdimuhamedow",
            year = "2001",
            coverImage = R.drawable.banner_47,
            translations = listOf(
                BookTranslation(
                    language = "Türkmen",
                    translator = null,
                    coverImage = R.drawable.banner_47,
                    pdfPath = "books/47/1.pdf"
                )
            )
        ))









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