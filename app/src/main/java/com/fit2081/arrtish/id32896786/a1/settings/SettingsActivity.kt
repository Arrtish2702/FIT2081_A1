package com.fit2081.arrtish.id32896786.a1.settings

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.edit
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fit2081.arrtish.id32896786.a1.AppViewModelFactory
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.PatientRepository
import com.fit2081.arrtish.id32896786.a1.ui.theme.A1Theme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            A1Theme {

            }
        }
    }
}

@Composable
fun SettingsPage(
    userId: Int,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    patientRepository: PatientRepository
) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val viewModel: SettingsViewModel = viewModel(
        factory = AppViewModelFactory(patientRepository)
    )

    val patient by viewModel.patient.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadPatientScoresById(userId)
    }

    // Default values before patient data is loaded
    val phoneNumber = patient?.patientPhoneNumber ?: "Loading..."
    val userName = patient?.patientName ?: "Loading..."

    val onClinicianLoginClick = {
        Toast.makeText(context, "Navigating to Clinician Login", Toast.LENGTH_SHORT).show()
        // Navigate to Clinician Login screen
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Settings", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(24.dp))

        // User Info Section
        Text("ACCOUNT", fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Phone, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(phoneNumber)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Person, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(userName)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Info, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(userId.toString())
        }

        Spacer(modifier = Modifier.height(32.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        Text("OTHER SETTINGS", fontWeight = FontWeight.SemiBold)

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { navController.navigate("clinician login") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.AccountBox, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Clinician Login")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                sharedPreferences.edit() { putInt("userId", 0) }
                navController.navigate("login"){
                    popUpTo(0) { inclusive = true }
                }
                expanded = false
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Logout")
        }
    }
}

