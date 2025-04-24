package com.fit2081.arrtish.id32896786.a1.nutricoach

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fit2081.arrtish.id32896786.a1.ui.theme.A1Theme

class NutriCoachActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            A1Theme {

            }
        }
    }
}

@Composable
fun NutriCoachPage(userId: Int?, modifier: Modifier = Modifier, viewModel: NutriCoachViewModel = viewModel()) {
    var fruitName by remember { mutableStateOf("") }
    var motivationalMessage by remember { mutableStateOf("") }

    val fruitDetails = remember {
        mutableStateOf(
            mapOf(
                "family" to "",
                "calories" to "",
                "fat" to "",
                "sugar" to "",
                "carbohydrates" to "",
                "protein" to ""
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("NutriCoach", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = fruitName,
            onValueChange = { fruitName = it },
            label = { Text("Fruit Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                // Dummy data for demonstration
                fruitDetails.value = mapOf(
                    "family" to "Musaceae",
                    "calories" to "96",
                    "fat" to "0.2",
                    "sugar" to "17.2",
                    "carbohydrates" to "22",
                    "protein" to "1"
                )
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Details")
        }

        Spacer(modifier = Modifier.height(16.dp))

        fruitDetails.value.forEach { (label, value) ->
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("$label :", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                Text(value, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                motivationalMessage = "Hey! Just a little nudge to maybe grab a banana or an apple today. You got this! 🍌🍎"
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBB86FC))
        ) {
            Icon(Icons.Default.Star, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Motivational Message (AI)")
        }

        Spacer(modifier = Modifier.height(12.dp))

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

        Spacer(modifier = Modifier.height(20.dp))

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
