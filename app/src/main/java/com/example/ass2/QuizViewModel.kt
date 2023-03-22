package com.example.ass2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class Question(
    val question: String,
    val options: List<String>,
    val answerIndex: Int
)

val questionBank = listOf(
    Question(
        "What is the capital of France?",
        listOf("Paris", "Berlin", "Madrid", "Rome"),
        0
    ),
    Question(
        "What is the tallest mammal?",
        listOf("Elephant", "Giraffe", "Hippopotamus", "Lion"),
        1
    ),
    Question(
        "3",
        listOf("3", "Giraffe", "Hippopotamus", "Lion"),
        1
    ),
    Question(
        "4",
        listOf("4", "Giraffe", "Hippopotamus", "Lion"),
        1
    ),
    Question(
        "5",
        listOf("5", "Giraffe", "Hippopotamus", "Lion"),
        1
    ),
    Question(
        "6?",
        listOf("6", "Giraffe", "Hippopotamus", "Lion"),
        1
    ),
    Question(
        "7?",
        listOf("7", "Giraffe", "Hippopotamus", "Lion"),
        1
    ),
    Question(
        "8?",
        listOf("8", "Giraffe", "Hippopotamus", "Lion"),
        1
    ),
    Question(
        "9?",
        listOf("9", "Giraffe", "Hippopotamus", "Lion"),
        1
    ),
    Question(
        "10",
        listOf("10", "Giraffe", "Hippopotamus", "Lion"),
        1
    ),
)


class QuizViewModel : ViewModel() {
    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int> = _score

    private val _question = MutableLiveData<Question>()
    val question: LiveData<Question> = _question

    private val _askedQuestions = mutableSetOf<Int>()
    private var numQuestionsAsked = 0
    private var clickTime = 0;

    init {
        resetGame()
        getNextQuestion()
    }

    fun clickCount(){
        clickTime++
    }

    fun resetGame() {
        _score.value = 0
        _askedQuestions.clear()
        numQuestionsAsked = 0
        clickTime = 0
    }

    private fun getNextQuestion() {
        val unansweredQuestions = questionBank.filter { ! _askedQuestions.contains(questionBank.indexOf(it)) }
        if (unansweredQuestions.isNotEmpty()) {
            val randomIndex = (unansweredQuestions.indices ).random()
            _question.value = unansweredQuestions[randomIndex]
            numQuestionsAsked++
            _askedQuestions.add(questionBank.indexOf(_question.value))
        }

    }

    fun submitAnswer(answerIndex: Int) {
        if (answerIndex == _question.value?.answerIndex) {
            _score.value = (_score.value ?: 0) + 1
        }
        getNextQuestion()
    }

    fun isGameOver(): Boolean {
        return questionBank.size == clickTime;
    }

}
