package com.example.arkadagapp.presentation.settings.favoriteQuotesFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.arkadagapp.R
import com.example.arkadagapp.presentation.quote.adapter.QuotesAdapter
import com.example.arkadagapp.utils.QuotesManager

class FavoriteQuotesFragment : Fragment() {

    private lateinit var quotesRecycler: RecyclerView
    private lateinit var quotesAdapter: QuotesAdapter
    private lateinit var emptyView: TextView
    private lateinit var backButton: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite_quotes, container, false)

        QuotesManager.init(requireContext())

        backButton = view.findViewById(R.id.back_button)
        quotesRecycler = view.findViewById(R.id.quotes_recycler)
        emptyView = view.findViewById(R.id.empty_view)

        quotesRecycler.layoutManager = LinearLayoutManager(context)

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        loadFavorites()

        return view
    }

    private fun loadFavorites() {
        val favorites = QuotesManager.getFavoriteQuotes()

        if (favorites.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            quotesRecycler.visibility = View.GONE
        } else {
            emptyView.visibility = View.GONE
            quotesRecycler.visibility = View.VISIBLE

            quotesAdapter = QuotesAdapter(favorites) {
                loadFavorites()
            }
            quotesRecycler.adapter = quotesAdapter
        }
    }
}