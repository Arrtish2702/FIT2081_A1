package com.fit2081.arrtish.id32896786.a1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fit2081.arrtish.id32896786.a1.ui.theme.A1Theme
import java.io.BufferedReader
import java.io.InputStreamReader

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Authentication.init(this) // Initialize auth system
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
            modifier = modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Login",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

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
                        DropdownMenuItem(text = { Text(userId) }, onClick = {
                            selectedUserId = userId
                            expanded = false
                        })
                    }
                }
            }

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = {
                    phoneNumber = it
                    phoneNumberError = !isValidNumber(it)
                },
                label = { Text("Phone Number") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = phoneNumberError,
                singleLine = true
            )
            if (phoneNumberError) {
                Text(
                    "Invalid Phone Number",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Button(onClick = {
                if (selectedUserId.isNotEmpty() && phoneNumber.isNotEmpty()) {
                    Authentication.login(context, selectedUserId, phoneNumber)
                } else {
                    Toast.makeText(
                        context,
                        "Please select a user ID and enter a phone number",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Login")
            }
        }
    }
}

fun extractUserIdsFromCSV(context: Context): List<String> {
    val userIds = mutableListOf<String>()
    try {
        val inputStream = context.assets.open("nutritrack_data.csv")
        val reader = BufferedReader(InputStreamReader(inputStream))
        reader.useLines { lines ->
            lines.drop(1).forEach { line ->
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

fun isValidNumber(number: String): Boolean {
    val aussieRegex = Regex("^(61[2-478]\\d{8})$")
    val malaysianRegex = Regex("^(60[1-9]\\d{7,9})$")
    return aussieRegex.matches(number) || malaysianRegex.matches(number)
}
