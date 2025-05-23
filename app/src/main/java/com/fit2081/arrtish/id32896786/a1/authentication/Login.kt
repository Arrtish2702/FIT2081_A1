package com.fit2081.arrtish.id32896786.a1.authentication

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fit2081.arrtish.id32896786.a1.AppViewModelFactory
import com.fit2081.arrtish.id32896786.a1.MainActivity
import com.fit2081.arrtish.id32896786.a1.R


/**
 * Composable function for displaying the Login Page.
 *
 * @param modifier Modifier for styling and layout
 * @param navController NavController for navigation between screens
 * @param viewModelFactory Factory to provide AuthenticationViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModelFactory: AppViewModelFactory
){
    val context = LocalContext.current

    // Obtain AuthenticationViewModel instance using the provided factory
    val viewModel: AuthenticationViewModel = viewModel(factory = viewModelFactory)

    // Observe LiveData from ViewModel for user IDs, selected ID, password, login message and loading state
    val userIds by viewModel.registeredPatientIds.observeAsState(initial = emptyList())
    val selectedUserId by viewModel.selectedUserId
    var expanded by remember { mutableStateOf(false) } // Dropdown menu state for user selection
    val password by viewModel.password

    val loginMessage by viewModel.loginMessage
    val isLoading = viewModel.isLoading.value

    val scrollState = rememberScrollState()

    // Show toast messages on login status changes
    LaunchedEffect(loginMessage) {
        loginMessage?.let {
            if (it.isNotBlank()) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.loginMessage.value = ""  // Reset login message after showing
            }
        }
    }

    // Display loading overlay with spinner while login is in progress
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    // Main UI column containing logo, login form and buttons
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App logo image at the top of the page
        Image(
            painter = painterResource(id = R.drawable.logo_for_an_app_called_nutritrack),
            contentDescription = "NutriTrack Logo",
            modifier = Modifier
                .size(200.dp)
                .padding(top = 60.dp)
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Header text "Login"
        Text(
            "Login",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        // Dropdown menu for selecting registered User ID
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedUserId,
                onValueChange = {},
                label = { Text("User ID") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) },
                readOnly = true,
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                userIds.forEach { userId ->
                    DropdownMenuItem(
                        text = { Text(userId.toString()) },
                        onClick = {
                            viewModel.updateSelectedUserId(userId.toString()) // Update selected user ID in ViewModel
                            expanded = false
                        }
                    )
                }
            }
        }

        // Password input field
        OutlinedTextField(
            value = password,
            onValueChange = { viewModel.updatePassword(it) },
            label = { Text("Password") },
            placeholder = { Text("Enter your password") },
            visualTransformation = PasswordVisualTransformation(), // Hide password text
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Login button triggers authentication
        Button(
            onClick = {
                if (selectedUserId.isNotEmpty() && password.isNotEmpty()) {
                    Log.v(MainActivity.TAG, "input user: $selectedUserId")
                    Log.v(MainActivity.TAG, "password on login: $password")
                    viewModel.loginSuccessful.value = false
                    viewModel.appLogin(selectedUserId, password, navController, context)
                } else {
                    Toast.makeText(context, "Please select a user ID and enter a phone number", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Navigate to Forgot Password screen
        Button(
            onClick = {
                navController.navigate("forgotPassword") {
                    popUpTo("login") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Forgot your Password?")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Navigate to Register screen for new users
        Button(
            onClick = {
                navController.navigate("register") {
                    popUpTo("login") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("New to this app? Register here")
        }
    }
}