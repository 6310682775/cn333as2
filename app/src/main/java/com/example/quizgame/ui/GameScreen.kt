package com.example.quizgame.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
//import com.example.quizgame.data.Question
//import com.example.quizgame.data.questionBank
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview


import androidx.lifecycle.ViewModel
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quizgame.R
import com.example.quizgame.data.Question

import com.example.quizgame.ui.theme.QuizGameTheme
import kotlin.reflect.KProperty
import androidx.lifecycle.ViewModel as ViewModel1

sealed class Screen(val route: String){
    object Home: Screen("home")
    object QuizScreen: Screen("quizScreen")
    object GameOver: Screen("gameOver")
}

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    //gameViewModel: GameViewModel = viewModel()
    gameViewModel: GameViewModel = viewModel()

) {
    val question: Question? by gameViewModel.question.observeAsState(null)
    val score: Int? by gameViewModel.score.observeAsState(null)
    val navController = rememberNavController()
    NavHost(navController, startDestination = "home") {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.QuizScreen.route) {
            QuizScreen(navController = navController, question = question,
                score = score ?: 0
            ){ answerIndex ->
                gameViewModel.submitAnswer(answerIndex)
            }
        }
        composable(Screen.GameOver.route) {
            GameOverScreen(navController = navController, score = score ?: 0)
        }
    }
}



@Composable
fun HomeScreen(navController: NavController, modifier: Modifier = Modifier) {
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
            shuffledOptions.forEachIndexed { index, option ->
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
        Text(
            text = "Score: $score",
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(top = 16.dp)
        )
    }


}


@Composable
private fun GameOverScreen(gameViewModel: GameViewModel = viewModel(),navController: NavController, score: Int) {
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
                gameViewModel.resetGame()
                navController.navigate("quizScreen")
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Play Again")
        }
    }
}

@Composable
fun AnswerOption(modifier: Modifier = Modifier,
                 gameViewModel: GameViewModel = viewModel(),option: String, isSelected: Boolean, isEnabled: Boolean, onClick: () -> Unit) {
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
