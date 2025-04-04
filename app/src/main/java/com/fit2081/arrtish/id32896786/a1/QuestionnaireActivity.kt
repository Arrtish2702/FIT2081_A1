package com.fit2081.arrtish.id32896786.a1

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.core.content.edit
import kotlin.String

// QuestionnaireActivity for the questionnaire form
class QuestionnaireActivity : ComponentActivity() {
    private lateinit var userPrefs: UserSharedPreferences

    private val selectedCategories = mutableStateListOf<String>()  // List to hold selected food categories
    private val personas = listOf(
        "Health Devotee", "Mindful Eater", "Wellness Striver",
        "Balance Seeker", "Health Procrastinator", "Food Carefree"
    )  // Predefined list of personas

    private val biggestMealTime = mutableStateOf("12:00 PM")  // State to store biggest meal time
    private val sleepTime = mutableStateOf("10:00 PM")  // State to store sleep time
    private val wakeTime = mutableStateOf("6:00 AM")  // State to store wake time
    private val selectedPersona = mutableStateOf("")  // State to store selected persona

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userId = intent.getStringExtra("user_id") ?: "default_user"  // Get user ID from intent or use default
        Log.v("Questionnaire", userId)  // Log user ID

        userPrefs = UserSharedPreferences(this, userId)  // Initialize UserSharedPreferences for the user

        loadSavedPreferences()  // Load saved preferences if they exist

        setContent {
            ShowSystemBars()  // Ensure system bars are visible
            A1Theme {  // Apply app theme
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

    // Function to load saved preferences from UserSharedPreferences
    private fun loadSavedPreferences() {
        val savedChoices = userPrefs.getUserChoices()  // Retrieve saved user choices
        if (savedChoices != null) {
            selectedCategories.clear()
            selectedCategories.addAll(savedChoices["selectedCategories"] as List<String>)  // Load selected categories

            biggestMealTime.value = savedChoices["biggestMealTime"] as String  // Load biggest meal time
            sleepTime.value = savedChoices["sleepTime"] as String  // Load sleep time
            wakeTime.value = savedChoices["wakeTime"] as String  // Load wake time
            selectedPersona.value = savedChoices["selectedPersona"] as String  // Load selected persona
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
    selectedPersona: MutableState<String>,
    userPrefs: UserSharedPreferences,
    context: Context
) {
    val scrollState = rememberScrollState()  // Scroll state for vertical scrolling

    var expanded by remember { mutableStateOf(false) }  // State for dropdown menu expansion
    var showModal by remember { mutableStateOf(false) }  // State for modal dialog visibility

    // Function to open a time picker dialog and set the time
    fun openTimePicker(initialTime: String, onTimeSet: (String) -> Unit) {
        val calendar = Calendar.getInstance()  // Get current calendar instance
        val initialHour = calendar.get(Calendar.HOUR_OF_DAY)  // Initial hour of the day
        val initialMinute = calendar.get(Calendar.MINUTE)  // Initial minute of the day

        // Create and show time picker dialog
        val timePickerDialog = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val time = formatTime(hourOfDay, minute)  // Format time
                onTimeSet(time)  // Set the chosen time
            },
            initialHour, initialMinute, false
        )
        timePickerDialog.show()  // Show time picker dialog
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),  // Make column scrollable
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Select Food Categories:", fontSize = 20.sp)  // Label for food categories selection

        val foodCategories = listOf("Fruits", "Vegetables", "Grains", "Red Meat", "Seafood", "Poultry", "Fish", "Eggs","Nuts/Seeds")  // Food categories list

        // Display food categories in a grid layout with checkboxes
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(foodCategories) { category ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Checkbox for each food category
                    Checkbox(
                        checked = selectedCategories.contains(category),
                        onCheckedChange = {
                            if (it) selectedCategories.add(category) else selectedCategories.remove(category)  // Add or remove category
                        }
                    )
                    Text(text = category, fontSize = 12.sp)  // Display category name
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))  // Spacer for layout spacing

        Text("Select Your Persona:", fontSize = 20.sp)  // Label for persona selection

        // Display personas as buttons
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
                        selectedPersona.value = persona  // Set selected persona
                        showModal = true  // Show persona modal
                    },
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .size(width = 200.dp, height = 50.dp)
                ) {
                    Text(text = persona, fontSize = 12.sp)  // Display persona name
                }
            }
        }

        // Show persona modal dialog if `showModal` is true
        if (showModal) {
            PersonaModal(selectedPersona = selectedPersona.value, onDismiss = { showModal = false })
        }

        Spacer(modifier = Modifier.height(16.dp))  // Spacer for layout spacing

        // Exposed dropdown menu for selecting persona
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }  // Toggle dropdown visibility
        ) {
            TextField(
                value = selectedPersona.value,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),  // Display the persona name in a read-only text field
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }  // Close dropdown on dismiss
            ) {
                personas.forEach { persona ->
                    DropdownMenuItem(
                        text = { Text(persona) },
                        onClick = {
                            selectedPersona.value = persona  // Set selected persona
                            expanded = false  // Close dropdown menu
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))  // Spacer for layout spacing

        Text("Select Your Meal Time:", fontSize = 20.sp)  // Label for meal time selection
        Button(
            onClick = { openTimePicker(biggestMealTime.value) { biggestMealTime.value = it } }  // Open time picker for biggest meal time
        ) {
            Text(biggestMealTime.value)  // Display selected meal time
        }

        Spacer(modifier = Modifier.height(8.dp))  // Spacer for layout spacing

        Text("Select Your Sleep Time:", fontSize = 20.sp)  // Label for sleep time selection
        Button(
            onClick = { openTimePicker(sleepTime.value) { sleepTime.value = it } }  // Open time picker for sleep time
        ) {
            Text(sleepTime.value)  // Display selected sleep time
        }

        Spacer(modifier = Modifier.height(8.dp))  // Spacer for layout spacing

        Text("Select Your Wake Time:", fontSize = 20.sp)  // Label for wake time selection
        Button(
            onClick = { openTimePicker(wakeTime.value) { wakeTime.value = it } }  // Open time picker for wake time
        ) {
            Text(wakeTime.value)  // Display selected wake time
        }

        Spacer(modifier = Modifier.height(16.dp))  // Spacer for layout spacing

        // Row with save and clear buttons
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                completeQuestionnaire(
                    userId, context, selectedCategories, biggestMealTime, sleepTime, wakeTime, selectedPersona, userPrefs
                )
            }) {
                Text("Save Responses")  // Save button
            }

            Spacer(modifier = Modifier.width(4.dp))  // Spacer for layout spacing

            Button(onClick = { eraseQuestionnaireData(context, userId, userPrefs) }) {
                Text("Clear Responses")  // Clear button
            }
        }
        Spacer(modifier = Modifier.height(32.dp))  // Spacer for layout spacing
    }
}

