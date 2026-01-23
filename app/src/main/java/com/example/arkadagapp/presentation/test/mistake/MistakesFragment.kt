package com.example.arkadagapp.presentation.test.mistake

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
import com.example.arkadagapp.model.WrongQuestion
import com.google.android.material.bottomnavigation.BottomNavigationView

class MistakesFragment : Fragment() {

    private lateinit var btnBack: ImageView
    private lateinit var mistakesRecycler: RecyclerView
    private var wrongQuestions = ArrayList<WrongQuestion>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            wrongQuestions = it.getParcelableArrayList(ARG_WRONG_QUESTIONS) ?: ArrayList()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mistakes, container, false)

        btnBack = view.findViewById(R.id.btn_back)
        mistakesRecycler = view.findViewById(R.id.mistakes_recycler)

        mistakesRecycler.layoutManager = LinearLayoutManager(context)
        mistakesRecycler.adapter = MistakesAdapter(wrongQuestions)

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }



        return view
    }

    override fun onPause() {
        super.onPause()
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.GONE
    }

    companion object {
        private const val ARG_WRONG_QUESTIONS = "wrong_questions"

        fun newInstance(wrongQuestions: ArrayList<WrongQuestion>) =
            MistakesFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_WRONG_QUESTIONS, wrongQuestions)
                }
            }
    }
}

// Adapter
class MistakesAdapter(
    private val mistakes: List<WrongQuestion>
) : RecyclerView.Adapter<MistakesAdapter.MistakeViewHolder>() {

    class MistakeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val questionNumber: TextView = view.findViewById(R.id.question_number)
        val questionText: TextView = view.findViewById(R.id.question_text)
        val userAnswer: TextView = view.findViewById(R.id.user_answer)
        val correctAnswer: TextView = view.findViewById(R.id.correct_answer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MistakeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mistake, parent, false)
        return MistakeViewHolder(view)
    }

    override fun onBindViewHolder(holder: MistakeViewHolder, position: Int) {
        val mistake = mistakes[position]

        holder.questionNumber.text = "Sorag ${mistake.questionNumber}"
        holder.questionText.text = mistake.question.question
        holder.userAnswer.text = mistake.question.answers[mistake.userAnswerIndex]
        holder.correctAnswer.text = mistake.question.answers[mistake.question.correctAnswerIndex]
    }




    override fun getItemCount() = mistakes.size
}