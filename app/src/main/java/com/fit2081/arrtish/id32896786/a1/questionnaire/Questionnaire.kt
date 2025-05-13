package com.fit2081.arrtish.id32896786.a1.questionnaire

import android.app.TimePickerDialog
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fit2081.arrtish.id32896786.a1.AppViewModelFactory
import com.fit2081.arrtish.id32896786.a1.MainActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.fit2081.arrtish.id32896786.a1.R
import kotlin.String


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionnairePage(
    userId: Int,
    navController: NavController
) {

    val context = LocalContext.current
    val viewModel: QuestionnaireViewModel = viewModel(factory = AppViewModelFactory(context))
    Log.v(MainActivity.TAG, "Questionnaire: Vm made")
    // Ensure we call loadPatientDataByIdAndIntake only once
    val hasLoaded = remember { mutableStateOf(false) }
    if (!hasLoaded.value) {
        viewModel.loadPatientDataByIdAndIntake(userId)
        hasLoaded.value = true
    }

    val foodIntake by viewModel.existingIntake.observeAsState()

    val personas = listOf("Health Devotee", "Mindful Eater", "Wellness Striver", "Balance Seeker", "Health Procrastinator", "Food Carefree")

    val selectedCategories = remember { mutableStateListOf<String>() }
    val biggestMealTime = remember { mutableStateOf("12:00 PM") }
    val sleepTime = remember { mutableStateOf("10:00 PM") }
    val wakeTime = remember { mutableStateOf("6:00 AM") }
    val selectedPersona = remember { mutableStateOf("") }
    val scrollState = rememberScrollState()  // Scroll state for vertical scrolling

    var expanded by remember { mutableStateOf(false) }  // State for dropdown menu expansion
    var showModal by remember { mutableStateOf(false) }  // State for modal dialog visibility

    LaunchedEffect(foodIntake) {
        if (foodIntake != null) {
            biggestMealTime.value = foodIntake!!.biggestMealTime.toString()
            sleepTime.value = foodIntake!!.sleepTime.toString()
            wakeTime.value = foodIntake!!.wakeTime.toString()
            selectedPersona.value = foodIntake!!.selectedPersona

            selectedCategories.clear()
            if (foodIntake!!.eatsFruits) selectedCategories.add("Fruits")
            if (foodIntake!!.eatsVegetables) selectedCategories.add("Vegetables")
            if (foodIntake!!.eatsGrains) selectedCategories.add("Grains")
            if (foodIntake!!.eatsRedMeat) selectedCategories.add("Red Meat")
            if (foodIntake!!.eatsSeafood) selectedCategories.add("Seafood")
            if (foodIntake!!.eatsPoultry) selectedCategories.add("Poultry")
            if (foodIntake!!.eatsFish) selectedCategories.add("Fish")
            if (foodIntake!!.eatsEggs) selectedCategories.add("Eggs")
            if (foodIntake!!.eatsNutsOrSeeds) selectedCategories.add("Nuts/Seeds")
        }
    }


    foodIntake?.let {
        Log.v(MainActivity.TAG, "UI: existing intake updated: $it")
    }


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
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 120.dp // Ensures buttons at the bottom are visible
            )
            .verticalScroll(scrollState),  // Make column scrollable
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

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
            itemsIndexed(foodCategories) { index, category ->
                key(index) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = selectedCategories.contains(category),
                            onCheckedChange = {
                                if (it) selectedCategories.add(category) else selectedCategories.remove(category)
                            }
                        )
                        Text(text = category, fontSize = 12.sp)
                    }
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
            itemsIndexed(personas) { index, persona ->
                key(index) {
                    Button(
                        onClick = {
                            selectedPersona.value = persona
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
                viewModel.saveFoodIntake(
                    selectedCategories = selectedCategories,
                    biggestMealTime = biggestMealTime.value,
                    sleepTime = sleepTime.value,
                    wakeTime = wakeTime.value,
                    selectedPersona = selectedPersona.value
                )
            }) {
                Text("Save Responses")  // Save button
            }

            Spacer(modifier = Modifier.width(4.dp))  // Spacer for layout spacing

            Button(onClick = { viewModel.clearFoodIntake() }) {
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