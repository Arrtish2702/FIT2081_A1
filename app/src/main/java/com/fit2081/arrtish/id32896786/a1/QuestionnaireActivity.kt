package com.fit2081.arrtish.id32896786.a1

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit2081.arrtish.id32896786.a1.ui.theme.A1Theme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class QuestionnaireActivity : ComponentActivity() {
    private lateinit var userPrefs: UserSharedPreferences

    private val selectedCategories = mutableStateListOf<String>()
    private val personas = listOf(
        "Health Devotee", "Mindful Eater", "Wellness Striver",
        "Balance Seeker", "Health Procrastinator", "Food Carefree"
    )

    private val biggestMealTime = mutableStateOf("12:00 PM")
    private val sleepTime = mutableStateOf("10:00 PM")
    private val wakeTime = mutableStateOf("6:00 AM")
    private val selectedPersona = mutableStateOf("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Retrieve userId from Intent
        val userId = intent.getStringExtra("user_id") ?: "default_user"
        Log.v("Questionnaire", userId)

        // Initialize UserSharedPreferences
        userPrefs = UserSharedPreferences(this, userId)

        // Call loadSavedPreferences here instead of onStart
        loadSavedPreferences()

        setContent {
            A1Theme {
                QuestionnairePage(
                    userId = userId,
                    selectedCategories = selectedCategories,
                    personas = personas,
                    biggestMealTime = biggestMealTime,
                    sleepTime = sleepTime,
                    wakeTime = wakeTime,
                    selectedPersona = selectedPersona,
                    userPrefs = userPrefs,
                    context = this
                )
            }
        }
    }

    private fun loadSavedPreferences() {
        val savedChoices = userPrefs.getUserChoices()
        if (savedChoices != null) {
            selectedCategories.clear()
            selectedCategories.addAll(savedChoices["selectedCategories"] as List<String>)

            biggestMealTime.value = savedChoices["biggestMealTime"] as String
            sleepTime.value = savedChoices["sleepTime"] as String
            wakeTime.value = savedChoices["wakeTime"] as String
            selectedPersona.value = savedChoices["selectedPersona"] as String
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionnairePage(
    userId: String,
    selectedCategories: MutableList<String>,
    personas: List<String>,
    biggestMealTime: MutableState<String>,
    sleepTime: MutableState<String>,
    wakeTime: MutableState<String>,
    selectedPersona: MutableState<String>, // Keep this as a parameter
    userPrefs: UserSharedPreferences,
    context: Context
) {
    val scrollState = rememberScrollState()
    // To handle dropdown visibility
    var expanded by remember { mutableStateOf(false) }
    var showModal by remember { mutableStateOf(false) }  // Track if modal should be shown

    // Function to open TimePicker
    fun openTimePicker(initialTime: String, onTimeSet: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val initialHour = calendar.get(Calendar.HOUR_OF_DAY)
        val initialMinute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val time = formatTime(hourOfDay, minute)
                onTimeSet(time)
            },
            initialHour, initialMinute, false
        )
        timePickerDialog.show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Food Categories Section
        Text("Select Food Categories:", fontSize = 20.sp)

        val foodCategories = listOf("Fruits", "Vegetables", "Grains", "Proteins", "Dairy", "Fats")

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
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

        // Persona Selection Section
        Text("Select Your Persona:", fontSize = 20.sp)

        // LazyVerticalGrid with persona buttons
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(personas) { persona ->
                Button(
                    onClick = {
                        selectedPersona.value = persona
                        showModal = true // Open the modal when the button is clicked
                    },
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .size(width = 200.dp, height = 50.dp)
                ) {
                    Text(text = persona, fontSize = 12.sp)
                }
            }
        }

        // Show the Persona Modal if selected
        if (showModal) {
            PersonaModal(selectedPersona = selectedPersona.value, onDismiss = { showModal = false })
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Persona Dropdown Section
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selectedPersona.value, // Use selectedPersona.value directly
                onValueChange = {},  // No need to change value on input (readonly)
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                personas.forEach { persona ->
                    DropdownMenuItem(
                        text = { Text(persona) },
                        onClick = {
                            selectedPersona.value =
                                persona // Correctly update the MutableState here
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Time Selection Sections
        Text("Select Your Meal Time:", fontSize = 20.sp)
        Button(
            onClick = { openTimePicker(biggestMealTime.value) { biggestMealTime.value = it } }
        ) {
            Text(biggestMealTime.value)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Select Your Sleep Time:", fontSize = 20.sp)
        Button(
            onClick = { openTimePicker(sleepTime.value) { sleepTime.value = it } }
        ) {
            Text(sleepTime.value)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Select Your Wake Time:", fontSize = 20.sp)
        Button(
            onClick = { openTimePicker(wakeTime.value) { wakeTime.value = it } }
        ) {
            Text(wakeTime.value)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Save & Clear Buttons
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                completeQuestionnaire(
                    userId, context, selectedCategories, biggestMealTime, sleepTime, wakeTime, selectedPersona, userPrefs
                )
            }) {
                Text("Save Responses")
            }

            Spacer(modifier = Modifier.width(4.dp))

            Button(onClick = { eraseQuestionnaireData(context, userId, userPrefs) }) {
                Text("Clear Responses")
            }
        }
        Spacer(modifier = Modifier.height(32.dp)) // Extra padding to prevent cutoff
    }
}

// Format time as HH:mm AM/PM
fun formatTime(hour: Int, minute: Int): String {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, minute)

    val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return format.format(calendar.time)
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


fun completeQuestionnaire(
    userId: String,
    context: Context,
    selectedCategories: List<String>,
    biggestMealTime: MutableState<String>,
    sleepTime: MutableState<String>,
    wakeTime: MutableState<String>,
    selectedPersona: MutableState<String>,
    userPrefs: UserSharedPreferences,
) {
    // Construct a map with the questionnaire data
    val userChoices = mapOf(
        "selectedCategories" to selectedCategories,
        "biggestMealTime" to biggestMealTime.value,
        "sleepTime" to sleepTime.value,
        "wakeTime" to wakeTime.value,
        "selectedPersona" to selectedPersona.value
    )

    // Log before saving
    Log.v("Questionnaire", "Saving User Preferences for User ID: ${userPrefs}")
    Log.v("Questionnaire", "Data being saved: $userChoices")

    userPrefs.saveUserChoices(userChoices)

    // Log after saving
    val savedData = userPrefs.getUserChoices()
    Log.v("Questionnaire", "User Preferences after saving: $savedData")

    Toast.makeText(context, "Responses saved!", Toast.LENGTH_SHORT).show()
    onRouteToHome(context, userId)
}

fun eraseQuestionnaireData(context: Context, userId: String, userPrefs: UserSharedPreferences) {
    // Create a map with default empty data for the questionnaire
    val emptyChoices = mapOf(
        "selectedCategories" to emptyList<String>(),
        "biggestMealTime" to "12:00 PM",
        "sleepTime" to "10:00 PM",
        "wakeTime" to "6:00 AM",
        "selectedPersona" to "Select a persona"
    )
    // Log before clearing
    Log.v("Questionnaire", "Clearing User Preferences for User ID: ${userPrefs}")
    Log.v("Questionnaire", "Data before clearing: ${userPrefs.getUserChoices()}")

    userPrefs.saveUserChoices(emptyChoices)

    // Log after clearing
    val clearedData = userPrefs.getUserChoices()
    Log.v("Questionnaire", "User Preferences after clearing: $clearedData")

    Toast.makeText(context, "Data Erased", Toast.LENGTH_LONG).show()
    onRouteToHome(context, userId)
}


fun onRouteToHome(context: Context, userId: String?) {
    val intent = Intent(context, HomeActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        putExtra("user_id", userId)
    }
    context.startActivity(intent)
}
