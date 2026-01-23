package com.example.arkadagapp.presentation.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.arkadagapp.R
import com.example.arkadagapp.model.WrongQuestion
import com.example.arkadagapp.presentation.test.mistake.MistakesFragment

class ResultFragment : Fragment() {

    private lateinit var percentageText: TextView
    private lateinit var resultProgress: ProgressBar
    private lateinit var correctCount: TextView
    private lateinit var wrongCount: TextView
    private lateinit var btnShowMistakes: Button
    private lateinit var btnBackToTests: Button

    private var totalQuestions = 0
    private var correctAnswers = 0
    private var wrongAnswers = 0
    private var percentage = 0
    private var wrongQuestions = ArrayList<WrongQuestion>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            totalQuestions = it.getInt(ARG_TOTAL)
            correctAnswers = it.getInt(ARG_CORRECT)
            wrongAnswers = it.getInt(ARG_WRONG)
            percentage = it.getInt(ARG_PERCENTAGE)
            wrongQuestions = it.getParcelableArrayList(ARG_WRONG_QUESTIONS) ?: ArrayList()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_result, container, false)

        percentageText = view.findViewById(R.id.percentage_text)
        resultProgress = view.findViewById(R.id.result_progress)
        correctCount = view.findViewById(R.id.correct_count)
        wrongCount = view.findViewById(R.id.wrong_count)
        btnShowMistakes = view.findViewById(R.id.btn_show_mistakes)
        btnBackToTests = view.findViewById(R.id.btn_back_to_tests)

        setupUI()
        setupListeners()

        return view
    }

    private fun setupUI() {
        percentageText.text = "$percentage%"
        resultProgress.progress = percentage
        correctCount.text = correctAnswers.toString()
        wrongCount.text = wrongAnswers.toString()

        // Если нет ошибок - скрыть кнопку
        if (wrongQuestions.isEmpty()) {
            btnShowMistakes.visibility = View.GONE
        }
    }

    private fun setupListeners() {
        btnShowMistakes.setOnClickListener {
            val fragment = MistakesFragment.newInstance(wrongQuestions)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        btnBackToTests.setOnClickListener {
            // Очистить весь back stack и вернуться к началу
            parentFragmentManager.popBackStack(
                null,
                androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        }
    }

    companion object {
        private const val ARG_TOTAL = "total"
        private const val ARG_CORRECT = "correct"
        private const val ARG_WRONG = "wrong"
        private const val ARG_PERCENTAGE = "percentage"
        private const val ARG_WRONG_QUESTIONS = "wrong_questions"

        fun newInstance(
            totalQuestions: Int,
            correctAnswers: Int,
            wrongAnswers: Int,
            percentage: Int,
            wrongQuestions: ArrayList<WrongQuestion>
        ) = ResultFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_TOTAL, totalQuestions)
                putInt(ARG_CORRECT, correctAnswers)
                putInt(ARG_WRONG, wrongAnswers)
                putInt(ARG_PERCENTAGE, percentage)
                putParcelableArrayList(ARG_WRONG_QUESTIONS, wrongQuestions)
            }
        }
    }
}