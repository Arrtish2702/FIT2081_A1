package com.fit2081.arrtish.id32896786.a1.authentication

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.PatientRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import androidx.core.content.edit

class LoginViewModel(private val repository: PatientRepository) : ViewModel() {

    val patientIds: Flow<List<Int>> = repository.allPatientIds()

    var registrationSuccessful = mutableStateOf(false)
        private set
    var registrationMessage = mutableStateOf<String?>(null)
        private set
    var loginSuccessful = mutableStateOf<Boolean?>(null)
        private set
    var loginMessage = mutableStateOf<String?>(null)
        private set

    var isLoading = mutableStateOf(false)
        private set

    fun setLoading(value: Boolean) {
        isLoading.value = value
    }

    // Function to validate phone number (supports Australian and Malaysian numbers)
    fun isValidNumber(number: String): Boolean {
        val aussieRegex = Regex("^(61[2-478]\\d{8})$") // Australian phone number regex
        val malaysianRegex = Regex("^(60[1-9]\\d{7,9})$") // Malaysian phone number regex
        return aussieRegex.matches(number) || malaysianRegex.matches(number) // Return true if valid
    }


    fun appLogin(userId: String, password: String, context: Context) {
        viewModelScope.launch {
            loginSuccessful.value = false
            val patientId = userId.toIntOrNull()
            if (patientId == null) {
                loginMessage.value = "Invalid ID format."
                return@launch
            }

            val patient = repository.getPatientById(patientId)

            when {
                patient == null -> {
                    loginMessage.value = "User ID not found."
                }
                patient.patientPassword.isEmpty() -> {
                    loginMessage.value = "Account has not been registered."
                }
                patient.patientPassword != password -> {
                    loginMessage.value = "Incorrect password."
                }
                else -> {
                    // ✅ Use AuthManager instead of SharedPreferences
                    AuthManager.login(patientId)
                    loginMessage.value = "Login successful!"
                    loginSuccessful.value = true
                }
            }
        }
    }


    fun appRegister(selectedId: String, name: String, phone: String, password: String, confirmPassword: String) {

        Log.d("LoginViewModel", "attempting appRegister")
        viewModelScope.launch {
            registrationSuccessful.value = false
            val patientId = selectedId.toIntOrNull()
            if (patientId == null) {
                registrationMessage.value = "Invalid ID format."
                return@launch
            }

            val patient = repository.getPatientById(patientId)

            if (patient == null || patient.patientPhoneNumber != phone) {
                registrationMessage.value = "ID or phone number is incorrect."
                return@launch
            }

            if (password != confirmPassword) {
                registrationMessage.value = "Passwords do not match."
                return@launch
            }

            val updated = patient.copy(patientName = name,patientPassword = password)
            repository.updatePatient(updated)
            registrationSuccessful.value = true
            registrationMessage.value = "Registration successful!"
        }
    }

    fun clinicianLogin(inputKey: String): Boolean {
        val validKey = "dollar-entry-apples"
        return inputKey == validKey
    }

}