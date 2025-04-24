package com.fit2081.arrtish.id32896786.a1.settings

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
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
fun SettingsPage(userId: Int?, modifier: Modifier = Modifier, viewModel: SettingsViewModel = viewModel()) {
    // Mock user data (replace with real data or ViewModel state later)
    val userName = remember { mutableStateOf("") }
    val phoneNumber = remember { mutableStateOf("") }
    val userId = remember { mutableStateOf("") }

    // Define button click behaviors (replace with real nav/logout logic)
    val context = LocalContext.current

    val onLogoutClick = {
        Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
        // Navigate to login screen
    }

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
            Icon(Icons.Default.Person, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(userName.value)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Phone, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(phoneNumber.value)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Info, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(userId.value)
        }

        Spacer(modifier = Modifier.height(32.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))

        Text("OTHER SETTINGS", fontWeight = FontWeight.SemiBold)

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Logout")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onClinicianLoginClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.AccountBox, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Clinician Login")
        }
    }
}

