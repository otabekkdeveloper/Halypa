package com.example.arkadagapp.presentation.quote

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.arkadagapp.R
import com.example.arkadagapp.presentation.quote.adapter.QuotesAdapter
import com.example.arkadagapp.utils.QuotesManager

class QuoteFragment : Fragment() {

    private lateinit var searchInput: EditText
    private lateinit var clearIcon: ImageView
    private lateinit var quotesRecycler: RecyclerView
    private lateinit var quotesAdapter: QuotesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_quote, container, false)

        QuotesManager.init(requireContext())

        searchInput = view.findViewById(R.id.search_input)
        clearIcon = view.findViewById(R.id.clear_icon)
        quotesRecycler = view.findViewById(R.id.quotes_recycler)

        quotesRecycler.layoutManager = LinearLayoutManager(context)

        quotesAdapter = QuotesAdapter(QuotesManager.getAllQuotes())
        quotesRecycler.adapter = quotesAdapter

        setupSearch()

        return view
    }

    private fun setupSearch() {
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()

                if (query.isNotEmpty()) {
                    clearIcon.visibility = View.VISIBLE
                    val filtered = QuotesManager.searchQuotes(query)
                    quotesAdapter.updateQuotes(filtered)
                } else {
                    clearIcon.visibility = View.GONE
                    quotesAdapter.updateQuotes(QuotesManager.getAllQuotes())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        clearIcon.setOnClickListener {
            searchInput.text.clear()
        }
    }
}