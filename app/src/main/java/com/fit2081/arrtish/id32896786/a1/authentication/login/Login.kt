package com.fit2081.arrtish.id32896786.a1.authentication.login

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
import com.fit2081.arrtish.id32896786.a1.authentication.AuthenticationViewModel


// Composable function for the login page UI
@OptIn(ExperimentalMaterial3Api::class) // Opt-in to use new Material3 components
@Composable
fun LoginPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModelFactory: AppViewModelFactory
){
    var context = LocalContext.current
    val viewModel: AuthenticationViewModel = viewModel(factory = viewModelFactory)
    var selectedUserId by remember { mutableStateOf("") }
    val userIds by viewModel.registeredPatientIds.observeAsState(initial = emptyList())
    var expanded by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    val loginMessage by viewModel.loginMessage
    val isLoading = viewModel.isLoading.value

    LaunchedEffect(loginMessage) {
        loginMessage?.let {
            if (it.isNotBlank()) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.loginMessage.value = "" // Reset message
            }
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f)), // Optional dim
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp), // Fill screen and add padding
        verticalArrangement = Arrangement.Top, // Center vertically
        horizontalAlignment = Alignment.CenterHorizontally // Center horizontally
    ) {
        // Display "Login" header text
        Image(
            painter = painterResource(id = R.drawable.logo_for_an_app_called_nutritrack),
            contentDescription = "NutriTrack Logo",
            modifier = Modifier
                .size(200.dp) // Set logo size
//                    .align(Alignment.TopCenter) // Align logo to the top center
                .padding(top = 60.dp) // Apply top padding to space out the logo
        )

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            "Login",
            style = MaterialTheme.typography.headlineLarge, // Use the large headline style
            fontWeight = FontWeight.Bold // Set bold font weight
        )

        // Exposed dropdown menu for user selection
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }) { // Toggle dropdown expansion on click
            OutlinedTextField(
                value = selectedUserId, // Bind selected user ID to text field
                onValueChange = {}, // No action on value change, read-only field
                label = { Text("User ID") }, // Label for the input
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }, // Person icon
                trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) }, // Dropdown icon
                readOnly = true, // Make the field read-only
                modifier = Modifier.fillMaxWidth().menuAnchor() // Full width and position dropdown
            )

            // Dropdown menu that shows all user IDs
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                userIds.forEach { userId -> // Loop through user IDs
                    DropdownMenuItem(text = { Text(userId.toString()) }, onClick = {
                        selectedUserId = userId.toString() // Set selected user ID
                        expanded = false // Close dropdown
                    })
                }
            }
        }

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            placeholder = { Text("Enter your password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Login button that triggers authentication
        Button(onClick = {
            if (selectedUserId.isNotEmpty() && password.isNotEmpty()) {
                Log.v(MainActivity.TAG, "input user: $selectedUserId")
                Log.v(MainActivity.TAG, "password on login: $password")
                viewModel.loginSuccessful.value = false
                viewModel.appLogin(selectedUserId, password, navController, context)
            } else {
                Toast.makeText(
                    context,
                    "Please select a user ID and enter a phone number",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Login") // Button label
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                navController.navigate("forgotPassword") {
                    popUpTo("login") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Change Password")
        }

        Spacer(modifier = Modifier.height(12.dp))

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