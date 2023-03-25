package com.example.quizgame.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quizgame.data.Question
import com.example.quizgame.data.questionBank
import com.example.quizgame.ui.GameUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int> = _score

    private val _question = MutableLiveData<Question>()
    val question: LiveData<Question> = _question

    private val _askedQuestions = mutableSetOf<Int>()

    init {
        resetGame()
        getNextQuestion()
    }

    fun clickCount(){
        val updatedClick = _uiState.value.clickTime.plus(1)
        _uiState.update { currentState ->
            currentState.copy(
                clickTime = updatedClick
            )
        }

    }

    private fun getNextQuestion() {
        val unansweredQuestions = questionBank.filter { ! _askedQuestions.contains(questionBank.indexOf(it)) }
        if (unansweredQuestions.isNotEmpty()) {
            val randomIndex = (unansweredQuestions.indices ).random()
            val updatedQuestionAsked = _uiState.value.numQuestionsAsked.plus(1)
            _question.value = unansweredQuestions[randomIndex]

            _askedQuestions.add(questionBank.indexOf(_question.value))
            _uiState.update { currentState ->
                currentState.copy(
                    numQuestionsAsked = updatedQuestionAsked
                )
            }
        }

    }

    fun submitAnswer(answerIndex: Int) {
        if (answerIndex == _question.value?.answerIndex) {
            _score.value = (_score.value ?: 0) + 1
        }
        getNextQuestion()
    }

    fun isGameOver(): Boolean {
        return questionBank.size == _uiState.value.clickTime;
    }

    fun resetGame() {
        _score.value = 0
        _askedQuestions.clear()
        _uiState.update { currentState ->
            currentState.copy(
                numQuestionsAsked = 0,
                clickTime = 0
            )
        }
    }




}

