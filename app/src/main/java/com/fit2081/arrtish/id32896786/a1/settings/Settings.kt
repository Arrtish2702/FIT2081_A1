package com.fit2081.arrtish.id32896786.a1.settings

import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fit2081.arrtish.id32896786.a1.AppViewModelFactory
import com.fit2081.arrtish.id32896786.a1.MainActivity
import com.fit2081.arrtish.id32896786.a1.MainViewModel
import com.fit2081.arrtish.id32896786.a1.authentication.AuthManager
import com.fit2081.arrtish.id32896786.a1.questionnaire.QuestionnaireViewModel
import com.fit2081.arrtish.id32896786.a1.ui.theme.A1Theme


@Composable
fun SettingsPage(
    userId: Int,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    isDarkTheme: MutableState<Boolean>,
    viewModelFactory: AppViewModelFactory

) {
    var expanded by remember { mutableStateOf(false) }
    var context = LocalContext.current
    val settingsViewModel: SettingsViewModel = viewModel(factory = viewModelFactory)
    val mainViewModel: MainViewModel = viewModel(factory = viewModelFactory)

    val patient by settingsViewModel.patient.observeAsState()

    LaunchedEffect(userId) {
        settingsViewModel.loadPatientDataById(userId)
    }

    // Default values before patient data is loaded
    val phoneNumber = patient?.patientPhoneNumber ?: "Loading..."
    val userName = patient?.patientName ?: "Loading..."

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp, bottom = 128.dp, start = 16.dp, end = 16.dp)
    ) {
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

            Text("Dark Mode")
            Switch(
                checked = isDarkTheme.value,
                onCheckedChange = { isDark ->
                    isDarkTheme.value = isDark
                    mainViewModel.saveThemePreference(context, isDark)
                }
            )


            Spacer(Modifier.width(8.dp))

            Button(
                onClick = {
                    Toast.makeText(context, "Navigating to Clinician Login", Toast.LENGTH_SHORT)
                        .show()
                    navController.navigate("clinician login")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.AccountBox, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Clinician Login")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    AuthManager.logout(context)
                    Log.v(MainActivity.TAG, "userID on logout: $userId")
                    navController.navigate("login") {
                        popUpTo("settings") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Logout")
            }
        }
    }
}

