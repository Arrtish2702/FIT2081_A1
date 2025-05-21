package com.fit2081.arrtish.id32896786.a1.authentication.login

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.fit2081.arrtish.id32896786.a1.MainActivity
import com.fit2081.arrtish.id32896786.a1.authentication.AuthManager
import com.fit2081.arrtish.id32896786.a1.authentication.PasswordUtils
import com.fit2081.arrtish.id32896786.a1.databases.foodintakedb.FoodIntakeRepository
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.PatientRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(private val foodIntakeRepository: FoodIntakeRepository, private val patientRepository: PatientRepository) : ViewModel() {

    val patientIds: LiveData<List<Int>> = patientRepository.allPatientIds()
    val registeredPatientIds: LiveData<List<Int>> = patientRepository.allRegisteredPatientIds()
    val unregisteredPatientIds: LiveData<List<Int>> = patientRepository.allUnregisteredPatientIds()

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
        viewModelScope.launch(Dispatchers.IO){
            val patient = patientRepository.getPatientById(selectedUserId)

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
            patientRepository.updatePatient(updatedPatient)

            changePasswordMessage.value = "Password updated successfully."
            changePasswordSuccessful.value = true
        }
    }


    fun appLogin(userId: String, password: String, navController: NavController, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            loginSuccessful.value = false
            val patientId = userId.toIntOrNull()
            if (patientId == null) {
                loginMessage.value = "Invalid ID format."
                return@launch
            }

            Log.v(MainActivity.Companion.TAG, "inside vm: $patientId")

            val patient = patientRepository.getPatientById(patientId)

            val logpw = patient?.patientPassword

            Log.v(MainActivity.Companion.TAG, "patient: $patient")
            Log.v(MainActivity.Companion.TAG, "saved: $logpw")
            Log.v(MainActivity.Companion.TAG, "input pw: $password")

            Log.v(MainActivity.Companion.TAG, "patient password length: ${patient?.patientPassword?.length}")
            Log.v(MainActivity.Companion.TAG, "input password length: ${password.length}")
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
                    AuthManager.login(patientId, context)
                    val foodIntake = foodIntakeRepository.getFoodIntake(patientId)

                    withContext(Dispatchers.Main) {
                        loginMessage.value = "Login successful!"
                        loginSuccessful.value = true

                        if (foodIntake == null) {
                            navController.navigate("questionnaire") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }
                }
            }
        }
    }

    fun appRegister(selectedId: String, name: String, phone: String, password: String, confirmPassword: String) {

        Log.d(MainActivity.Companion.TAG, "LoginViewModel: attempting appRegister")
        viewModelScope.launch (Dispatchers.IO){
            registrationSuccessful.value = false
            val patientId = selectedId.toIntOrNull()
            if (patientId == null) {
                registrationMessage.value = "Invalid ID format."
                return@launch
            }

            val patient = patientRepository.getPatientById(patientId)

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
            patientRepository.updatePatient(updated)
            registrationSuccessful.value = true
            registrationMessage.value = "Registration successful!"
        }
    }

    fun clinicianLogin(inputKey: String): Boolean {
        val validKey = "dollar-entry-apples"
        return inputKey == validKey
    }

}