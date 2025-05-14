package com.fit2081.arrtish.id32896786.a1.nutricoach

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fit2081.arrtish.id32896786.a1.AppViewModelFactory

@Composable
fun NutriCoachPage(userId: Int, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val viewModel: NutriCoachViewModel = viewModel(
        factory = AppViewModelFactory(context)
    )

    var fruitName by remember { mutableStateOf("") }
    var motivationalMessage by remember { mutableStateOf("") }

    val fruitDetails by viewModel.fruitDetails.observeAsState(emptyMap())
    val errorMessage by viewModel.errorMessage.observeAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("NutriCoach", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = fruitName,
            onValueChange = { fruitName = it },
            label = { Text("Fruit Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                viewModel.fetchFruit(fruitName)
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Details")
        }

        Spacer(Modifier.height(16.dp))

        // Show error if any
        errorMessage?.let { err ->
            Text(
                text = err,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
        }

        // Render the details
        if (fruitDetails.isNotEmpty()) {
            fruitDetails.forEach { (label, value) ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("$label :", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    Text(value, modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(4.dp))
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                motivationalMessage = "Hey! Just a little nudge to maybe grab a banana or an apple today. You got this! 🍌🍎"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Star, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Motivational Message (AI)")
        }

        Spacer(Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Text(
                motivationalMessage,
                modifier = Modifier.padding(12.dp),
                fontStyle = FontStyle.Italic
            )
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                // Show modal or toast for saved tips
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Shows All Tips")
        }
    }
}


