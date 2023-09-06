package com.example.interriitapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.interriitapp.Models.Recipe
import com.example.interriitapp.Models.RecipeDetails
import com.example.interriitapp.Network.createEdamamService
import com.example.interriitapp.ui.theme.InterrIITAppTheme
import retrofit2.awaitResponse

class FoodTechActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InterrIITAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val makeCall = remember { mutableStateOf(false) }
                    val qry = remember { mutableStateOf("") }
                    val list = remember { mutableStateOf(listOf<Recipe>()) }
                    val showProgress = remember { mutableStateOf(false) }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        SearchBar(makeCall,qry, showProgress)



                        if(showProgress.value){
                            CircularProgressIndicator(modifier = Modifier
                                .width(50.dp)
                                .height(50.dp)
                                .align(Alignment.CenterHorizontally))
                        }else{
                            Spacer(modifier = Modifier.height(16.dp))
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(list.value){
                                    RecipeCard(it.recipe)
                                }
                            }
                        }
                    }

                    if(makeCall.value){
                        val apiService = createEdamamService()
                        val call = apiService.searchRecipes(qry.value,"eff100db",
                            "c200bf963488e2e0fedf1d33c03a19be","any")

                        LaunchedEffect(qry.value){
                            Log.d("Response","Something")
                            val response = call.awaitResponse()
                            if(response.isSuccessful){
                                showProgress.value = false
                                val data = response.body()
                                list.value = data?.hits!!
                            }else{
                                showProgress.value = false
                                Toast.makeText(this@FoodTechActivity,"Error",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }
}

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SearchBar(makeCall: MutableState<Boolean>, query : MutableState<String>, showProgress : MutableState<Boolean>) {
        var searchText by remember { mutableStateOf(TextFieldValue()) }

        OutlinedTextField(
            value = searchText,
            onValueChange = { newText ->
                searchText = newText
                query.value = newText.text
            },
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = { Text("Search for recipes...") },
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.Black,
                containerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            singleLine = true,
            trailingIcon = {
                Button(
                    onClick = {
                        makeCall.value = true
                        showProgress.value = true
                    },
                    modifier = Modifier.padding(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Search")
                }
            }
        )
    }
@Composable
fun RecipeCard(recipe: RecipeDetails) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Image
            Image(
                painter = rememberImagePainter(recipe.image),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = recipe.label ?: "",
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Source
            Text(
                text = "Source: ${recipe.source ?: ""}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Ingredients
            recipe.ingredients?.let {
                Column {
                    Text(
                        text = "Ingredients:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    it.forEach { ingredient ->
                        Text(
                            text = "- ${ingredient.text}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}