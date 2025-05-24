package com.fit2081.arrtish.id32896786.a1.internalpages.insights
/**
 * Disclaimer:
 * This file may include comments or documentation assisted by OpenAI's GPT model.
 * All code logic and architectural decisions were implemented and verified by the developer.
 */
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fit2081.arrtish.id32896786.a1.AppViewModelFactory

/**
 * InsightsPage Composable
 *
 * Displays detailed nutritional insights for a user based on their patient data.
 * Fetches patient data from ViewModel using provided userId.
 * Shows individual food group scores and a total food quality score.
 * Provides buttons to share insights or navigate to NutriCoach page for diet improvement.
 *
 * @param userId The ID of the user whose insights to display.
 * @param modifier Optional Modifier for styling/layout.
 * @param navController NavHostController to handle navigation actions.
 * @param viewModelFactory Factory to provide the InsightsViewModel instance.
 */
@Composable
fun InsightsPage(
    userId: Int,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModelFactory: AppViewModelFactory
) {
    // Obtain ViewModel instance using the provided factory
    val viewModel: InsightsViewModel = viewModel(factory = viewModelFactory)

    // Obtain current Context for sharing and other operations
    val context = LocalContext.current

    // Observe the patient LiveData from ViewModel and get the latest patient data
    val patient by viewModel.patient.observeAsState()

    // Trigger loading patient data by userId
    viewModel.loadPatientDataById(userId)

    // If patient data is available, render insights UI
    patient?.let { patientData ->
        // Map each food category to its score for display
        val userScores = mapOf(
            "Vegetables" to patientData.vegetables,
            "Fruits" to patientData.fruits,
            "Grains & Cereals" to patientData.grainsAndCereals,
            "Whole Grains" to patientData.wholeGrains,
            "Meat & Alternatives" to patientData.meatAndAlternatives,
            "Dairy & Alternatives" to patientData.dairyAndAlternatives,
            "Water" to patientData.water,
            "Unsaturated Fats" to patientData.unsaturatedFats,
            "Sodium" to patientData.sodium,
            "Sugar" to patientData.sugar,
            "Alcohol" to patientData.alcohol,
            "Discretionary Foods" to patientData.discretionaryFoods
        )

        val totalScore = patientData.totalScore

        // Container Box for overall layout with padding
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 32.dp, bottom = 128.dp, start = 16.dp, end = 16.dp)
        ) {
            // Vertical scrollable column for content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Page title
                Text("Insights", fontSize = 24.sp, fontWeight = FontWeight.Bold)

                Spacer(Modifier.height(24.dp))

                // Section title for Food Score breakdown
                Text(
                    text = "Food Score",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )

                // Display each food score as a slider item
                userScores.forEach { (name, score) ->
                    FoodScoreItem(name, score)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title for total score slider
                Text(
                    text = "Total Food Quality Score",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                // Disabled slider showing total score (0 to 100)
                Slider(
                    value = totalScore,
                    onValueChange = {},
                    valueRange = 0f..100f,
                    enabled = false,
                    colors = SliderDefaults.colors(
                        disabledThumbColor = MaterialTheme.colorScheme.primary,
                        disabledActiveTrackColor = MaterialTheme.colorScheme.primary
                    )
                )

                // Display numeric total score under slider
                Text(
                    text = "%.2f / 100".format(totalScore),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Button to share insights using ViewModel's share function
                Button(
                    onClick = {
                        viewModel.sharingInsights(context, userScores, totalScore, 100f)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Share with someone")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Button to navigate to NutriCoach page for diet improvement
                Button(
                    onClick = {
                        navController.navigate("nutricoach")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Improve my diet!")
                }
            }
        }
    }
}

/**
 * FoodScoreItem Composable
 *
 * Displays an individual food group name and its corresponding score using
 * a disabled slider and numeric text.
 *
 * @param name The name of the food group (e.g., "Vegetables").
 * @param score The numeric score for that food group (usually 0 to 10).
 */
@Composable
fun FoodScoreItem(name: String, score: Number) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Food group name, takes weight 1 of available horizontal space
        Text(text = name, modifier = Modifier.weight(1f))

        Spacer(modifier = Modifier.width(2.dp))

        // Disabled slider representing the score visually (range 0-10)
        Slider(
            value = score.toFloat(),
            onValueChange = {},
            valueRange = 0f..10f,
            enabled = false,
            modifier = Modifier
                .weight(1.2f)
                .padding(),
            colors = SliderDefaults.colors(
                disabledThumbColor = MaterialTheme.colorScheme.primary,
                disabledActiveTrackColor = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.width(2.dp))

        // Numeric score displayed as text with 2 decimals, e.g. "7.25/10"
        Text(text = "%.2f/10".format(score), modifier = Modifier.padding(start = 8.dp))
    }
}