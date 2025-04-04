package com.fit2081.arrtish.id32896786.a1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit2081.arrtish.id32896786.a1.ui.theme.A1Theme

// InsightsActivity class - Activity for displaying food insights
class InsightsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get user ID from intent or set default
        val userId = intent.getStringExtra("user_id") ?: "default_user"
        Log.v("FIT2081-InsightsScreen", userId)

        setContent {
            ShowSystemBars()  // Show system bars (status and navigation)
            A1Theme {  // Apply app's theme
                InsightsScreen(userId)  // Pass user ID to InsightsScreen composable
            }
        }
    }
}

// InsightsScreen composable - Displays the user's food insights
@Composable
fun InsightsScreen(userId: String) {
    val context = LocalContext.current  // Get current context
    val userPrefs = remember { UserSharedPreferences(context, userId) }  // Initialize shared preferences for user

    // Retrieve the user's insights from preferences
    var userInsights = userPrefs.getInsights()

    // Define food categories and their max scores
    val categories = listOf(
        "Vegetables" to 10, "Fruits" to 10, "Grains & Cereals" to 10, "Whole Grains" to 10,
        "Meat & Alternatives" to 10, "Dairy" to 10, "Water" to 5, "Unsaturated Fats" to 10,
        "Sodium" to 10, "Sugar" to 10, "Alcohol" to 5, "Discretionary Foods" to 10
    )

    // Map user scores to their respective categories
    val userScores = categories.associate { (category, _) ->
        category to (userInsights[category]?.toString()?.toFloatOrNull() ?: 0f)  // Default to 0 if score is missing
    }

    // Calculate total score based on user scores and max possible score
    val totalScoreRaw = userScores.values.sum()  // Sum of user scores
    val maxScoreRaw = categories.sumOf { it.second.toDouble() }.toFloat()  // Total max score possible
    val totalScore = if (maxScoreRaw > 0) (totalScoreRaw / maxScoreRaw) * 100 else 0f  // Calculate percentage score

    // Call the content composable with user scores and total score
    InsightsContent(userScores, totalScore, context)
}

// InsightsContent composable - Displays the content of insights including individual scores and total score
@Composable
fun InsightsContent(userScores: Map<String, Float>, totalScore: Float, context: Context) {
    Box(
        modifier = Modifier
            .fillMaxSize()  // Fill available space
            .padding(16.dp)  // Add padding around the content
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),  // Make column scrollable
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title text for insights screen
            Text(text = "Insights: Food Score", fontSize = 20.sp, modifier = Modifier.padding(bottom = 16.dp))

            // Iterate through user scores and display each category score
            userScores.forEach { (name, score) ->
                FoodScoreItem(name, score)  // Display individual food category score
            }

            Spacer(modifier = Modifier.height(8.dp))  // Add vertical space

            // Text for total score
            Text(text = "Total Food Quality Score", fontSize = 16.sp)

            // Slider displaying total score as a percentage
            Slider(
                value = totalScore,
                onValueChange = {},  // No interaction allowed (disabled slider)
                valueRange = 0f..100f,  // Value range from 0 to 100
                enabled = false,  // Slider is disabled (read-only)
                colors = SliderDefaults.colors(
                    disabledThumbColor = MaterialTheme.colorScheme.primary,
                    disabledActiveTrackColor = MaterialTheme.colorScheme.primary
                )
            )
            // Display formatted total score as percentage
            Text(text = "%.2f/100".format(totalScore))

            Spacer(modifier = Modifier.height(4.dp))  // Add vertical space

            // Button to share insights with someone
            Button(onClick = { sharingInsights(context, userScores, totalScore, 100) }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Share with someone")
            }

            Spacer(modifier = Modifier.height(4.dp))  // Add vertical space

            // Button to show a toast message (NutriCoach development status)
            Button(onClick = {
                Toast.makeText(context, "NutriCoach in Development", Toast.LENGTH_LONG).show()
            }, modifier = Modifier.fillMaxWidth()) {
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

// Preview of InsightsScreen composable (for UI design testing)
@Preview(showBackground = true)
@Composable
fun InsightsScreenPreview() {
    A1Theme {
        InsightsContent(userScores = emptyMap(), totalScore = 0f, context = LocalContext.current)  // Show empty preview
    }
}

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

    // Create an intent to share the insights text
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"  // Define the share type
        putExtra(Intent.EXTRA_TEXT, shareText)  // Attach the insights text to the intent
    }

    // Start the chooser intent to select sharing app
    val chooser = Intent.createChooser(intent, "Share your insights via:")
    context.startActivity(chooser)  // Launch the chooser
}
