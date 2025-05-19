package com.fit2081.arrtish.id32896786.a1.questionnaire

import android.app.TimePickerDialog
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fit2081.arrtish.id32896786.a1.AppViewModelFactory
import com.fit2081.arrtish.id32896786.a1.MainActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.String
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionnairePage(
    userId: Int,
    navController: NavController,
    viewModel: QuestionnaireViewModel = viewModel(
        factory = AppViewModelFactory(LocalContext.current)
    )
) {
    val context = LocalContext.current

    Log.v(MainActivity.TAG, "Questionnaire: Vm made")
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
    val questionnaireMessage by viewModel.questionnaireMessage

    LaunchedEffect(questionnaireMessage) {
        questionnaireMessage?.let {
            if (it.isNotBlank()) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.questionnaireMessage.value = "" // reset message after showing
            }
        }
    }

    LaunchedEffect(foodIntake) {
        if (foodIntake != null) {
            val fmt = SimpleDateFormat("hh:mm a", Locale.getDefault())

            // Convert Date back to the desired string format
            biggestMealTime.value = fmt.format(foodIntake!!.biggestMealTime)
            sleepTime.value = fmt.format(foodIntake!!.sleepTime)
            wakeTime.value = fmt.format(foodIntake!!.wakeTime)
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


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 16.dp // Ensures buttons at the bottom are visible
            )
            .verticalScroll(scrollState),  // Make column scrollable
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Questionnaire", fontSize = 24.sp, fontWeight = FontWeight.Bold)

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
                                if (it) selectedCategories.add(category) else selectedCategories.remove(
                                    category
                                )
                            }
                        )
                        Text(text = category, fontSize = 12.sp)
                    }
                }
            }

        }

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
            PersonaModal(
                selectedPersona = selectedPersona.value,
                onDismiss = { showModal = false })
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
            onClick = {
                viewModel.openTimePicker(
                    context,
                    biggestMealTime.value
                ) { biggestMealTime.value = it }
            }  // Open time picker for biggest meal time
        ) {
            Text(biggestMealTime.value)  // Display selected meal time
        }

        Spacer(modifier = Modifier.height(8.dp))  // Spacer for layout spacing

        Text("Select Your Sleep Time:", fontSize = 20.sp)  // Label for sleep time selection
        Button(
            onClick = {
                viewModel.openTimePicker(context, sleepTime.value) {
                    sleepTime.value = it
                }
            }  // Open time picker for sleep time
        ) {
            Text(sleepTime.value)  // Display selected sleep time
        }

        Spacer(modifier = Modifier.height(8.dp))  // Spacer for layout spacing

        Text("Select Your Wake Time:", fontSize = 20.sp)  // Label for wake time selection
        Button(
            onClick = {
                viewModel.openTimePicker(context, wakeTime.value) {
                    wakeTime.value = it
                }
            }  // Open time picker for wake time
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
                navController.navigate("home") {
                    popUpTo("questionnaire") { inclusive = true }
                }
            }) {
                Text("Save Responses")  // Save button
            }
        }
        Spacer(modifier = Modifier.height(32.dp))  // Spacer for layout spacing
    }
}



// Modal to display selected persona details
@Composable
fun PersonaModal(selectedPersona: String, onDismiss: () -> Unit) {
    val persona = PersonaEnum.fromDisplayName(selectedPersona)

    val textInput = persona.description
    val imageResId = persona.imageResId

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