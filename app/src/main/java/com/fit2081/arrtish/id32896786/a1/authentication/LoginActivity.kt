package com.fit2081.arrtish.id32896786.a1.authentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fit2081.arrtish.id32896786.a1.HomeActivity
import com.fit2081.arrtish.id32896786.a1.R
import com.fit2081.arrtish.id32896786.a1.authentication.AuthenticationViewModel.AuthenticationViewModelFactory
import com.fit2081.arrtish.id32896786.a1.ui.theme.A1Theme


// Main Activity for Login
class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Enables edge-to-edge display (no status bar)
    }
}

// Composable function for the login page UI
@OptIn(ExperimentalMaterial3Api::class) // Opt-in to use new Material3 components
@Composable
fun LoginPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: AuthenticationViewModel = viewModel(factory = AuthenticationViewModelFactory(LocalContext.current))
){
    var context = LocalContext.current
    var selectedUserId by remember { mutableStateOf("") } // State to store selected user ID
    var phoneNumber by remember { mutableStateOf("") } // State to store entered phone number
    var phoneNumberError by remember { mutableStateOf(false) } // State to track phone number validity
    val userIds by viewModel.patientIds.collectAsState(initial = emptyList())
    var expanded by remember { mutableStateOf(false) } // State to control dropdown menu expansion

    Box(
        modifier = modifier
            .fillMaxSize() // Fill the screen with the Box
            .padding(16.dp) // Apply padding
    ) {
        // Display logo image at the top of the screen
        Image(
            painter = painterResource(id = R.drawable.logo_for_an_app_called_nutritrack),
            contentDescription = "NutriTrack Logo",
            modifier = Modifier
                .size(200.dp) // Set logo size
                .align(Alignment.TopCenter) // Align logo to the top center
                .padding(top = 60.dp) // Apply top padding to space out the logo
        )
        // Column to arrange UI elements vertically
        Column(
            modifier = modifier.fillMaxSize().padding(16.dp), // Fill screen and add padding
            verticalArrangement = Arrangement.Center, // Center vertically
            horizontalAlignment = Alignment.CenterHorizontally // Center horizontally
        ) {
            // Display "Login" header text
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

            // Outlined text field for phone number input
            OutlinedTextField(
                value = phoneNumber, // Bind entered phone number
                onValueChange = {
                    phoneNumber = it
                    phoneNumberError = !viewModel.isValidNumber(it) // Validate phone number
                },
                label = { Text("Phone Number") }, // Label for phone input
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) }, // Phone icon
                modifier = Modifier.fillMaxWidth(), // Full width for input field
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), // Phone number keyboard
                isError = phoneNumberError, // Show error if phone number is invalid
                singleLine = true // Single line input
            )
            // Display error message if phone number is invalid
            if (phoneNumberError) {
                Text(
                    "Invalid Phone Number",
                    color = MaterialTheme.colorScheme.error, // Use error color
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp) // Apply padding for the message
                )
            }

            // Login button that triggers authentication
            Button(onClick = {
                if (selectedUserId.isNotEmpty() && phoneNumber.isNotEmpty()) {
                    if(viewModel.login(context, selectedUserId, phoneNumber)){
                        val intent = Intent(context, HomeActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK  // Clear previous activities
                            putExtra("user_id", selectedUserId)  // Pass userId to the HomeActivity
                        }
                        context.startActivity(intent)
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Please select a user ID and enter a phone number",
                        Toast.LENGTH_SHORT
                    ).show() // Show error if fields are empty
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Login") // Button label
            }

            Button(
                onClick = {
                    navController.navigate("register") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C29FC))
            ) {
                Text("Register", color = Color.White)
            }
        }
    }
}