// Function to format time to "hh:mm a" format
fun formatTime(hour: Int, minute: Int): String {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, minute)

    val format = SimpleDateFormat("hh:mm a", Locale.getDefault())  // Date format for time
    return format.format(calendar.time)  // Return formatted time
}

// Modal to display selected persona details
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
        onDismissRequest = onDismiss,  // Dismiss modal on request
        title = { Text(selectedPersona) },  // Display selected persona's name
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = imageResId),  // Display persona image
                    contentDescription = selectedPersona,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))  // Spacer for layout
                Text(textInput, textAlign = TextAlign.Center)  // Display persona description
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")  // Close button to dismiss modal
            }
        }
    )
}

// Function to complete and save the questionnaire data
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
    val userChoices = mapOf(
        "selectedCategories" to selectedCategories,
        "biggestMealTime" to biggestMealTime.value,
        "sleepTime" to sleepTime.value,
        "wakeTime" to wakeTime.value,
        "selectedPersona" to selectedPersona.value
    )

    Log.v("FIT2081-Questionnaire", "Saving User Preferences for User ID: $userPrefs")
    Log.v("FIT2081-Questionnaire", "Data being saved: $userChoices")

    userPrefs.saveUserChoices(userChoices)  // Save the user's questionnaire responses

    // Mark the questionnaire as completed in shared preferences
    UserSharedPreferences.getPreferences(context, userId).edit {
        putBoolean("answered", true)
    }

    val savedData = userPrefs.getUserChoices()
    Log.v("FIT2081-Questionnaire", "User Preferences after saving: $savedData")

    Toast.makeText(context, "Responses saved!", Toast.LENGTH_SHORT).show()  // Show toast on successful save
    onRouteToHome(context, userId)  // Navigate to home screen
}

// Function to erase questionnaire data
fun eraseQuestionnaireData(context: Context, userId: String, userPrefs: UserSharedPreferences) {
    Log.v("FIT2081-Questionnaire", "Clearing User Preferences for User ID: $userPrefs")
    Log.v("FIT2081-Questionnaire", "Data before clearing: ${userPrefs.getUserChoices()}")

    userPrefs.clearUserChoices()  // Clear saved choices
    UserSharedPreferences.getPreferences(context, userId).edit {
        putBoolean("answered", false)  // Mark questionnaire as not answered
    }

    val clearedData = userPrefs.getUserChoices()
    Log.v("FIT2081-Questionnaire", "User Preferences after clearing: $clearedData")

    Toast.makeText(context, "Data Erased", Toast.LENGTH_LONG).show()  // Show toast on successful data erase
    onRouteToHome(context, userId)  // Navigate to home screen
}

// Function to navigate back to the home screen
fun onRouteToHome(context: Context, userId: String?) {
    val intent = Intent(context, HomeActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        putExtra("user_id", userId)  // Pass user ID to HomeActivity
    }
    context.startActivity(intent)  // Start HomeActivity
}
