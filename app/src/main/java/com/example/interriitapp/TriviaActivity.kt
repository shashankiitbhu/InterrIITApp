package com.example.interriitapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.interriitapp.Models.TriviaQuestion
import com.example.interriitapp.Network.TriviaResponse
import com.example.interriitapp.Network.createTriviaService
import com.example.interriitapp.ui.theme.InterrIITAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.awaitResponse
import kotlin.math.absoluteValue
import kotlin.math.pow

class TriviaActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InterrIITAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val triviaQuestions = remember{mutableStateOf(listOf<TriviaQuestion>())}
                    val diff = remember{ mutableStateOf("") }
                    val num = remember{ mutableStateOf(1) }
                    val makeCall = remember{ mutableStateOf(false) }
                    val showCards = remember{ mutableStateOf(false) }
                    val apiCall = createTriviaService()
                    val showProgressBar = remember{ mutableStateOf(false) }

                    val response = apiCall.getTriviaQuestions(num.value,18,""+diff.value,"multiple")
                    if(makeCall.value){
                        LaunchedEffect(Unit){
                            showProgressBar.value = true
                            val result = response.awaitResponse()

                            if(result.isSuccessful){
                                triviaQuestions.value = result.body()!!.results
                                showCards.value = true
                                showProgressBar.value = false
                            }else{
                                Toast.makeText(this@TriviaActivity, "Error", Toast.LENGTH_SHORT).show()
                                showProgressBar.value = false
                            }
                        }
                    }
                    Scaffold(

                    ){
                        if(showCards.value){
                            Column(modifier = Modifier
                                .fillMaxSize()
                                .padding(it)) {
                                TriviaGame(questions = triviaQuestions.value)
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(onClick = {
                                    showCards.value = false
                                    showProgressBar.value = false
                                },
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .align(Alignment.CenterHorizontally)) {
                                    Text(text = "Reset Trivia")
                                }
                            }

                        }else{
                            if(showProgressBar.value){
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                                    CircularProgressIndicator()
                                }
                            }else{

                                Column(modifier = Modifier
                                    .fillMaxSize()
                                    .padding(it)) {
                                    OutlinedCard(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        shape = MaterialTheme.shapes.medium,
                                        elevation = CardDefaults.cardElevation(4.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surface,
                                            contentColor = MaterialTheme.colorScheme.onSurface
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp)
                                        ) {
                                            Text(
                                                text = "Create Your Own Trivia",
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            )

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Text(
                                                text = "Select the difficulty and number of questions you want to play with. Do you think you know it all? Check it out!",
                                                fontSize = 16.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(24.dp))
                                    TriviaOptionsInput(onSearchClicked = {difficulty, input ->
                                        diff.value = difficulty
                                        num.value = input
                                        makeCall.value = true
                                    } )

                                }
                            }
                        }
                    }


                }
            }
        }
    }
}
@Composable
fun TriviaCard(
    modifier: Modifier ,
    question: TriviaQuestion,
    onAnswerSelected: (Boolean) -> Unit
) {
    var selectedAnswer by rememberSaveable { mutableStateOf("") }
    var showResult by rememberSaveable { mutableStateOf(false) }

    // Shuffle the options so that the correct answer isn't always in the same spot
    val options = question.incorrect_answers + question.correct_answer

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(400.dp)
            .clickable {
                if (!showResult) {
                    selectedAnswer = ""
                    showResult = false
                }
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = android.text.Html.fromHtml(question.question, android.text.Html.FROM_HTML_MODE_LEGACY).toString(),
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                options.shuffled().forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (showResult && option == question.correct_answer)
                                    Color.Green.copy(alpha = 0.5f)
                                else if (showResult && option == selectedAnswer)
                                    Color.Red.copy(alpha = 0.5f)
                                else
                                    Color.Transparent
                            )
                            .clip(MaterialTheme.shapes.medium)
                            .clickable {
                                if (!showResult) {
                                    selectedAnswer = option
                                    showResult = true
                                    onAnswerSelected(option == question.correct_answer)
                                }
                            }
                            .padding(8.dp)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(4.dp)
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = option, modifier = Modifier
                            .weight(1f)
                            .padding(8.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TriviaGame(
    questions: List<TriviaQuestion>
) {
    val context = LocalContext.current

    Box(modifier =  Modifier.fillMaxWidth()){
        val pagerState = rememberPagerState()
        HorizontalPager(pageCount = questions.size, state = pagerState, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.align(
            Alignment.Center)) { page ->
            val question = questions[page]
            TriviaCard(
                modifier = Modifier.pagerTransition(page, pagerState),
                question = question,
                onAnswerSelected = { isCorrect ->
                    Toast.makeText(context, "Answer is ${isCorrect}", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.pagerTransition(page: Int, pagerState: PagerState) = graphicsLayer {
    val pageOffset = pagerState.calculateCurrentOffsetForPage(page)
    translationX = pageOffset * size.width

    if (pageOffset < -1f) {
        alpha = 0f
    } else if (pageOffset <= 0) {
        alpha = 1f
        rotationZ = -36000f * pageOffset.absoluteValue.pow(7)

    } else if (pageOffset <= 1) {
        alpha = 1f
        rotationZ = 36000f * pageOffset.absoluteValue.pow(7)
    } else {
        alpha = 0f
    }

    if (pageOffset.absoluteValue <= 0.5) {
        alpha = 1f
        scaleX = (1 - pageOffset.absoluteValue)
        scaleY = (1 - pageOffset.absoluteValue)
    } else if (pageOffset.absoluteValue > 0.5) {
        alpha = 0f
    }
}
@OptIn(ExperimentalFoundationApi::class)
fun PagerState.calculateCurrentOffsetForPage(page: Int): Float {
    return (currentPage - page) + currentPageOffsetFraction
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TriviaOptionsInput(
    onSearchClicked: (difficulty: String,input: Int) -> Unit,
) {
    var selectedDifficulty by remember { mutableStateOf("medium") }
    var mExpanded by remember { mutableStateOf(false) }
    var selectedNumber by remember { mutableStateOf(0) }
    var searchInput by remember { mutableStateOf("") }


    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        // Search Box
        TextField(
            value = searchInput,
            onValueChange = { newValue ->
                searchInput = newValue
                // Here you can add validation for numeric input if needed
                val number = newValue.toIntOrNull() ?: 0
                selectedNumber = number
            },
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.Black,
                containerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            singleLine = true,
            placeholder = { Text("Enter Number of Questions") },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        )

        // Difficulty Dropdown
        ExposedDropdownMenuBox(expanded = mExpanded, onExpandedChange ={newValue -> mExpanded = newValue} ) {
            TextField(
                value = selectedDifficulty,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = mExpanded)
                },
                placeholder = {
                   "Hellop"
                },
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.Black,
                    containerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                singleLine = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            DropdownMenu(
                expanded = mExpanded,
                onDismissRequest = { mExpanded = false  }
            ) {
                DropdownMenuItem(
                    onClick = {
                        selectedDifficulty = "easy"
                        mExpanded = false
                    },
                    text = {Text("Easy")}
                )
                DropdownMenuItem(
                    onClick = {
                        selectedDifficulty = "medium"
                        mExpanded = false
                    },
                    text = {Text("Medium")}
                )
                DropdownMenuItem(
                    onClick = {
                        selectedDifficulty = "hard"
                        mExpanded = false
                    },
                    text = {Text("Hard")}
                )
            }

        }
        Button(
            onClick = {
                onSearchClicked(selectedDifficulty,selectedNumber)
            },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text("Make Trivia")
        }

    }
}



