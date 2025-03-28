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
import androidx.core.content.edit
import com.fit2081.arrtish.id32896786.a1.ui.theme.A1Theme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import java.util.Locale

class QuestionnaireActivity : ComponentActivity() {

    private val selectedCategories = mutableStateListOf<String>()
    private val personas = listOf(
        "Health Devotee", "Mindful Eater", "Wellness Striver",
        "Balance Seeker", "Health Procrastinator", "Food Carefree"
    )

    private val biggestMealTime = mutableStateOf("12:00 PM")
    private val sleepTime = mutableStateOf("10:00 PM")
    private val wakeTime = mutableStateOf("6:00 AM")
    private var selectedPersona = mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    }

    override fun onStart() {
        super.onStart()
        loadSavedPreferences()
        setContent {
            HideSystemBars()
            A1Theme {
                QuestionnairePage(
                    selectedCategories = selectedCategories,
                    personas = personas,
                    biggestMealTime = biggestMealTime,
                    sleepTime = sleepTime,
                    wakeTime = wakeTime,
                    selectedPersona = selectedPersona
                )
            }
        }
    }

    private fun loadSavedPreferences() {
        val sharedPreferences = this.getSharedPreferences("assignment_1", Context.MODE_PRIVATE)
        if (sharedPreferences.getBoolean("answered", false)) {
            val savedCategories = sharedPreferences.getStringSet("selectedCategories", emptySet()) ?: emptySet()
            selectedCategories.clear()
            selectedCategories.addAll(savedCategories)

            biggestMealTime.value = sharedPreferences.getString("biggestMealTime", "12:00 PM") ?: "12:00 PM"
            sleepTime.value = sharedPreferences.getString("sleepTime", "10:00 PM") ?: "10:00 PM"
            wakeTime.value = sharedPreferences.getString("wakeTime", "6:00 AM") ?: "6:00 AM"

            val savedPersona = sharedPreferences.getString("selectedPersona", null)
            if (savedPersona != null && personas.contains(savedPersona)) {
                selectedPersona.value = savedPersona // ✅ Fix: Correctly assign to MutableState<String>
            } else {
                selectedPersona.value = personas.firstOrNull() ?: "Select a persona"
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionnairePage(
    selectedCategories: MutableList<String>,
    personas: List<String>,
    biggestMealTime: MutableState<String>,
    sleepTime: MutableState<String>,
    wakeTime: MutableState<String>,
    selectedPersona: MutableState<String> // Pass as String, not MutableState
) {
    val context = LocalContext.current
    var selectedPersonaForModal by remember { mutableStateOf<String?>(null) }
    var showModal by remember { mutableStateOf(false) }
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    var expanded by remember { mutableStateOf(false) }
//    var selectedPersona by remember { mutableStateOf(personas.firstOrNull() ?: "Select a persona") }
    val scrollState = rememberScrollState() // For scrolling

    // Create TimePickerDialogs once
    val biggestMealTimePicker = TimePickerDialog(
        context,
        { _, selectedHour, selectedMinute ->
            biggestMealTime.value = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
        },
        hour, minute, false
    )

    val sleepTimePicker = TimePickerDialog(
        context,
        { _, selectedHour, selectedMinute ->
            sleepTime.value = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
        },
        hour, minute, false
    )

    val wakeTimePicker = TimePickerDialog(
        context,
        { _, selectedHour, selectedMinute ->
            wakeTime.value = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
        },
        hour, minute, false
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),  // Enable scrolling
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Select Food Categories:", fontSize = 20.sp)

        val foodCategories = listOf("Fruits", "Vegetables", "Grains", "Proteins", "Dairy", "Fats")

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp), // Prevent infinite height from breaking scroll
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
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp), // Ensuring proper scroll behavior
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

        Spacer(modifier = Modifier.height(16.dp))

        // Dropdown for persona selection
        Box(modifier = Modifier.fillMaxWidth()) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    value = selectedPersona.value, // Use selectedPersona.value
                    onValueChange = {},
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
                                selectedPersona.value = persona // Corrected to update MutableState
                                expanded = false
                            }
                        )
                    }
                }
            }
        }


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
            Button(onClick = { completeQuestionnaire(context, selectedCategories, biggestMealTime, sleepTime, wakeTime, selectedPersona) }) {
                Text("Save Responses")
            }

            Spacer(modifier = Modifier.width(4.dp))

            Button(onClick = { eraseQuestionnaireData(context) }) {
                Text("Clear Responses")
            }
        }
        Spacer(modifier = Modifier.height(32.dp)) // Extra padding to prevent cutoff
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


fun completeQuestionnaire(
    context: Context,
    selectedCategories: List<String>,
    biggestMealTime: MutableState<String>,
    sleepTime: MutableState<String>,
    wakeTime: MutableState<String>,
    selectedPersona: MutableState<String> // Pass as String, not MutableState
) {
    val sharedPreferences = context.getSharedPreferences("assignment_1", Context.MODE_PRIVATE)
    sharedPreferences.edit {
        putStringSet("selectedCategories", selectedCategories.toSet())
        putString("biggestMealTime", biggestMealTime.value)
        putString("sleepTime", sleepTime.value)
        putString("wakeTime", wakeTime.value)
        putString("selectedPersona", selectedPersona.value) // ✅ Fix: Store persona correctly
        putBoolean("answered", true)
        apply()
    }
    onRouteToHome(context)
}

fun eraseQuestionnaireData(context: Context) {
    val sharedPreferences = context.getSharedPreferences("assignment_1", Context.MODE_PRIVATE)
    sharedPreferences.edit {
        putStringSet("selectedCategories", null)
        putString("biggestMealTime", null)
        putString("sleepTime", null)
        putString("wakeTime", null)
        putString("selectedPersona", null) // ✅ Fix: Remove stored persona
        putBoolean("answered", false)
        apply()
    }
    Toast.makeText(context, "Data Erased", Toast.LENGTH_LONG).show()
    onRouteToHome(context)
}

fun onRouteToHome(context: Context) {
    val intent = Intent(context, HomeActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(intent)
}