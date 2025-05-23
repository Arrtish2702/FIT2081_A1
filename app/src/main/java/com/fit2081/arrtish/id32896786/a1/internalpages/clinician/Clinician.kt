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


@Composable
fun ClinicianLogin(
    navController: NavHostController,
    viewModelFactory: AppViewModelFactory
) {
    val viewModel: AuthenticationViewModel = viewModel(factory = viewModelFactory)
    val context = LocalContext.current

    var clinicianKey by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Clinician Login", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = clinicianKey,
            onValueChange = { clinicianKey = it },
            label = { Text("Enter your clinician key") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (viewModel.clinicianLogin(clinicianKey)) {
                    navController.navigate("clinician")
                } else {
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



@Composable
fun ClinicianPage(
    userId: Int,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModelFactory: AppViewModelFactory
) {
    val context = LocalContext.current
    val viewModel: ClinicianViewModel = viewModel(factory = viewModelFactory)
    val isLoading by viewModel.isLoading.observeAsState(initial = false)
    val avgScores by viewModel.generateAvgScores.observeAsState(Pair(0f, 0f))

    val insights by viewModel.patterns.observeAsState(initial = emptyList())
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp)
    ) {
        Text("Clinician Dashboard", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Average HEIFA (Male): ${avgScores.first}", fontSize = 16.sp)
        Text("Average HEIFA (Female): ${avgScores.second}", fontSize = 16.sp)

        Spacer(modifier = Modifier.height(24.dp))

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

        Button(
            onClick = { viewModel.generateInterestingPatterns(context) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Find Data Pattern")
        }

        Spacer(modifier = Modifier.height(24.dp))

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
                    color = Color.Gray
                )
            }

            else -> {
                insights.forEach { insight ->
                    Text("• $insight", fontSize = 14.sp, modifier = Modifier.padding(bottom = 12.dp))
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { navController.navigate("home") },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Done", color = Color.White)
        }
    }
}