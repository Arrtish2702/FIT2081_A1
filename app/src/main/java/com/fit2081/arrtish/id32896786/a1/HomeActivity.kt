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

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Retrieve userId from Intent extras
        val userId = intent.getStringExtra("user_id") ?: "default_user" // Provide a default if null
        Log.v("FIT2081-HomeActivity", "User ID: $userId")

        val sharedPreferences = UserSharedPreferences.getPreferences(this, userId)
        val isUpdated = sharedPreferences.getBoolean("updated", false) // Check if data has been updated

        Log.v("FIT2081-HomeActivity", "SharedPreferences: $sharedPreferences | Updated: $isUpdated")

        if (!isUpdated) { // If data has not been updated, fetch and save it
            Log.v("FIT2081-HomeActivity", "Adding data to SharedPreferences for user $userId")
            getUserDetailsAndSave(this, userId)
        } else {
            Log.v("FIT2081-HomeActivity", "Using existing SharedPreferences for user $userId")
        }

        Log.v("FIT2081-HomeActivity", "SharedPreferences: $sharedPreferences")

        setContent {
            HideSystemBars()
            A1Theme {
                HomePage(userId,sharedPreferences,modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun HomePage(userId: String?,sharedPreferences: SharedPreferences, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // ✅ Retrieve the "choices" JSON string and parse it
    val detailsJson = sharedPreferences.getString("insights", "{}") ?: "{}"
    val userDetails = JSONObject(detailsJson)

    val foodQualityScore = userDetails.optDouble("qualityScore", 0.0).toFloat() // ✅ Extract score correctly

    var showDialog by remember { mutableStateOf(false) }

    Log.v("FIT2081-HomeActivity", "Food Quality Score: $foodQualityScore")

    // Check if the user has answered the questionnaire
    checkForQuestionnaire(context,userId) { hasAnswered ->
        if (!hasAnswered) {
            showDialog = true
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { /* Prevent dismiss */ },
            title = { Text("Complete the Questionnaire") },
            text = { Text("You need to complete the questionnaire to continue using the app.") },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    onRouteToQuestionnaire(context, userId)
                }) {
                    Text("Complete Now")
                }
            },
            dismissButton = {
                Button(onClick = { Authentication.logout(context) }) {
                    Text(text = "Log Out")
                }
            }
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter // Ensures everything is stacked top-down
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Image at the top
            Image(
                painter = painterResource(
                    id = when {
                        foodQualityScore.toInt() >= 80 -> R.drawable.high_score_picture_removebg_preview
                        foodQualityScore.toInt() >= 40 -> R.drawable.medium_score_picture_removebg_preview
                        foodQualityScore.toInt() >= 0 -> R.drawable.low_score_picture_removebg_preview
                        else -> 0 // Invalid case (Won't render)
                    }
                ),
                contentDescription = "Food Quality Score",
                modifier = Modifier
                    .size(275.dp)
                    .align(Alignment.CenterHorizontally) // Ensures image is centered
            )

            // User Info
            Text(
                text = "Hello, ${userId ?: "Guest"}!",
                fontSize = 24.sp,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            // Score Display
            Text(
                text = "Your Food Quality Score: $foodQualityScore",
                fontSize = 20.sp,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            // Description
            Text(
                text = "Your Food Quality Score provides a snapshot of how well your eating patterns align with established food guidelines, helping you identify both strengths and opportunities for improvement in your diet.\n" +
                        "This personalized measurement considers various food groups, including vegetables, fruits, whole grains, and proteins, to give you practical insights for making healthier food choices.\n",
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )

            // Buttons
            Button(onClick = {
            onRouteToQuestionnaire(context, userId)
            }) {
                Text(text = "Edit Responses")
            }

            Button(onClick = { onRouteToInsights(context, userId) }) {
                Text(text = "View Insights")
            }

            Button(onClick = { Authentication.logout(context) }) {
                Text(text = "Log Out")
            }
        }
    }
}

fun checkForQuestionnaire(context: Context, userId: String?, callback: (Boolean) -> Unit) {
    if (userId == null) {
        callback(false) // Default to false if userId is null
        return
    }

    val sharedPreferences = UserSharedPreferences.getPreferences(context, userId)
    val answeredQuestionnaire = sharedPreferences.getBoolean("answered", false) // Check if data has been updated
    Log.v("FIT2081-Questionnaire", answeredQuestionnaire.toString())
    callback(answeredQuestionnaire)
}


fun onRouteToQuestionnaire(context: Context, userId: String?) {
    val intent = Intent(context, QuestionnaireActivity::class.java).apply{
        putExtra("user_id", userId)
    }
    context.startActivity(intent)
}

fun onRouteToInsights(context: Context, userId: String?) {
    val intent = Intent(context, InsightsActivity::class.java).apply{
        putExtra("user_id", userId)
    }
    context.startActivity(intent)
}

@Composable
internal fun HideSystemBars() {
    val systemUiController = rememberSystemUiController()
    systemUiController.isSystemBarsVisible = false  // Hides both status & nav bar
}

@Composable
internal fun ShowSystemBars() {
    val systemUiController = rememberSystemUiController()
    systemUiController.isSystemBarsVisible = true  // Shows both status & nav bar
}
