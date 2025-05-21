package com.fit2081.arrtish.id32896786.a1.authentication

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fit2081.arrtish.id32896786.a1.ui.theme.A1Theme
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.fit2081.arrtish.id32896786.a1.AppViewModelFactory


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModelFactory: AppViewModelFactory
) {
    var context = LocalContext.current
    val viewModel: LoginViewModel = viewModel(factory = viewModelFactory)
    var selectedUserId by remember { mutableStateOf("") } // State to store selected user ID
    val userIds by viewModel.unregisteredPatientIds.observeAsState(initial = emptyList())
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    val message = viewModel.registrationMessage.value

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.registrationMessage.value = null // reset so it doesn’t keep showing
        }
    }

    if (viewModel.registrationSuccessful.value) {
        LaunchedEffect(Unit) {
            navController.navigate("login") {
                popUpTo("register") { inclusive = true }
            }
        }
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(text = "Register", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(24.dp))

        // Dropdown for ID
        var expanded by remember { mutableStateOf(false) }

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

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            placeholder = { Text("Enter your name") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number") },
            placeholder = { Text("Enter your phone number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            placeholder = { Text("Enter your password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            placeholder = { Text("Enter your password again") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "This app is only for pre-registered users. Please enter your ID, phone number and password to claim your account.",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.appRegister(selectedUserId, name, phone, password, confirmPassword) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Register Account")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                navController.navigate("login") {
                    popUpTo("register") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Return to Login")
        }
    }
}
