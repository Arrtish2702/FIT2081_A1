package com.fit2081.arrtish.id32896786.a1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit2081.arrtish.id32896786.a1.CsvExports.getUserDetailsAndSave
import com.fit2081.arrtish.id32896786.a1.ui.theme.A1Theme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.json.JSONObject

// Home Activity for the app home page
class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Enable edge-to-edge display (no status bar)

        // Get user ID passed from the previous activity, default to "default_user" if not available
        val userId = intent.getStringExtra("user_id") ?: "default_user"
        Log.v("FIT2081-HomeActivity", "User ID: $userId")

        // Get shared preferences specific to the user
        val sharedPreferences = UserSharedPreferences.getPreferences(this, userId)
        // Check if the user data is updated in the shared preferences
        val isUpdated = sharedPreferences.getBoolean("updated", false)

        Log.v("FIT2081-HomeActivity", "SharedPreferences: $sharedPreferences | Updated: $isUpdated")

        // If data is not updated, fetch user details and save it
        if (!isUpdated) {
            Log.v("FIT2081-HomeActivity", "Adding data to SharedPreferences for user $userId")
            getUserDetailsAndSave(this, userId)
        } else {
            Log.v("FIT2081-HomeActivity", "Using existing SharedPreferences for user $userId")
        }

        Log.v("FIT2081-HomeActivity", "SharedPreferences: $sharedPreferences")

        // Set up the UI content for the Home page
        setContent {
            HideSystemBars() // Hide system bars for a full-screen experience
            A1Theme { // Apply the app's theme
                // Display the HomePage composable with user ID and shared preferences
                HomePage(userId, sharedPreferences, modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun HomePage(userId: String?, sharedPreferences: SharedPreferences, modifier: Modifier = Modifier) {
    val context = LocalContext.current // Get the current context

    // Retrieve the user's insights data (food quality score)
    val detailsJson = sharedPreferences.getString("insights", "{}") ?: "{}"
    val userDetails = JSONObject(detailsJson)

    // Extract food quality score from the JSON (default to 0.0 if not found)
    val foodQualityScore = userDetails.optDouble("qualityScore", 0.0).toFloat()

    var showDialog by remember { mutableStateOf(false) } // State to control dialog visibility

    Log.v("FIT2081-HomeActivity", "Food Quality Score: $foodQualityScore")

    // Check if the user has completed the questionnaire
    checkForQuestionnaire(context, userId) { hasAnswered ->
        if (!hasAnswered) {
            showDialog = true // Show dialog if questionnaire has not been answered
        }
    }

    // Display dialog prompting the user to complete the questionnaire if needed
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { /* Prevent dismiss */ },
            title = { Text("Complete the Questionnaire") },
            text = { Text("You need to complete the questionnaire to continue using the app.") },
            confirmButton = {
                Button(onClick = {
                    showDialog = false // Close dialog
                    onRouteToQuestionnaire(context, userId) // Navigate to questionnaire
                }) {
                    Text("Complete Now")
                }
            },
            dismissButton = {
                Button(onClick = { Authentication.logout(context) }) {
                    Text(text = "Log Out") // Log out button
                }
            }
        )
    }

    // Box layout to hold all UI elements
    Box(
        modifier = Modifier
            .fillMaxSize() // Fill the screen
            .padding(16.dp), // Add padding
        contentAlignment = Alignment.TopCenter // Align content to the top center
    ) {
        // Column to arrange elements vertically
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp), // Space between elements
            modifier = Modifier.fillMaxSize()
        ) {
            // Display image based on food quality score
            Image(
                painter = painterResource(
                    id = when {
                        foodQualityScore.toInt() >= 80 -> R.drawable.high_score_picture_removebg_preview
                        foodQualityScore.toInt() >= 40 -> R.drawable.medium_score_picture_removebg_preview
                        foodQualityScore.toInt() >= 0 -> R.drawable.low_score_picture_removebg_preview
                        else -> 0 // Default image if no match
                    }
                ),
                contentDescription = "Food Quality Score", // Image description
                modifier = Modifier
                    .size(275.dp) // Set image size
                    .align(Alignment.CenterHorizontally) // Center align the image
            )

            // Display greeting with the user's ID
            Text(
                text = "Hello, ${userId ?: "Guest"}!", // If userId is null, show "Guest"
                fontSize = 24.sp, // Font size for greeting
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center // Center align the text
            )

            // Display food quality score
            Text(
                text = "Your Food Quality Score: $foodQualityScore",
                fontSize = 20.sp, // Font size for score display
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            // Provide detailed explanation about the food quality score
            Text(
                text = "Your Food Quality Score provides a snapshot of how well your eating patterns align with established food guidelines, helping you identify both strengths and opportunities for improvement in your diet.\n" +
                        "This personalized measurement considers various food groups, including vegetables, fruits, whole grains, and proteins, to give you practical insights for making healthier food choices.\n",
                fontSize = 12.sp, // Font size for explanation
                textAlign = TextAlign.Center
            )

            // Button to edit questionnaire responses
            Button(onClick = {
                onRouteToQuestionnaire(context, userId) // Navigate to questionnaire
            }) {
                Text(text = "Edit Responses")
            }

            // Button to view insights
            Button(onClick = { onRouteToInsights(context, userId) }) {
                Text(text = "View Insights")
            }

            // Button to log out
            Button(onClick = { Authentication.logout(context) }) {
                Text(text = "Log Out")
            }
        }
    }
}

// Function to check if the user has answered the questionnaire
fun checkForQuestionnaire(context: Context, userId: String?, callback: (Boolean) -> Unit) {
    if (userId == null) {
        callback(false) // Return false if no user ID is available
        return
    }

    // Retrieve shared preferences for the specific user
    val sharedPreferences = UserSharedPreferences.getPreferences(context, userId)
    val answeredQuestionnaire = sharedPreferences.getBoolean("answered", false) // Check if questionnaire is answered
    Log.v("FIT2081-Questionnaire", answeredQuestionnaire.toString())
    callback(answeredQuestionnaire) // Return the result to the callback
}

// Function to navigate to the questionnaire activity
fun onRouteToQuestionnaire(context: Context, userId: String?) {
    val intent = Intent(context, QuestionnaireActivity::class.java).apply {
        putExtra("user_id", userId) // Pass user ID to the next activity
    }
    context.startActivity(intent) // Start the activity
}

// Function to navigate to the insights activity
fun onRouteToInsights(context: Context, userId: String?) {
    val intent = Intent(context, InsightsActivity::class.java).apply {
        putExtra("user_id", userId) // Pass user ID to the next activity
    }
    context.startActivity(intent) // Start the activity
}

// Composable function to hide system bars
@Composable
internal fun HideSystemBars() {
    val systemUiController = rememberSystemUiController() // Get system UI controller
    systemUiController.isSystemBarsVisible = false // Hide the system bars
}

// Composable function to show system bars
@Composable
internal fun ShowSystemBars() {
    val systemUiController = rememberSystemUiController() // Get system UI controller
    systemUiController.isSystemBarsVisible = true // Show the system bars
}
