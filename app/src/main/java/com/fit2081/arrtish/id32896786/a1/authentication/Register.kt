package com.fit2081.arrtish.id32896786.a1.authentication

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.fit2081.arrtish.id32896786.a1.AppViewModelFactory


/**
 * Composable function for the Registration screen.
 * Displays a form allowing pre-registered users to claim their account by entering
 * user ID, name, phone number, password and confirming password.
 *
 * @param modifier Modifier for this composable.
 * @param navController Navigation controller to move between screens.
 * @param viewModelFactory Factory for creating AuthenticationViewModel with dependencies.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModelFactory: AppViewModelFactory
) {
    val context = LocalContext.current

    // Get AuthenticationViewModel instance with provided factory
    val viewModel: AuthenticationViewModel = viewModel(factory = viewModelFactory)

    // Observe list of unregistered patient IDs from ViewModel LiveData
    val userIds by viewModel.unregisteredPatientIds.observeAsState(initial = emptyList())

    // Collect state from ViewModel for registration input fields
    val selectedUserId by viewModel.regSelectedUserId
    val name by viewModel.regName
    val phone by viewModel.regPhone
    val password by viewModel.regPassword
    val confirmPassword by viewModel.regConfirmPassword

    // Password validation rules
    val hasMinLength = password.length >= 8
    val hasUppercase = password.any { it.isUpperCase() }
    val hasLowercase = password.any { it.isLowerCase() }
    val hasNumber = password.any { it.isDigit() }
    val hasSpecialChar = password.any { it in "!@#\$%^&*." }

    // Observe registration message for user feedback
    val message = viewModel.registrationMessage.value

    val scrollState = rememberScrollState()

    // Show Toast when a registration message is emitted by the ViewModel
    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.registrationMessage.value = null // Reset message after showing
        }
    }

    // On successful registration, navigate back to login screen and clear registration from back stack
    if (viewModel.registrationSuccessful.value) {
        LaunchedEffect(Unit) {
            navController.navigate("login") {
                popUpTo("register") { inclusive = true }
            }
        }
    }

    // Main registration form layout in a vertical scrollable column
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(text = "Register", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(24.dp))

        // Dropdown menu state for selecting user ID
        var expanded by remember { mutableStateOf(false) }

        // User ID selection dropdown menu
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = selectedUserId,
                onValueChange = {},
                label = { Text("User ID") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) },
                readOnly = true,
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )

            // Dropdown items with available user IDs
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                userIds.forEach { userId ->
                    DropdownMenuItem(text = { Text(userId.toString()) }, onClick = {
                        viewModel.regSelectedUserId.value = userId.toString()
                        expanded = false
                    })
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Input field for user's name
        OutlinedTextField(
            value = name,
            onValueChange = { viewModel.regName.value = it },
            label = { Text("Name") },
            placeholder = { Text("Enter your name") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input field for user's phone number
        OutlinedTextField(
            value = phone,
            onValueChange = { viewModel.regPhone.value = it },
            label = { Text("Phone Number") },
            placeholder = { Text("Enter your phone number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password input field with validation error indication
        OutlinedTextField(
            value = password,
            onValueChange = { viewModel.regPassword.value = it },
            label = { Text("Password") },
            placeholder = { Text("Enter your password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            isError = password.isNotBlank() && !(hasMinLength && hasUppercase && hasLowercase && hasNumber && hasSpecialChar)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Display password requirements with check/clear icons
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Password must include:",
                style = MaterialTheme.typography.bodySmall
            )
            PasswordRequirement("• At least 8 characters", hasMinLength)
            PasswordRequirement("• One uppercase letter", hasUppercase)
            PasswordRequirement("• One lowercase letter", hasLowercase)
            PasswordRequirement("• One number", hasNumber)
            PasswordRequirement("• One special character (!@#\$%^&*)", hasSpecialChar)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm password input field
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { viewModel.regConfirmPassword.value = it },
            label = { Text("Confirm Password") },
            placeholder = { Text("Enter your password again") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Instructional text for pre-registered users
        Text(
            text = "This app is only for pre-registered users. Please enter your ID, phone number and password to claim your account.",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Button to trigger registration logic in ViewModel
        Button(
            onClick = { viewModel.appRegister(selectedUserId, name, phone, password, confirmPassword) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Register Account")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Button to navigate back to login screen
        Button(
            onClick = {
                navController.navigate("login") {
                    popUpTo("register") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Return to Login")
        }
    }
}


/**
 * Composable function to display individual password requirement with an icon.
 * Shows a check icon if requirement is met, otherwise a clear icon.
 *
 * @param text Description of the password requirement.
 * @param met Boolean indicating if the requirement is met.
 */
@Composable
fun PasswordRequirement(text: String, met: Boolean) {
    val color = if (met) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.error
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (met) Icons.Default.Check else Icons.Default.Clear,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, color = color, style = MaterialTheme.typography.bodySmall)
    }
}
