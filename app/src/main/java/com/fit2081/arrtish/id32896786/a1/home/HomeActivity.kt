package com.fit2081.arrtish.id32896786.a1.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fit2081.arrtish.id32896786.a1.AppViewModelFactory
import com.fit2081.arrtish.id32896786.a1.R
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.PatientRepository

// Home Activity for the app home page
class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Enable edge-to-edge display (no status bar)

    }
}

@Composable
fun HomePage(userId: Int, modifier: Modifier = Modifier, patientRepository: PatientRepository) {


    val viewModel: HomeViewModel = viewModel(
        factory = AppViewModelFactory(patientRepository)
    )

    LaunchedEffect(Unit) {
        viewModel.loadPatientDataById(userId)
    }

    val patient by viewModel.patient.collectAsState()

    patient?.let { patientData ->
        val foodQualityScore = patientData.totalScore
        val name = patientData.patientName

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
                    text = "Hello, ${name ?: "Guest"}!", // If userId is null, show "Guest"
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
    //                onRouteToQuestionnaire(context, userId) // Navigate to questionnaire
                }) {
                    Text(text = "Edit Responses")
                }
            }
        }
    }
}