package com.fit2081.arrtish.id32896786.a1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.fit2081.arrtish.id32896786.a1.ui.theme.A1Theme
import androidx.compose.material3.Button
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.BufferedReader
import java.io.InputStreamReader

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            A1Theme {
                HomePage(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePage(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val userDetails = remember { getUserDetails(context) }
    val firstName = userDetails?.first ?: "Unknown"
    val lastName = userDetails?.second ?: "Unknown"
    val foodQualityScore = userDetails?.third ?: "Unknown"
    var showDialog by remember { mutableStateOf(false) }

    // Check if the user has answered the questionnaire
    checkForQuestionnaire(context) { hasAnswered ->
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
                    onRouteToQuestionnaire(context) // Route to the questionnaire
                }) {
                    Text("Complete Now")
                }
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                if (foodQualityScore >= 80.toString()) {
                    Image(
                        painter = painterResource(id = R.drawable.high_score_picture_removebg_preview),
                        contentDescription = "High Food Quality Score",
                        modifier = Modifier.size(300.dp)
                    )
                } else if (foodQualityScore >= 40.toString()) {
                    Image(
                        painter = painterResource(id = R.drawable.medium_score_picture_removebg_preview),
                        contentDescription = "Medium Food Quality Score",
                        modifier = Modifier.size(300.dp)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.low_score_picture_removebg_preview),
                        contentDescription = "Low Food Quality Score",
                        modifier = Modifier.size(300.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Hello, $firstName!",
                fontSize = 24.sp,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Your Food Quality Score: $foodQualityScore",
                fontSize = 20.sp,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "This score represents the overall quality of your food choices based on your responses.",
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { onRouteToQuestionnaire(context) }) {
                Text(text = "Edit Responses")
            }
        }
    }
}



fun getUserDetails(context: Context): Triple<String, String, String>? {
    val sharedPreferences = context.getSharedPreferences("assignment_1", Context.MODE_PRIVATE)
    val savedUserId = sharedPreferences.getString("user_id", null)
    val assets = context.assets
    try {
        val inputStream = assets.open("nutritrack_data.csv")
        val reader = BufferedReader(InputStreamReader(inputStream))

        reader.useLines { lines ->
            lines.drop(1).forEach { line -> // Skip header
                val values = line.split(",")
                if (values.size >= 4 && values[1].trim() == savedUserId) { // Match user ID
                    val firstName = values[2].trim()
                    val lastName = values[3].trim()
                    val foodScoreByGender = if (values[4].trim() == "Male") {
                        values[5].trim()
                    } else {
                        values[6].trim()
                    }

                    return Triple(firstName, lastName, foodScoreByGender)
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null // Return null if not found
}

// Updated checkForQuestionnaire function
fun checkForQuestionnaire(context: Context, callback: (Boolean) -> Unit) {
    val sharedPreferences = context.getSharedPreferences("assignment_1", Context.MODE_PRIVATE)
    val answeredQuestionnaire = sharedPreferences.getBoolean("answered", false)
    callback(answeredQuestionnaire) // Call the callback with the result
}

fun onRouteToQuestionnaire(context: Context) {
    val intent = Intent(context, QuestionnaireActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(intent)
}