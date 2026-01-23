package com.example.arkadagapp.presentation.test

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.arkadagapp.R
import com.example.arkadagapp.R.color.ic_checked
import com.example.arkadagapp.model.Question
import com.example.arkadagapp.model.WrongQuestion
import com.example.arkadagapp.utils.QuestionManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class QuestionFragment : Fragment() {

    private lateinit var btnClose: ImageView
    private lateinit var testProgress: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var questionText: TextView
    private lateinit var answersGroup: RadioGroup
    private lateinit var btnNext: Button

    private var questions = listOf<Question>()
    private var currentIndex = 0
    private var correctCount = 0
    private val wrongQuestions = mutableListOf<WrongQuestion>()
    private var selectedIndex = -1
    private var hasAnswered = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_question, container, false)

        btnClose = view.findViewById(R.id.btn_close)
        testProgress = view.findViewById(R.id.test_progress)
        progressBar = view.findViewById(R.id.progress_bar)
        questionText = view.findViewById(R.id.question_text)
        answersGroup = view.findViewById(R.id.answers_group)
        btnNext = view.findViewById(R.id.btn_next)

        questions = QuestionManager.getRandomQuestions(10)

        setupListeners()
        showQuestion()



        return view
    }


    override fun onPause() {
        super.onPause()
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility =
            View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.GONE
    }

    private fun setupListeners() {
        btnClose.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnNext.setOnClickListener {
            if (hasAnswered) {
                nextQuestion()
            } else {
                checkAnswer()
            }
        }
    }

    private fun updateNextButtonState(isEnabled: Boolean) {
        if (isEnabled) {
            // Синяя кнопка - ответ выбран
            btnNext.isEnabled = true
            btnNext.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.blue)
            )
        } else {
            // Серая кнопка - ответ не выбран
            btnNext.isEnabled = false
            btnNext.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.gray)
            )
        }
    }

    private fun showQuestion() {
        val question = questions[currentIndex]

        testProgress.text = "Sorag ${currentIndex + 1}/${questions.size}"
        progressBar.progress = currentIndex + 1
        questionText.text = question.question

        answersGroup.removeAllViews()
        selectedIndex = -1
        hasAnswered = false
        btnNext.isEnabled = false
        btnNext.text = "Öňe"

        updateNextButtonState(false)

        question.answers.forEachIndexed { index, answer ->
            val radioButton = layoutInflater.inflate(
                R.layout.item_answer, answersGroup, false
            ) as RadioButton

            radioButton.id = View.generateViewId()
            radioButton.text = answer
            radioButton.tag = index

            radioButton.setOnClickListener {
                if (!hasAnswered) {
                    selectedIndex = index
                    btnNext.isEnabled = true

                    updateNextButtonState(true)
                }
            }
            answersGroup.addView(radioButton)
        }
    }

    private fun checkAnswer() {
        if (selectedIndex == -1) return

        hasAnswered = true
        val question = questions[currentIndex]
        val isCorrect = selectedIndex == question.correctAnswerIndex

        if (isCorrect) {
            correctCount++
        } else {
            wrongQuestions.add(
                WrongQuestion(
                    questionNumber = currentIndex + 1,
                    question = question,
                    userAnswerIndex = selectedIndex
                )
            )
        }

        // Показать результат
        for (i in 0 until answersGroup.childCount) {
            val radio = answersGroup.getChildAt(i) as RadioButton
            val index = radio.tag as Int

            radio.isEnabled = false

            when {
                index == question.correctAnswerIndex -> {
                    radio.setBackgroundResource(R.drawable.bg_answer_correct)
                }

                index == selectedIndex && !isCorrect -> {
                    radio.setBackgroundResource(R.drawable.bg_answer_wrong)
                }
            }
        }

        btnNext.text = if (currentIndex < questions.size - 1) {
            "Öňe"
        } else {
            "Gutarmak"
        }
    }

    private fun nextQuestion() {
        if (currentIndex < questions.size - 1) {
            currentIndex++
            showQuestion()
        } else {
            showResults()
        }
    }

    private fun showResults() {
        val percentage = (correctCount * 100) / questions.size

        val fragment = ResultFragment.newInstance(
            totalQuestions = questions.size,
            correctAnswers = correctCount,
            wrongAnswers = wrongQuestions.size,
            percentage = percentage,
            wrongQuestions = ArrayList(wrongQuestions)
        )

        parentFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
    }
}