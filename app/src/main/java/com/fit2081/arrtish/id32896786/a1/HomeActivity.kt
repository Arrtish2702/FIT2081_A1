package com.fit2081.arrtish.id32896786.a1

import UserSharedPreferences
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit2081.arrtish.id32896786.a1.ui.theme.A1Theme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Retrieve userId from Intent extras
        val userId = intent.getStringExtra("user_id")

        setContent {
            HideSystemBars()
            A1Theme {
                HomePage(userId,modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePagePreview() {
    HomePage(userId = "Guest",modifier = Modifier.fillMaxSize())
}

@Composable
fun HomePage(userId: String?, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val userPrefs = userId?.let { UserSharedPreferences(context, it) }
    val foodQualityScore = userPrefs?.getUserChoices()?.get("foodScore")?.toString() ?: "0"

    var showDialog by remember { mutableStateOf(false) }

    // Check if the user has answered the questionnaire
    checkForQuestionnaire(context,userId) { hasAnswered ->
        if (!hasAnswered) {
            showDialog = true
        }
    }

//    if (showDialog) {
//        AlertDialog(
//            onDismissRequest = { /* Prevent dismiss */ },
//            title = { Text("Complete the Questionnaire") },
//            text = { Text("You need to complete the questionnaire to continue using the app.") },
//            confirmButton = {
//                Button(onClick = {
//                    showDialog = false
//                    onRouteToQuestionnaire(context)
//                }) {
//                    Text("Complete Now")
//                }
//            }
//        )
//    }
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
                    .size(300.dp)
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
                text = "This score represents the overall quality of your food choices based on your responses.",
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            // Buttons
            Button(onClick = {
//            onRouteToQuestionnaire(context)
            }) {
                Text(text = "Edit Responses")
            }

            Button(onClick = { onRouteToInsights(context) }) {
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

    val userPreferences = UserSharedPreferences(context, userId)
    val answeredQuestionnaire = userPreferences.getUserChoices()?.get("answered") as? Boolean ?: false
    callback(answeredQuestionnaire)
}
//
//fun onRouteToQuestionnaire(context: Context) {
//    val intent = Intent(context, QuestionnaireActivity::class.java)
//    context.startActivity(intent)
//}

fun onRouteToInsights(context: Context) {
    val intent = Intent(context, InsightsActivity::class.java)
    context.startActivity(intent)
}

@Composable
internal fun HideSystemBars() {
    val systemUiController = rememberSystemUiController()
    systemUiController.isSystemBarsVisible = false  // Hides both status & nav bar
}
