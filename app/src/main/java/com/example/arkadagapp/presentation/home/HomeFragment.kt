package com.example.arkadagapp.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
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
    private lateinit var searchView: SearchView
    private var books = mutableListOf<Book>()
    private var filteredBooks = mutableListOf<Book>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.books_recycler_view)
        searchView = view.findViewById(R.id.search_view)

        // Grid Layout s 2 kolonkami
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        // Zagruzhaem knigi
        books.clear()
        loadBooks()
        filteredBooks.addAll(books)

        // Adapter s klikom
        bookAdapter = BookAdapter(filteredBooks) { book ->
            openBookDetail(book)
        }
        recyclerView.adapter = bookAdapter

        // POISK cherez SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterBooks(newText ?: "")
                return true
            }
        })

        return view
    }

    private fun filterBooks(query: String) {
        filteredBooks.clear()

        if (query.isEmpty()) {
            filteredBooks.addAll(books)
        } else {
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