package com.fit2081.arrtish.id32896786.a1.internalpages.home
/**
 * Disclaimer:
 * This file may include comments or documentation assisted by OpenAI's GPT model.
 * All code logic and architectural decisions were implemented and verified by the developer.
 */
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fit2081.arrtish.id32896786.a1.AppViewModelFactory
import com.fit2081.arrtish.id32896786.a1.R

/**
 * HomePage Composable
 *
 * Displays the home screen with personalized nutrition summary for the logged-in user.
 * It shows the user's name, Food Quality Score, descriptive text, and relevant image based on the score.
 * Includes buttons to navigate to the questionnaire and detailed insights screens.
 *
 * @param userId The ID of the current user to load their data.
 * @param modifier Optional Compose Modifier for styling the layout.
 * @param navController Navigation controller used to navigate to other screens.
 * @param viewModelFactory Factory to provide the HomeViewModel instance.
 */
@Composable
fun HomePage(
    userId: Int,
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModelFactory: AppViewModelFactory
) {
    // Obtain the HomeViewModel instance scoped to this Composable, using the provided factory
    val viewModel: HomeViewModel = viewModel(factory = viewModelFactory)

    // Observe the patient LiveData from ViewModel as Compose State for recomposition on change
    val patient by viewModel.patient.observeAsState()

    // Remember scroll state for vertical scrolling of content
    val scrollState = rememberScrollState()

    // Trigger loading of patient data when this Composable enters composition
    viewModel.loadPatientDataById(userId)

    // Only display content if patient data is loaded (non-null)
    patient?.let { patientData ->

        // Extract relevant patient info
        val foodQualityScore = patientData.totalScore
        val name = patientData.patientName

        // Main container box filling the entire screen with padding, top center alignment
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            // Vertical column layout for screen contents, scrollable and spaced
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                // Top row aligned to the end with a button to navigate to questionnaire screen
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            navController.navigate("questionnaire")
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Questionnaire")
                    }
                }

                // Display an image reflecting the food quality score range
                Image(
                    painter = painterResource(
                        id = when {
                            foodQualityScore.toInt() >= 80 -> R.drawable.high_score_picture_removebg_preview
                            foodQualityScore.toInt() >= 40 -> R.drawable.medium_score_picture_removebg_preview
                            foodQualityScore.toInt() >= 0 -> R.drawable.low_score_picture_removebg_preview
                            else -> 0  // Fallback if score is invalid
                        }
                    ),
                    contentDescription = "Food Quality Score",
                    modifier = Modifier
                        .size(275.dp)
                        .align(Alignment.CenterHorizontally)
                )

                // Greeting Text with patient's name
                Text(
                    text = "Hello, ${name}!",
                    fontSize = 24.sp,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )

                // Display the Food Quality Score numerically
                Text(
                    text = "Your Food Quality Score: $foodQualityScore",
                    fontSize = 20.sp,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )

                // Informational description about the Food Quality Score meaning
                Text(
                    text = "Your Food Quality Score provides a snapshot of how well your eating patterns align with established food guidelines, helping you identify both strengths and opportunities for improvement in your diet.\n" +
                            "This personalized measurement considers various food groups, including vegetables, fruits, whole grains, and proteins, to give you practical insights for making healthier food choices.\n",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )

                // Button to navigate to the insights screen showing all scores
                Button(
                    onClick = {
                        navController.navigate("insights")
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Show all Scores")
                }
            }
        }
    }
}