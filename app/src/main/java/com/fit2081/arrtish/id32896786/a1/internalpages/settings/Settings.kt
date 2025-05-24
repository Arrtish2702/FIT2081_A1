package com.fit2081.arrtish.id32896786.a1.internalpages.settings
/**
 * Disclaimer:
 * This file may include comments or documentation assisted by OpenAI's GPT model.
 * All code logic and architectural decisions were implemented and verified by the developer.
 */
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fit2081.arrtish.id32896786.a1.AppViewModelFactory
import com.fit2081.arrtish.id32896786.a1.MainActivity
import com.fit2081.arrtish.id32896786.a1.MainViewModel
import com.fit2081.arrtish.id32896786.a1.authentication.AuthManager

/**
 * Displays the user settings screen with account information, theme settings,
 * navigation to clinician login and password change, and logout functionality.
 *
 * @param userId ID of the current user
 * @param modifier Modifier for UI styling
 * @param navController Controller for navigating between screens
 * @param isDarkTheme Mutable state that controls dark mode setting
 * @param viewModelFactory Factory to inject ViewModel dependencies
 */
@Composable
fun SettingsPage(
    userId: Int,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    isDarkTheme: MutableState<Boolean>,
    viewModelFactory: AppViewModelFactory
) {
    val context = LocalContext.current

    // Initialize ViewModels with dependency factory
    val settingsViewModel: SettingsViewModel = viewModel(factory = viewModelFactory)
    val mainViewModel: MainViewModel = viewModel(factory = viewModelFactory)

    // Observe current patient's LiveData
    val patient by settingsViewModel.patient.observeAsState()

    // Trigger data load once on composition
    LaunchedEffect(userId) {
        settingsViewModel.loadPatientDataById(userId)
    }

    // Fallback values during patient data loading
    val phoneNumber = patient?.patientPhoneNumber ?: "Loading..."
    val userName = patient?.patientName ?: "Loading..."

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp, bottom = 128.dp, start = 16.dp, end = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Page title
            Text("Settings", fontSize = 24.sp, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(24.dp))

            // =====================
            // Section: Account Info
            // =====================
            Text("ACCOUNT", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))

            // Display phone number
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Phone, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(phoneNumber)
            }

            // Display user name
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(userName)
            }

            // Display user ID
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(userId.toString())
            }

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Section: Other Preferences
            Text("OTHER SETTINGS", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(12.dp))

            // Toggle: Dark mode setting
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Dark Mode")
                Spacer(modifier = Modifier.width(12.dp))
                Switch(
                    checked = isDarkTheme.value,
                    onCheckedChange = { isDark ->
                        isDarkTheme.value = isDark
                        mainViewModel.saveThemePreference(context, isDark)
                    }
                )
            }

            Spacer(Modifier.width(8.dp))

            // Button: Navigate to clinician login screen
            Button(
                onClick = {
                    Toast.makeText(context, "Navigating to Clinician Login", Toast.LENGTH_SHORT).show()
                    navController.navigate("clinician login")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.AccountBox, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Clinician Login")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Button: Navigate to change password screen
            Button(
                onClick = {
                    navController.navigate("changePassword")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Lock, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Change Password")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Button: Logout and return to login screen
            Button(
                onClick = {
                    AuthManager.logout(context)
                    Log.v(MainActivity.TAG, "userID on logout: $userId")
                    navController.navigate("login") {
                        popUpTo("settings") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Logout")
            }
        }
    }
}