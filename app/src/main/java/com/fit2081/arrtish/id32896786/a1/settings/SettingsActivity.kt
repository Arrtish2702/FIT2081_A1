package com.fit2081.arrtish.id32896786.a1.settings

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fit2081.arrtish.id32896786.a1.MainActivity
import com.fit2081.arrtish.id32896786.a1.databases.AppDataBase
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.PatientRepository
import com.fit2081.arrtish.id32896786.a1.ui.theme.A1Theme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            A1Theme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
            }
        }
    }
}

@Composable
fun SettingsPage(userId: Int, modifier: Modifier = Modifier, navController: NavHostController) {
    var expanded by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val db = AppDataBase.getDatabase(context)
    val repository = PatientRepository(db.patientDao())
    val viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.SettingsViewModelFactory(repository)
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
                val intent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                context.startActivity(intent)
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

