package com.example.ass2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.collectAsState



import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ass2.ui.theme.Ass2Theme


sealed class Screen(val route: String){
    object Home: Screen("home")
    object QuizScreen: Screen("quizScreen")
    object GameOver: Screen("gameOver")
}

class MainActivity : ComponentActivity() {
    private val viewModel: QuizViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Ass2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val question: Question? by viewModel.question.observeAsState(null)
                    val score: Int? by viewModel.score.observeAsState(null)
                    val navController = rememberNavController()
                    NavHost(navController, startDestination = "home") {
                        composable(Screen.Home.route) {
                            HomeScreen(navController = navController)
                        }
                        composable(Screen.QuizScreen.route) {
                            QuizScreen(navController = navController, question,score ?: 0){ answerIndex ->
                                viewModel.submitAnswer(answerIndex)
                            }
                        }
                        composable(Screen.GameOver.route) {
                            GameOverScreen(navController = navController, score ?: 0)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun HomeScreen(navController: NavController, modifier: Modifier = Modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(align = Alignment.Center)
        ) {
            Button(onClick = { navController.navigate("quizScreen") }) {
                Text(text = "Start Game")
            }
        }


    }


    @Composable
    fun QuizScreen(navController: NavController,question: Question?, score: Int, onAnswerSelected: (Int) -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
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
                            viewModel.clickCount()
                            if (viewModel.isGameOver()) {
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
    fun GameOverScreen(navController: NavController,score: Int) {
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
                    viewModel.resetGame()
                    navController.navigate("quizScreen")
                    },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Play Again")
            }
        }
    }

@Composable
fun AnswerOption(option: String, isSelected: Boolean, isEnabled: Boolean, onClick: () -> Unit) {
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

}


