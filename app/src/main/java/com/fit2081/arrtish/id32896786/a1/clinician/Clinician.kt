package com.fit2081.arrtish.id32896786.a1.clinician

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fit2081.arrtish.id32896786.a1.AppViewModelFactory
import com.fit2081.arrtish.id32896786.a1.authentication.login.LoginViewModel
import com.fit2081.arrtish.id32896786.a1.ui.theme.A1Theme


@Composable
fun ClinicianLogin(
    navController: NavHostController,
    viewModelFactory: AppViewModelFactory
) {
    val viewModel: LoginViewModel = viewModel(factory = viewModelFactory)
    var context = LocalContext.current

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
                .height(48.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Clinician Login", color = Color.White)
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
    val viewModel: ClinicianViewModel = viewModel(factory = viewModelFactory)

    val avgScores by viewModel.generateAvgScores.observeAsState(Pair(0f, 0f))

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

        Button(
            onClick = { /* trigger data pattern generation */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A))
        ) {
            Text("Find Data Pattern", color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))

        val insights = listOf(
            "Variable Water Intake: Consumption of water varies greatly among users...",
            "Low Wholegrain Consumption: Intake of wholegrains appears generally low...",
            "Potential Gender Difference in HEIFA Scoring: The data includes columns for both..."
        )

        insights.forEach { insight ->
            Text("• $insight", fontSize = 14.sp, modifier = Modifier.padding(bottom = 12.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { navController.navigate("home") },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
        ) {
            Text("Done", color = Color.White)
        }
    }
}

