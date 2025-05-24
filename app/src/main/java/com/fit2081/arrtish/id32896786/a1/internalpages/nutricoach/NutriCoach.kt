package com.fit2081.arrtish.id32896786.a1.internalpages.nutricoach
/**
 * Disclaimer:
 * This file may include comments or documentation assisted by OpenAI's GPT model.
 * All code logic and architectural decisions were implemented and verified by the developer.
 */
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fit2081.arrtish.id32896786.a1.AppViewModelFactory
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage

/**
 * NutriCoachPage Composable
 *
 * Displays a nutrition coach UI that:
 * - Checks optimal fruit score for a user
 * - Allows searching and fetching fruit nutritional details from Fruityvice API
 * - Shows motivational AI-generated messages
 * - Displays saved nutritional tips in a dialog
 *
 * @param userId The current user's ID for personalized data fetching
 * @param modifier Modifier for styling and layout customization
 * @param viewModelFactory Factory to provide NutriCoachViewModel instance with dependencies
 */
@Composable
fun NutriCoachPage(
    userId: Int,
    modifier: Modifier = Modifier,
    viewModelFactory: AppViewModelFactory
) {
    // Obtain the ViewModel instance using provided factory
    val viewModel: NutriCoachViewModel = viewModel(factory = viewModelFactory)

    // On first composition, check the user's optimal fruit score
    LaunchedEffect(Unit) {
        viewModel.optimalFruitScoreChecker(userId)
    }

    // Observe LiveData state from ViewModel as Compose states
    val isGenerating by viewModel.isGeneratingMessage.observeAsState(false)
    val fruitName by viewModel.fruitName.observeAsState("")
    val motivationalMessage by viewModel.motivationalMessage.observeAsState("")
    val fruitDetails by viewModel.fruitDetails.observeAsState(emptyMap())
    val errorMessage by viewModel.errorMessage.observeAsState()
    val tipsList by viewModel.tipsList.observeAsState(emptyList())
    val optimalFruitScore by viewModel.optimalFruitScore.observeAsState()

    // Local state to control showing of tips dialog
    var showTipsDialog by remember { mutableStateOf(false) }

    // Scroll state for vertical scrolling in column
    val scrollState = rememberScrollState()

    // Main container Box with padding and fill size
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
            .fillMaxHeight()
    ) {
        // Main content Column with vertical scroll and padding
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Page title
            Text("NutriCoach", fontSize = 24.sp, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(24.dp))

            // Conditional UI based on user's optimal fruit score check status
            when (optimalFruitScore) {
                true -> {
                    // If user has optimal fruit score, show random fruit image
                    AsyncImage(
                        model = "https://picsum.photos/600/400",
                        contentDescription = "Random Fruit Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(8.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                false -> {
                    // If user does not have optimal fruit score, show Fruityvice API intro and input UI
                    Text(
                        "Fruityvice is a free online API that provides detailed nutritional information about various fruits. " +
                                "Use this tool to enter a fruit name and fetch its nutritional details to help you make informed dietary choices.",
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                    )

                    // Input field for fruit name with two-way binding to ViewModel
                    OutlinedTextField(
                        value = fruitName,
                        onValueChange = { viewModel.setFruitName(it) },
                        label = { Text("Fruit Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))

                    // Button to trigger fetching fruit details
                    Button(
                        onClick = {
                            viewModel.fetchFruit(fruitName)
                        },
                        modifier = Modifier.align(Alignment.End),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Details")
                    }

                    Spacer(Modifier.height(16.dp))

                    // Show error message from ViewModel, if any
                    errorMessage?.let { err ->
                        Text(
                            text = err,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                    }

                    // Display fruit nutritional details if available in key-value format
                    if (fruitDetails.isNotEmpty()) {
                        fruitDetails.forEach { (label, value) ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text("$label :", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                                Text(value, modifier = Modifier.weight(1f))
                            }
                            Spacer(Modifier.height(4.dp))
                        }
                    }
                }
                null -> {
                    // Show loading spinner while optimalFruitScore is loading
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Button to generate AI motivational message
            Button(
                onClick = {
                    viewModel.generateInsightfulMessage(userId)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Star, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Motivational Message (AI)")
            }

            Spacer(Modifier.height(12.dp))

            // Card to display motivational message or loading spinner or prompt text
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                when {
                    isGenerating -> {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    motivationalMessage.isNotBlank() -> {
                        Text(
                            motivationalMessage,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    else -> {
                        Text(
                            "Press the button above to receive a motivational insight.",
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Button to load and show saved nutritional tips in dialog
            Button(
                onClick = {
                    viewModel.loadAllTips(userId)
                    showTipsDialog = true
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Show All Tips")
            }

            // Dialog displaying all saved tips, shown if showTipsDialog is true
            if (showTipsDialog) {
                Dialog(onDismissRequest = { showTipsDialog = false }) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        tonalElevation = 4.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .heightIn(min = 300.dp, max = 700.dp)
                        ) {
                            Text("Saved Tips", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Spacer(Modifier.height(8.dp))

                            if (tipsList.isNotEmpty()) {
                                LazyColumn(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    // Render each tip as a Card with response text and timestamp
                                    items(items = tipsList, key = { it.tipsId }) { tip ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            elevation = CardDefaults.cardElevation(2.dp)
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Text(
                                                    text = tip.responseString,
                                                    fontStyle = FontStyle.Italic
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "Saved on: ${tip.responseTimeStamp}",
                                                    fontSize = 12.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                // Show message if no tips are available
                                Text("No tips available.")
                            }

                            Spacer(Modifier.height(16.dp))

                            // Button to close the dialog
                            Button(
                                onClick = { showTipsDialog = false },
                                modifier = Modifier.align(Alignment.End),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Close")
                            }
                        }
                    }
                }
            }
        }
    }
}