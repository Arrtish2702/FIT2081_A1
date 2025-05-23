package com.fit2081.arrtish.id32896786.a1.internalpages.clinician

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fit2081.arrtish.id32896786.a1.AppViewModelFactory
import com.fit2081.arrtish.id32896786.a1.authentication.AuthenticationViewModel


/**
 * ClinicianLogin Composable
 *
 * Displays a login screen for clinicians with a text input field for entering clinician key.
 * On login button press, attempts to authenticate via AuthenticationViewModel.
 * If successful, navigates to "clinician" screen; otherwise shows a Toast error message.
 *
 * @param navController NavController used for navigation between screens
 * @param viewModelFactory Factory for providing ViewModel instances
 */
@Composable
fun ClinicianLogin(
    navController: NavHostController,
    viewModelFactory: AppViewModelFactory
) {
    // Get AuthenticationViewModel from factory
    val viewModel: AuthenticationViewModel = viewModel(factory = viewModelFactory)
    val context = LocalContext.current

    // Mutable state holding clinician key input
    var clinicianKey by remember { mutableStateOf("") }

    // Scroll state for vertical scrolling if content overflows
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title Text
        Text("Clinician Login", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(24.dp))

        // Input field for clinician key
        OutlinedTextField(
            value = clinicianKey,
            onValueChange = { clinicianKey = it },
            label = { Text("Enter your clinician key") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Login button triggers clinician login attempt
        Button(
            onClick = {
                if (viewModel.clinicianLogin(clinicianKey)) {
                    // Navigate to clinician dashboard on success
                    navController.navigate("clinician")
                } else {
                    // Show error toast on failure
                    Toast.makeText(context, "Invalid clinician key", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Clinician Login")
        }
    }
}



/**
 * ClinicianPage Composable
 *
 * Displays the clinician dashboard showing:
 * - Average HEIFA scores by gender
 * - Insight generation requirements
 * - Button to generate data pattern insights
 * - Displays generated insights or loading indicator
 * - "Done" button to navigate back home
 *
 * @param userId The logged-in clinician's user ID (currently unused in this UI)
 * @param modifier Modifier to be applied to the root Column composable
 * @param navController NavController used for navigation
 * @param viewModelFactory Factory for providing ViewModel instances
 */
@Composable
fun ClinicianPage(
    userId: Int,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModelFactory: AppViewModelFactory
) {
    val context = LocalContext.current
    // Obtain ClinicianViewModel from factory
    val viewModel: ClinicianViewModel = viewModel(factory = viewModelFactory)

    // Observe loading state LiveData
    val isLoading by viewModel.isLoading.observeAsState(initial = false)

    // Observe average HEIFA scores LiveData; default to (0f, 0f)
    val avgScores by viewModel.generateAvgScores.observeAsState(Pair(0f, 0f))

    // Observe generated insights LiveData; default empty list
    val insights by viewModel.patterns.observeAsState(initial = emptyList())

    // Scroll state for vertical scrolling
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp)
    ) {
        // Dashboard title
        Text("Clinician Dashboard", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        // Display average HEIFA scores for male and female patients
        Text("Average HEIFA (Male): ${avgScores.first}", fontSize = 16.sp)
        Text("Average HEIFA (Female): ${avgScores.second}", fontSize = 16.sp)

        Spacer(modifier = Modifier.height(24.dp))

        // Card showing requirements to generate insights
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Insight Generation Requirements", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = """
                    • At least 3 registered patients
                    • At least 1 male and 1 female patient
                    • At least some food intake data logged
                    
                    Insights will only be generated once these criteria are met.
                    """.trimIndent(),
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Button to trigger generation of data pattern insights
        Button(
            onClick = { viewModel.generateInterestingPatterns(context) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Find Data Pattern")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Content area showing loading indicator, message, or insights
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 100.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                }
            }
            insights.isEmpty() -> {
                Text(
                    "No insights available. Press 'Find Data Pattern' to generate insights.",
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                )
            }
            else -> {
                // Display each insight in a bulleted list
                insights.forEach { insight ->
                    Text("• $insight", fontSize = 14.sp, modifier = Modifier.padding(bottom = 12.dp))
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // "Done" button navigates back to home screen
        Button(
            onClick = { navController.navigate("home") },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Done")
        }
    }
}