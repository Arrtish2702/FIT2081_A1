package com.fit2081.arrtish.id32896786.a1.insights


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fit2081.arrtish.id32896786.a1.databases.AppDataBase
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.PatientRepository

// InsightsActivity class - Activity for displaying food insights
class InsightsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

        }
    }
}

@Composable
fun InsightsPage(userId: Int, modifier: Modifier = Modifier, navController: NavHostController) {
    val context = LocalContext.current
    val db = AppDataBase.getDatabase(context)
    val repository = PatientRepository(db.patientDao())

    val viewModel: InsightsViewModel = viewModel(
        factory = InsightsViewModel.InsightsViewModelFactory(repository)
    )

    val patient by viewModel.patient.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadPatientScoresById(userId)
    }

    patient?.let { patientData ->
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Insights: Food Score",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                userScores.forEach { (name, score) ->
                    FoodScoreItem(name, score)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Total Food Quality Score", fontSize = 16.sp)

                Slider(
                    value = totalScore,
                    onValueChange = {}, // Read-only
                    valueRange = 0f..100f,
                    enabled = false,
                    colors = SliderDefaults.colors(
                        disabledThumbColor = MaterialTheme.colorScheme.primary,
                        disabledActiveTrackColor = MaterialTheme.colorScheme.primary
                    )
                )

                Text(text = "%.2f / 100".format(totalScore))

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        viewModel.sharingInsights(context, userScores, totalScore, 100f)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Share with someone")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        navController.navigate("nutricoach")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Improve my diet!")
                }
            }
        }
    }
}

// FoodScoreItem composable - Displays individual category name and score in a row
@Composable
fun FoodScoreItem(name: String, score: Number) {
    Row(
        modifier = Modifier.fillMaxWidth(),  // Fill the width of the row
        verticalAlignment = Alignment.CenterVertically  // Align items vertically at the center
    ) {
        // Category name text
        Text(text = name, modifier = Modifier.weight(1f))  // Make name take available space

        Spacer(modifier = Modifier.width(2.dp))  // Add horizontal space

        // Slider to display category score (disabled)
        Slider(
            value = score.toFloat(),
            onValueChange = {},  // No interaction allowed (disabled slider)
            valueRange = 0f..10f,  // Value range from 0 to 10
            enabled = false,  // Slider is disabled (read-only)
            modifier = Modifier
                .weight(1.2f)  // Slider takes more space than name
                .padding(),  // Add padding around slider
            colors = SliderDefaults.colors(
                disabledThumbColor = MaterialTheme.colorScheme.primary,
                disabledActiveTrackColor = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.width(2.dp))  // Add horizontal space

        // Display category score text (formatted to 2 decimal places)
        Text(text = "%.2f/10".format(score), modifier = Modifier.padding(start = 8.dp))
    }
}