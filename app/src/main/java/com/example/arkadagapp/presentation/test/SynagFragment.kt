package com.example.arkadagapp.presentation.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.arkadagapp.R
import com.example.arkadagapp.presentation.test.question.QuestionFragment

class SynagFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_synag, container, false)

        val btnStart: Button = view.findViewById(R.id.btn_start_test)

        btnStart.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, QuestionFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}