package com.fit2081.arrtish.id32896786.a1

import android.content.Context
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fit2081.arrtish.id32896786.a1.CsvExports.extractUserIdsFromCSV
import com.fit2081.arrtish.id32896786.a1.ui.theme.A1Theme

// Main Activity for Login
class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Enables edge-to-edge display (no status bar)
        Authentication.init(this) // Initialize authentication object
        setContent {
            A1Theme { // Apply the app's theme to the login page
                LoginPage(context = this, modifier = Modifier.fillMaxSize()) // Display the login page
            }
        }
    }
}

// Composable function for the login page UI
@OptIn(ExperimentalMaterial3Api::class) // Opt-in to use new Material3 components
@Composable
fun LoginPage(context: Context, modifier: Modifier = Modifier) {
    var selectedUserId by remember { mutableStateOf("") } // State to store selected user ID
    var phoneNumber by remember { mutableStateOf("") } // State to store entered phone number
    var phoneNumberError by remember { mutableStateOf(false) } // State to track phone number validity
    val userIds = remember { extractUserIdsFromCSV(context) } // Get user IDs from CSV file
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
                        DropdownMenuItem(text = { Text(userId) }, onClick = {
                            selectedUserId = userId // Set selected user ID
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
                    phoneNumberError = !isValidNumber(it) // Validate phone number
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
                    Authentication.login(context, selectedUserId, phoneNumber) // Perform login
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
        }
    }
}

// Function to validate phone number (supports Australian and Malaysian numbers)
fun isValidNumber(number: String): Boolean {
    val aussieRegex = Regex("^(61[2-478]\\d{8})$") // Australian phone number regex
    val malaysianRegex = Regex("^(60[1-9]\\d{7,9})$") // Malaysian phone number regex
    return aussieRegex.matches(number) || malaysianRegex.matches(number) // Return true if valid
}