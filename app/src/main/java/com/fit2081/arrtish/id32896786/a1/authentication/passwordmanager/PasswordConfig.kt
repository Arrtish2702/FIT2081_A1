package com.fit2081.arrtish.id32896786.a1.authentication.passwordmanager

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fit2081.arrtish.id32896786.a1.AppViewModelFactory
import com.fit2081.arrtish.id32896786.a1.authentication.AuthenticationViewModel
import com.fit2081.arrtish.id32896786.a1.authentication.PasswordRequirement


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: AuthenticationViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current))
) {
    val context = LocalContext.current
    val selectedUserId by viewModel.forgotUserId
    val phoneNumber by viewModel.forgotPhone
    val newPassword by viewModel.forgotNewPassword
    val confirmNewPassword by viewModel.forgotConfirmPassword
    val userIds by viewModel.registeredPatientIds.observeAsState(initial = emptyList())
    var expanded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val hasMinLength = newPassword.length >= 8
    val hasUppercase = newPassword.any { it.isUpperCase() }
    val hasLowercase = newPassword.any { it.isLowerCase() }
    val hasNumber = newPassword.any { it.isDigit() }
    val hasSpecialChar = newPassword.any { it in "!@#\$%^&*." }

    val message = viewModel.forgotPasswordMessage.value
    val passwordChangeSuccess = viewModel.forgotPasswordSuccessful.value

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.forgotPasswordMessage.value = null
        }
    }

    if (passwordChangeSuccess) {
        LaunchedEffect(Unit) {
            navController.navigate("login") {
                popUpTo("forgotPassword") { inclusive = true }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(text = "Forgot Password", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(24.dp))

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

            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                userIds.forEach { userId ->
                    DropdownMenuItem(text = { Text(userId.toString()) }, onClick = {
                        viewModel.forgotUserId.value = userId.toString()
                        expanded = false
                    })
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { viewModel.forgotPhone.value = it },
            label = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = newPassword,
            onValueChange = { viewModel.forgotNewPassword.value = it },
            label = { Text("New Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

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

        OutlinedTextField(
            value = confirmNewPassword,
            onValueChange = { viewModel.forgotConfirmPassword.value = it },
            label = { Text("Confirm New Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.forgotPassword(selectedUserId.toInt(), phoneNumber, newPassword, confirmNewPassword, context)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Update Password")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                navController.navigate("login") {
                    popUpTo("forgotPassword") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Back to Login")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: AuthenticationViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current))
) {
    val context = LocalContext.current
    val selectedUserId by viewModel.changeSelectedUserId
    val oldPassword by viewModel.oldPassword
    val newPassword by viewModel.changeNewPassword
    val confirmNewPassword by viewModel.changeConfirmPassword
    val userIds by viewModel.registeredPatientIds.observeAsState(initial = emptyList())
    var expanded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val hasMinLength = newPassword.length >= 8
    val hasUppercase = newPassword.any { it.isUpperCase() }
    val hasLowercase = newPassword.any { it.isLowerCase() }
    val hasNumber = newPassword.any { it.isDigit() }
    val hasSpecialChar = newPassword.any { it in "!@#\$%^&*." }

    val message = viewModel.changePasswordMessage.value
    val passwordChangeSuccess = viewModel.changePasswordSuccessful.value

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.changePasswordMessage.value = null
        }
    }

    if (passwordChangeSuccess) {
        LaunchedEffect(Unit) {
            navController.navigate("login") {
                popUpTo("changePassword") { inclusive = true }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(text = "Change Password", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(24.dp))

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

            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                userIds.forEach { userId ->
                    DropdownMenuItem(text = { Text(userId.toString()) }, onClick = {
                        viewModel.changeSelectedUserId.value = userId.toString()
                        expanded = false
                    })
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = oldPassword,
            onValueChange = { viewModel.oldPassword.value = it },
            label = { Text("Old Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = newPassword,
            onValueChange = { viewModel.changeNewPassword.value = it },
            label = { Text("New Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

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

        OutlinedTextField(
            value = confirmNewPassword,
            onValueChange = { viewModel.changeConfirmPassword.value = it },
            label = { Text("Confirm New Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.changePassword(
                    selectedUserId.toInt(),
                    oldPassword,
                    newPassword,
                    confirmNewPassword,
                    context
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Change Password")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                navController.navigate("home") {
                    popUpTo("changePassword") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Back to Home")
        }
    }
}

