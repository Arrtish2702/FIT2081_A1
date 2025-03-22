package com.fit2081.arrtish.id32896786.a1

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.text.font.FontWeight

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

    // State to track selected persona and modal visibility
    var selectedPersonaForModal by remember { mutableStateOf<String?>(null) }
    var showModal by remember { mutableStateOf(false) }

    val biggestMealTime = remember { mutableStateOf("12:00 PM") }
    val sleepTime = remember { mutableStateOf("10:00 PM") }
    val wakeTime = remember { mutableStateOf("6:00 AM") }

    // Get current time
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    // Create TimePickerDialogs once
    val biggestMealTimePicker = TimePickerDialog(
        context,
        { _, selectedHour, selectedMinute ->
            biggestMealTime.value = String.format("%02d:%02d", selectedHour, selectedMinute)
        },
        hour, minute, false
    )

    val sleepTimePicker = TimePickerDialog(
        context,
        { _, selectedHour, selectedMinute ->
            sleepTime.value = String.format("%02d:%02d", selectedHour, selectedMinute)
        },
        hour, minute, false
    )

    val wakeTimePicker = TimePickerDialog(
        context,
        { _, selectedHour, selectedMinute ->
            wakeTime.value = String.format("%02d:%02d", selectedHour, selectedMinute)
        },
        hour, minute, false
    )

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
                    onClick = {
                        selectedPersonaForModal = persona
                        showModal = true
                    },
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .size(width = 200.dp, height = 50.dp)
                ) {
                    Text(text = persona, fontSize = 12.sp)
                }
            }
        }

        //TODO: Do the DropDown for the personas

        Spacer(modifier = Modifier.height(16.dp))

        Text("Meal Timing:", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(8.dp))

        // Biggest Meal Time Picker
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("What time do you eat your biggest meal?", fontSize = 14.sp)
                TextField(
                    value = biggestMealTime.value,
                    onValueChange = {},
                    label = { Text("Biggest Meal Time") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { biggestMealTimePicker.show() }) {
                Icon(imageVector = Icons.Filled.DateRange, contentDescription = "Pick Biggest Meal Time")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Sleep Time Picker
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("What time do you go to sleep?", fontSize = 14.sp)
                TextField(
                    value = sleepTime.value,
                    onValueChange = {},
                    label = { Text("Sleep Time") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { sleepTimePicker.show() }) {
                Icon(imageVector = Icons.Filled.DateRange, contentDescription = "Pick Sleep Time")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Wake-up Time Picker
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("What time do you wake up?", fontSize = 14.sp)
                TextField(
                    value = wakeTime.value,
                    onValueChange = {},
                    label = { Text("Wake-up Time") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { wakeTimePicker.show() }) {
                Icon(imageVector = Icons.Filled.DateRange, contentDescription = "Pick Wake-Up Time")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { completeQuestionnaire(context) }) {
                Text("Save Responses")
            }

            Spacer(modifier = Modifier.width(4.dp))

            Button(onClick = { tempEraseQuestionnaireData(context) }) {
                Text("Clear Responses")
            }
        }
    }

    // Show modal only if showModal is true
    if (showModal && selectedPersonaForModal != null) {
        PersonaModal(selectedPersonaForModal!!) { showModal = false }
    }
}

@Composable
fun PersonaModal(selectedPersona: String, onDismiss: () -> Unit) {
    val (textInput, imageResId) = when (selectedPersona) {
        "Health Devotee" -> "You are highly committed to your health and wellness goals." to R.drawable.persona_1
        "Mindful Eater" -> "You pay close attention to your food choices and eat with awareness." to R.drawable.persona_2
        "Wellness Striver" -> "You make efforts to improve your well-being but seek more guidance." to R.drawable.persona_3
        "Balance Seeker" -> "You value a balanced lifestyle and strive for moderation in eating." to R.drawable.persona_4
        "Health Procrastinator" -> "You want to be healthier but often postpone taking action." to R.drawable.persona_5
        "Food Carefree" -> "You enjoy food freely without strict rules or limitations." to R.drawable.persona_6
        else -> "Invalid Persona" to R.drawable.default_image
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(selectedPersona) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = selectedPersona,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(textInput, textAlign = TextAlign.Center)
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
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