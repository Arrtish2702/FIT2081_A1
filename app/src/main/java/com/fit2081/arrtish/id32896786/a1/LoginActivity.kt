package com.fit2081.arrtish.id32896786.a1

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fit2081.arrtish.id32896786.a1.ui.theme.A1Theme
import java.io.BufferedReader
import java.io.InputStreamReader

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            A1Theme {
                LoginPage(context = this, modifier = Modifier.fillMaxSize())
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(context: Context, modifier: Modifier = Modifier) {
    var selectedUserId by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var phoneNumberError by remember { mutableStateOf(false) }
    val userIds = remember { extractUserIdsFromCSV(context) }
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_for_an_app_called_nutritrack),
            contentDescription = "NutriTrack Logo",
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.TopCenter)
                .padding(top = 60.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Login",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            // Exposed Dropdown for User ID with Arrow Icon
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedUserId,
                    onValueChange = {},
                    label = { Text("User ID") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = "User Icon") },
                    trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = "Dropdown Arrow") }, // <-- ARROW ADDED
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    userIds.forEach { userId ->
                        DropdownMenuItem(
                            text = { Text(userId) },
                            onClick = {
                                selectedUserId = userId
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Phone Number Input
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = {
                    phoneNumber = it
                    phoneNumberError = !isValidNumber(it)
                },
                label = { Text("Phone Number") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone Icon") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = phoneNumberError,
                singleLine = true
            )
            if (phoneNumberError) {
                Text(
                    text = "Invalid Phone Number",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            // Login Button
            Button(
                onClick = {
                    Authentication.login(context, selectedUserId, phoneNumber)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }
        }
    }
}


// Function to extract user IDs from the CSV
fun extractUserIdsFromCSV(context: Context): List<String> {
    val userIds = mutableListOf<String>()

    try {
        val inputStream = context.assets.open("nutritrack_data.csv")
        val reader = BufferedReader(InputStreamReader(inputStream))

        reader.useLines { lines ->
            lines.drop(1).forEach { line ->  // Skip header row
                val values = line.split(",").map { it.trim() }
                if (values.size >= 2) {
                    val csvUserId = values[1]
                    if (csvUserId.isNotEmpty()) {
                        userIds.add(csvUserId)
                    }
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return userIds
}

// Function to validate phone numbers
fun isValidNumber(number: String): Boolean {
    val aussieRegex = Regex("^(61[2-478]\\d{8})$")
    val malaysianRegex = Regex("^(60[1-9]\\d{7,9})$")
    return aussieRegex.matches(number) || malaysianRegex.matches(number)
}