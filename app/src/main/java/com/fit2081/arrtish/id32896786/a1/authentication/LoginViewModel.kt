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
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import com.fit2081.arrtish.id32896786.a1.MainActivity

class LoginViewModel(private val repository: PatientRepository) : ViewModel() {

    val patientIds: LiveData<List<Int>> = repository.allPatientIds()
    val registeredPatientIds: LiveData<List<Int>> = repository.allRegisteredPatientIds()
    val unregisteredPatientIds: LiveData<List<Int>> = repository.allUnregisteredPatientIds()

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

    val changePasswordMessage = mutableStateOf<String?>(null)
    val changePasswordSuccessful = mutableStateOf(false)

    fun changePassword(selectedUserId: Int, inputPhoneNumber: String, new: String, confirm: String, context: Context) {
        viewModelScope.launch {
            val patient = repository.getPatientById(selectedUserId)

            if (patient == null) {
                changePasswordMessage.value = "User not found."
                return@launch
            }

            // Trim and compare phone numbers
            if (patient.patientPhoneNumber.trim() != inputPhoneNumber.trim()) {
                changePasswordMessage.value = "Phone number does not match User Account."
                return@launch
            }

            if (new != confirm) {
                changePasswordMessage.value = "New passwords do not match."
                return@launch
            }

            // Perform update
            val updatedPatient = patient.copy(
                patientPassword = PasswordUtils.hashPassword(new.trim())
            )
            repository.updatePatient(updatedPatient)

            changePasswordMessage.value = "Password updated successfully."
            changePasswordSuccessful.value = true
        }
    }


    fun appLogin(userId: String, password: String, navController: NavController, context: Context) {
        viewModelScope.launch {
            loginSuccessful.value = false
            val patientId = userId.toIntOrNull()
            if (patientId == null) {
                loginMessage.value = "Invalid ID format."
                return@launch
            }

            Log.v(MainActivity.TAG, "inside vm: $patientId")

            val patient = repository.getPatientById(patientId)

            val logpw = patient?.patientPassword

            Log.v(MainActivity.TAG, "patient: $patient")
            Log.v(MainActivity.TAG, "saved: $logpw")
            Log.v(MainActivity.TAG, "input pw: $password")

            Log.v(MainActivity.TAG, "patient password length: ${patient?.patientPassword?.length}")
            Log.v(MainActivity.TAG, "input password length: ${password.length}")
            when {
                patient == null -> {
                    loginMessage.value = "User ID not found."
                    return@launch
                }
                patient.patientPassword.isEmpty() -> {
                    loginMessage.value = "Account has not been registered."
                    return@launch
                }
                (!PasswordUtils.passwordsMatch(password, patient.patientPassword))->{
                    loginMessage.value = "Incorrect password."
                    return@launch
                }
                else -> {
                    // ✅ Use AuthManager instead of SharedPreferences
                    AuthManager.login(patientId, context)
                    loginMessage.value = "Login successful!"
                    loginSuccessful.value = true
                    // Navigate directly after login success
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                    return@launch
                }
            }
        }
    }

    fun appRegister(selectedId: String, name: String, phone: String, password: String, confirmPassword: String) {

        Log.d(MainActivity.TAG, "LoginViewModel: attempting appRegister")
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

            val updated = patient.copy(
                patientName = name,
                patientPassword = PasswordUtils.hashPassword(password)
            )
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