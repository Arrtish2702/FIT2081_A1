package com.fit2081.arrtish.id32896786.a1.internalpages.questionnaire
/**
 * Disclaimer:
 * This file may include comments or documentation assisted by OpenAI's GPT model.
 * All code logic and architectural decisions were implemented and verified by the developer.
 */
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import kotlin.String
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Composable function displaying the questionnaire page for user input.
 *
 * @param userId The current user's ID.
 * @param navController Navigation controller to handle page transitions.
 * @param viewModelFactory Factory to create the QuestionnaireViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionnairePage(
    userId: Int,
    navController: NavController,
    viewModelFactory: AppViewModelFactory
) {
    // Get current Android context
    val context = LocalContext.current

    // Obtain the ViewModel using the provided factory
    val viewModel: QuestionnaireViewModel = viewModel(factory = viewModelFactory)

    Log.v(MainActivity.TAG, "Questionnaire: Vm made")

    // Load patient data and existing intake on first composition or when userId changes
    LaunchedEffect(userId) {
        viewModel.loadPatientDataByIdAndIntake(userId)
    }

    // Observe existing food intake LiveData from ViewModel
    val foodIntake by viewModel.existingIntake.observeAsState()

    // List of persona options for selection
    val personas = listOf(
        "Health Devotee", "Mindful Eater", "Wellness Striver",
        "Balance Seeker", "Health Procrastinator", "Food Carefree"
    )

    // Access ViewModel state variables for UI binding
    val selectedCategories = viewModel.selectedCategories
    val biggestMealTime = viewModel.biggestMealTime
    val sleepTime = viewModel.sleepTime
    val wakeTime = viewModel.wakeTime
    val selectedPersona = viewModel.selectedPersona
    val scrollState = rememberScrollState()

    // State controlling dropdown expansion and modal visibility
    var expanded by remember { mutableStateOf(false) }
    var showModal by remember { mutableStateOf(false) }

    // Observe messages from ViewModel to show Toasts or navigate
    val questionnaireMessage by viewModel.questionnaireMessage

    // Show toast messages on changes to questionnaireMessage LiveData
    LaunchedEffect(questionnaireMessage) {
        questionnaireMessage?.let {
            if (it.isNotBlank()) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()

                // Navigate back to home screen after successful save
                if (it == "Responses saved successfully.") {
                    navController.navigate("home") {
                        popUpTo("questionnaire") { inclusive = true }
                    }
                }

                // Reset message to avoid repeated toasts
                viewModel.questionnaireMessage.value = ""
            }
        }
    }

    // Log when existing intake data changes
    foodIntake?.let {
        Log.v(MainActivity.TAG, "UI: existing intake updated: $it")
    }

    // Main scrollable column layout for questionnaire UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Page title
        Text("Questionnaire", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(24.dp))

        // Section: Food Categories selection label
        Text("Select Food Categories:", fontSize = 20.sp)

        // List of food categories available for selection
        val foodCategories = listOf("Fruits", "Vegetables", "Grains", "Red Meat", "Seafood", "Poultry", "Fish", "Eggs","Nuts/Seeds")

        // Display food categories in a 3-column grid with checkboxes
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
                            checked = selectedCategories.value.contains(category),
                            onCheckedChange = {
                                val current = selectedCategories.value.toMutableList()
                                if (it) current.add(category) else current.remove(category)
                                viewModel.selectedCategories.value = current
                            }
                        )
                        Text(text = category, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section: Persona selection label
        Text("Select Your Persona:", fontSize = 20.sp)

        // Persona options shown in a 2-column grid as buttons
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
                            viewModel.selectedPersona.value = persona
                            showModal = true  // Show persona detail modal on selection
                        },
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .size(width = 200.dp, height = 50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = persona, fontSize = 12.sp)
                    }
                }
            }
        }

        // Show detailed modal with persona description and image if triggered
        if (showModal) {
            PersonaModal(
                selectedPersona = selectedPersona.value,
                onDismiss = { showModal = false }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dropdown menu for selecting persona (alternative to buttons)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selectedPersona.value,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
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
                            selectedPersona.value = persona
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section: Meal Time selection label and button to open time picker dialog
        Text("Select Your Meal Time:", fontSize = 20.sp)
        Button(
            onClick = {
                viewModel.openTimePicker(
                    context,
                    biggestMealTime.value
                ) { viewModel.biggestMealTime.value = it }
            },
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(biggestMealTime.value)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Section: Sleep Time selection label and button
        Text("Select Your Sleep Time:", fontSize = 20.sp)
        Button(
            onClick = {
                viewModel.openTimePicker(
                    context,
                    sleepTime.value,
                ) { viewModel.sleepTime.value = it }
            },
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(sleepTime.value)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Section: Wake Time selection label and button
        Text("Select Your Wake Time:", fontSize = 20.sp)
        Button(
            onClick = {
                viewModel.openTimePicker(
                    context,
                    wakeTime.value
                ) { viewModel.wakeTime.value = it }
            },
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(wakeTime.value)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Save responses button triggers ViewModel save method with current selections
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(
                onClick = {
                    viewModel.saveFoodIntake(
                        selectedCategories = selectedCategories.value,
                        biggestMealTime = biggestMealTime.value,
                        sleepTime = sleepTime.value,
                        wakeTime = wakeTime.value,
                        selectedPersona = selectedPersona.value
                    )
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Responses")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

/**
 * Composable that displays a modal dialog showing persona details.
 *
 * @param selectedPersona The persona name currently selected.
 * @param onDismiss Callback when the dialog is dismissed.
 */
@Composable
fun PersonaModal(selectedPersona: String, onDismiss: () -> Unit) {
    // Obtain the enum instance for the selected persona to get description and image
    val persona = PersonaEnum.fromDisplayName(selectedPersona)

    val textInput = persona.description
    val imageResId = persona.imageResId

    // Show an AlertDialog with persona details and an image
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
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Close")
            }
        }
    )
}