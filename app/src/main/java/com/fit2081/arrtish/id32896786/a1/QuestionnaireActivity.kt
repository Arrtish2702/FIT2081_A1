package com.fit2081.arrtish.id32896786.a1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.edit
import com.fit2081.arrtish.id32896786.a1.ui.theme.A1Theme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class QuestionnaireActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            A1Theme {
                QuestionnairePage()
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun QuestionnairePage() {
    val context = LocalContext.current
    val selectedCategories = remember { mutableStateListOf<String>() }
    val personas = listOf(
        "Health Devotee", "Mindful Eater", "Wellness Striver",
        "Balance Seeker", "Health Procrastinator", "Food Carefree"
    )
    val selectedPersona = remember { mutableStateOf(personas[0]) }
    val biggestMealTime = remember { mutableStateOf("12:00 PM") }
    val sleepTime = remember { mutableStateOf("10:00 PM") }
    val wakeTime = remember { mutableStateOf("6:00 AM") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Select Food Categories:", fontSize = 20.sp)

        val foodCategories = listOf("Fruits", "Vegetables", "Grains", "Proteins", "Dairy", "Fats")

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(foodCategories) { category ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = selectedCategories.contains(category),
                        onCheckedChange = {
                            if (it) selectedCategories.add(category) else selectedCategories.remove(category)
                        }
                    )
                    Text(text = category)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Select Your Persona:", fontSize = 20.sp)

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(personas) { persona ->
                Button(
                    onClick = { selectedPersona.value = persona },
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(text = persona)
                }
            }
        }
        
        Text("Meal Timing:")
        TextField(
            value = biggestMealTime.value,
            onValueChange = { biggestMealTime.value = it },
            label = { Text("Biggest Meal Time") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = sleepTime.value,
            onValueChange = { sleepTime.value = it },
            label = { Text("Sleep Time") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = wakeTime.value,
            onValueChange = { wakeTime.value = it },
            label = { Text("Wake-up Time") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { completeQuestionnaire(context) }) {
                Text("Save Responses")
            }
            Button(onClick = { tempEraseQuestionnaireData(context) }) {
                Text("Clear Response Data")
            }
        }
    }
}

fun completeQuestionnaire(context: Context){
    val sharedPreferences = context.getSharedPreferences("assignment_1", Context.MODE_PRIVATE)
    sharedPreferences.edit{
        putBoolean("answered", true)
    }
    onRouteToHome(context)
}

fun tempEraseQuestionnaireData(context: Context){
    val sharedPreferences = context.getSharedPreferences("assignment_1", Context.MODE_PRIVATE)
    sharedPreferences.edit{
        putBoolean("answered", false)
    }
    Toast.makeText(context,"Data Erased", Toast.LENGTH_LONG).show()
    onRouteToHome(context)
}

fun onRouteToHome(context: Context) {
    val intent = Intent(context, HomeActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(intent)
}