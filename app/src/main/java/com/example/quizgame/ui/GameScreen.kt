package com.example.quizgame.ui


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview



import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quizgame.R
import com.example.quizgame.data.Question

import com.example.quizgame.ui.theme.QuizGameTheme


sealed class Screen(val route: String){
    object Home: Screen("home")
    object QuizScreen: Screen("quizScreen")
    object GameOver: Screen("gameOver")
}

@Composable
fun GameScreen(
    gameViewModel: GameViewModel = viewModel()
) {

    val gameUiState by gameViewModel.uiState.collectAsState()
    val question: Question? by gameViewModel.question.observeAsState(null)
    val score: Int? by gameViewModel.score.observeAsState(null)
    val navController = rememberNavController()

    if (!gameUiState.isFinished){

        NavHost(navController, startDestination = "home") {
            composable(Screen.Home.route) {
                HomeScreen(navController = navController)
            }

            composable(Screen.QuizScreen.route) {
                GameStatus(questionCount = gameUiState.clickTime,score = score ?: 0)

                QuizScreen(navController = navController, question = question,
                    score = score ?: 0
                ){ answerIndex ->
                    gameViewModel.submitAnswer(answerIndex)
                }
            }
            composable(Screen.GameOver.route) {
                GameOverScreen(navController = navController, score = score ?: 0,onPlayAgain = {
                    gameViewModel.resetGame()
                })
            }
        }
    }
    else{
        GameOverScreen(navController = navController, score = score ?: 0,
        onPlayAgain = {
            gameViewModel.resetGame()
        }
    )}
}



@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .wrapContentSize(align = Alignment.Center)
    )
    {
        Spacer(Modifier.height(200.dp))
        Image(
            painter = painterResource(id = R.drawable.party),
            contentDescription = stringResource(id = R.string.party_content_description)
        )
        Spacer(Modifier.height(40.dp))

        Button(onClick = { navController.navigate("quizScreen") }, modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(align = Alignment.Center)) {
            Text(text = "Start Game")
        }
    }


}

@Composable
fun GameStatus(
    questionCount: Int,
    score: Int,
    modifier: Modifier = Modifier,
) {
    Spacer(modifier.height(100.dp))
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(30.dp)
            .size(64.dp),
    ) {
        Text(
            text = stringResource(R.string.question_count, questionCount ),
            fontSize = 18.sp,
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End),
            text = "Score: $score",
            fontSize = 18.sp
        )
    }
}

@Composable
fun QuizScreen(gameViewModel: GameViewModel = viewModel()
               , navController: NavController, question: Question?, score: Int, onAnswerSelected: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Image(

            painter = painterResource(id = R.drawable.party),
            contentDescription = stringResource(id = R.string.party_content_description)
        )
        Spacer(Modifier.height(30.dp))

        if (question != null) {
            Text(
                text = question.question,
                style = MaterialTheme.typography.h4,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            val shuffledOptions = question.options.shuffled() // Shuffle the options
            shuffledOptions.forEachIndexed { _, option ->
                AnswerOption(
                    option = option,
                    isSelected = false,
                    isEnabled = true,
                    onClick = {
                        onAnswerSelected(question.options.indexOf(option))
                        gameViewModel.clickCount()
                        if (gameViewModel.isGameOver()) {
                            navController.navigate("gameOver")
                        }
                    }
                )
            }
        }

    }


}


@Composable
private fun GameOverScreen(navController: NavController, score: Int, onPlayAgain: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Game Over!",
            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Your score is $score",
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(
            onClick = {
                onPlayAgain()
                navController.navigate("quizScreen")
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Play Again")
        }
    }
}

@Composable
fun AnswerOption(
                 option: String, isSelected: Boolean, isEnabled: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = null,
            modifier = Modifier.clickable(enabled = isEnabled, onClick = onClick)
        )
        Text(
            text = option,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    QuizGameTheme {
        GameScreen()
    }
}
