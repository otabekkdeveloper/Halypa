package com.example.arkadagapp.utils

import com.example.arkadagapp.model.Question

object QuestionManager {

    private val questionsTemplate = listOf(
        Question(1, "Konstitutsiýa", "Türkmenistanyň Konstitutsiýasy ilkinji gezek haçan kabul edildi?",
            listOf("1992-nji ýylyň 18-nji maýynda", "1991-nji ýylyň 18-nji maýynda", "1993-nji ýylyň 18-nji maýynda", "1990-nji ýylyň 18-nji maýynda"), 0),

        Question(2, "Konstitutsiýa", "Türkmenistanyň döwlet dili haýsy?",
            listOf("Türkmen dili", "Rus dili", "Iňlis dili", "Pars dili"), 0),

        Question(3, "Konstitutsiýa", "Türkmenistanyň paýtagty?",
            listOf("Aşgabat", "Mary", "Daşoguz", "Türkmenabat"), 0),

        Question(4, "Taryh", "Garaşsyzlyk güni haçan bellenilýär?",
            listOf("27-nji oktýabr", "12-nji fewral", "18-nji maý", "6-njy oktýabr"), 0),

        Question(5, "Geografiya", "Türkmenistanyň meydany nace?",
            listOf("491,2", "324,4", "123,5", "764,7"), 0),

        Question(6, "Geografiýa", "Türkmenistan haýsy yklymda ýerleşýär?",
            listOf("Merkezi Aziýa", "Gündogar Ýewropa", "Günorta Aziýa", "Ýakyn Gündogar"), 0),

        Question(7, "Geografiýa", "Türkmenistanyň iň beýik dagy haýsy?",
            listOf("Aýrybaba", "Köpetdag", "Balkandag", "Garagum"), 0),

        Question(8, "Edebiýat", "Magtymguly Pyragy haýsy asyryň şahyry?",
            listOf("XVIII asyr", "XVII asyr", "XIX asyr", "XX asyr"), 0),

        Question(9, "Edebiýat", "Görogly destany näme hakynda?",
            listOf("Gahrymançylyk we adalat", "Söweş", "Söýgi", "Tebigat"), 0),

        Question(10, "Ylym", "Türkmenistanyň Ylymlar akademiýasy haçan döredildi?",
            listOf("1951-nji ýylda", "1941-nji ýylda", "1961-nji ýylda", "1971-nji ýylda"), 0),

        Question(11, "Himiya Ylym", "Türkmenistanyň Ylymlar sfsefsefse",
            listOf("1951-nji ýylda", "1941-nji ýylda", "1961-nji ýylda", "1971-nji ýylda"), 0),
    )


    private fun shuffleAnswers(question: Question): Question {
        val answersWithIndex = question.answers.mapIndexed { index, answer ->
            answer to (index == question.correctAnswerIndex)
        }

        val shuffled = answersWithIndex.shuffled()
        val newAnswers = shuffled.map { it.first }
        val newCorrectIndex = shuffled.indexOfFirst { it.second }

        return question.copy(
            answers = newAnswers,
            correctAnswerIndex = newCorrectIndex
        )
    }

    fun getRandomQuestions(count: Int = 10): List<Question> {
        return questionsTemplate
            .shuffled()
            .take(count)
            .map { shuffleAnswers(it) }
    }
}