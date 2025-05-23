package com.fit2081.arrtish.id32896786.a1.authentication

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.fit2081.arrtish.id32896786.a1.MainActivity
import com.fit2081.arrtish.id32896786.a1.authentication.passwordmanager.PasswordUtils
import com.fit2081.arrtish.id32896786.a1.databases.foodintakedb.FoodIntakeRepository
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.PatientRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthenticationViewModel(
    private val foodIntakeRepository: FoodIntakeRepository,
    private val patientRepository: PatientRepository
) : ViewModel() {

    val registeredPatientIds: LiveData<List<Int>> = patientRepository.allRegisteredPatientIds()
    val unregisteredPatientIds: LiveData<List<Int>> = patientRepository.allUnregisteredPatientIds()

    var selectedUserId = mutableStateOf("")
        private set

    var password = mutableStateOf("")
        private set

    var isDropdownExpanded = mutableStateOf(false)
        private set

    fun updateSelectedUserId(newId: String) {
        selectedUserId.value = newId
    }

    fun updatePassword(newPassword: String) {
        password.value = newPassword
    }

    fun toggleDropdown() {
        isDropdownExpanded.value = !isDropdownExpanded.value
    }

    fun dismissDropdown() {
        isDropdownExpanded.value = false
    }

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

    var regSelectedUserId = mutableStateOf("")
    var regName = mutableStateOf("")
    var regPhone = mutableStateOf("")
    var regPassword = mutableStateOf("")
    var regConfirmPassword = mutableStateOf("")

    var changeSelectedUserId = mutableStateOf("")
    var changePhoneNumber = mutableStateOf("")
    var changeNewPassword = mutableStateOf("")
    var changeConfirmPassword = mutableStateOf("")

    val forgotPasswordMessage = mutableStateOf<String?>(null)
    val forgotPasswordSuccessful = mutableStateOf(false)

    var oldPassword = mutableStateOf("")

    val changePasswordMessage = mutableStateOf<String?>(null)
    val changePasswordSuccessful = mutableStateOf(false)

    fun forgotPassword(selectedUserId: Int, inputPhoneNumber: String, new: String, confirm: String, context: Context) {
        viewModelScope.launch(Dispatchers.IO){
            val patient = patientRepository.getPatientById(selectedUserId)

            if (patient == null) {
                forgotPasswordMessage.value = "User not found."
                return@launch
            }

            if (patient.patientPhoneNumber.trim() != inputPhoneNumber.trim()) {
                forgotPasswordMessage.value = "Phone number does not match User Account."
                return@launch
            }

            if (!PasswordUtils.isValidPassword(new)) {
                forgotPasswordMessage.value = "Password must be at least 8 characters and include a capital letter, lowercase letter, number, and special symbol (!@#\$%^&*)."
                return@launch
            }

            if (new != confirm) {
                forgotPasswordMessage.value = "New passwords do not match."
                return@launch
            }

            val updatedPatient = patient.copy(
                patientPassword = PasswordUtils.hashPassword(new.trim())
            )
            patientRepository.updatePatient(updatedPatient)

            forgotPasswordMessage.value = "Password updated successfully."
            forgotPasswordSuccessful.value = true
        }
    }

    fun changePassword(
        selectedUserId: Int,
        oldPasswordInput: String,
        newPasswordInput: String,
        confirmNewPasswordInput: String,
        context: Context
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val patient = patientRepository.getPatientById(selectedUserId)

            if (patient == null) {
                changePasswordMessage.value = "User not found."
                return@launch
            }

            if (patient.patientPassword.isEmpty()) {
                changePasswordMessage.value = "This account is not registered yet."
                return@launch
            }

            if (!PasswordUtils.passwordsMatch(oldPasswordInput, patient.patientPassword)) {
                changePasswordMessage.value = "Old password is incorrect."
                return@launch
            }

            if (!PasswordUtils.isValidPassword(newPasswordInput)) {
                changePasswordMessage.value = "Password must be at least 8 characters and include a capital letter, lowercase letter, number, and special symbol (!@#\$%^&*)."
                return@launch
            }

            if (newPasswordInput != confirmNewPasswordInput) {
                changePasswordMessage.value = "New passwords do not match."
                return@launch
            }

            if (PasswordUtils.passwordsMatch(newPasswordInput, patient.patientPassword)) {
                changePasswordMessage.value = "New password must be different from old password."
                return@launch
            }

            val updatedPatient = patient.copy(
                patientPassword = PasswordUtils.hashPassword(newPasswordInput.trim())
            )
            patientRepository.updatePatient(updatedPatient)

            changePasswordMessage.value = "Password changed successfully."
            changePasswordSuccessful.value = true
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

            if (!PasswordUtils.isValidPassword(password)) {
                registrationMessage.value = "Password must be 8+ chars, with upper, lower, number & symbol (!@#\$%^&*)."
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
                    Log.v(MainActivity.Companion.TAG, "foodintake: $foodIntake")
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

    fun clinicianLogin(inputKey: String): Boolean {
        val validKey = "dollar-entry-apples"
        return inputKey == validKey
    }

}