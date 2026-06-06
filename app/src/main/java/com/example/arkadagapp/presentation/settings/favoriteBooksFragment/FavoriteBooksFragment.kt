package com.example.arkadagapp.presentation.settings.favoriteBooksFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.arkadagapp.R
import com.example.arkadagapp.presentation.settings.favoriteBooksFragment.adapter.FavoriteBooksAdapter
import com.example.arkadagapp.utils.BookmarkManager
import com.example.arkadagapp.utils.LikeManager

class FavoriteBooksFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var likeManager: LikeManager
    private lateinit var bookmarkManager: BookmarkManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite_books, container, false)

        recyclerView = view.findViewById(R.id.favorite_books_recycler)
        likeManager = LikeManager(requireContext())
        bookmarkManager = BookmarkManager(requireContext())

        recyclerView.layoutManager = LinearLayoutManager(context)

        loadFavoriteBooks()

        view.findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }

    private fun loadFavoriteBooks() {
        val likedBooks = bookmarkManager.getAllBooksProgress()
            .filter { likeManager.isLiked(it.bookId) }

        val adapter = FavoriteBooksAdapter(likedBooks) { bookProgress ->
        }
        recyclerView.adapter = adapter
    }
}