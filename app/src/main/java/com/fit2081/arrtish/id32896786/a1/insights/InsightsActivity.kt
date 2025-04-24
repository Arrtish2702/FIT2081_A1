package com.fit2081.arrtish.id32896786.a1.insights

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

// InsightsActivity class - Activity for displaying food insights
class InsightsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
//            ShowSystemBars()  // Show system bars (status and navigation)
//            A1Theme {  // Apply app's theme
//                InsightsPage(userId)  // Pass user ID to InsightsPage composable
//            }
        }
    }
}
@Composable
fun InsightsPage(userId: Int?, modifier: Modifier = Modifier, viewModel: InsightsViewModel = viewModel()) {
    val context = LocalContext.current

    // Placeholder mock data
    val userId = remember { mutableStateOf("012345") }

    val userScores = remember {
        mutableStateOf(
            mapOf(
                "Fruits" to 80f,
                "Vegetables" to 65f,
                "Grains" to 75f,
                "Proteins" to 70f
            )
        )
    }

    val totalScore = remember {
        userScores.value.values.average().toFloat()
    }

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

            userScores.value.forEach { (name, score) ->
                FoodScoreItem(name, score)
            }

            Spacer(modifier = Modifier.height(8.dp))

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

            Text(text = "%.2f/100".format(totalScore))

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = {
                    sharingInsights(context, userScores.value, totalScore, 100)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Share with someone")
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = {
                    Toast.makeText(context, "NutriCoach in Development", Toast.LENGTH_LONG).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Improve my diet!")
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

//// Preview of InsightsPage composable (for UI design testing)
//@Preview(showBackground = true)
//@Composable
//fun InsightsScreenPreview() {
//    A1Theme {
//        InsightsContent(userScores = emptyMap(), totalScore = 0f, context = LocalContext.current)  // Show empty preview
//    }
//}

// sharingInsights function - Creates a shareable text of the user's insights and launches share intent
fun sharingInsights(context: Context, userScores: Map<String, Float>, totalScore: Float, maxScore: Int) {
    // Build the text to share
    val shareText = buildString {
        append("🌟 Insights: Food Score 🌟\n")
        userScores.forEach { (category, score) ->  // Append each category and score
            append("$category: %.2f/10\n".format(score))
        }
        append("\n🏆 Total Food Quality Score: %.2f/$maxScore".format(totalScore))  // Append total score
    }

//    // Create an intent to share the insights text
//    val intent = Intent(Intent.ACTION_SEND).apply {
//        Intent.setType = "text/plain"  // Define the share type
//        putExtra(Intent.EXTRA_TEXT, shareText)  // Attach the insights text to the intent
//    }

//    // Start the chooser intent to select sharing app
//    val chooser = Intent.createChooser(intent, "Share your insights via:")
//    context.startActivity(chooser)  // Launch the chooser
}
