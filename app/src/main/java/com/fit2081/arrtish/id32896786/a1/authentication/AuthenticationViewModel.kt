package com.fit2081.arrtish.id32896786.a1.authentication

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
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

/**
 * ViewModel class handling user authentication and registration operations.
 *
 * @param foodIntakeRepository Repository for managing food intake data.
 * @param patientRepository Repository for managing patient data.
 */
class AuthenticationViewModel(
    private val foodIntakeRepository: FoodIntakeRepository,
    private val patientRepository: PatientRepository
) : ViewModel() {

    // LiveData lists for registered and unregistered patient IDs
    val registeredPatientIds: LiveData<List<Int>> = patientRepository.allRegisteredPatientIds()
    val unregisteredPatientIds: LiveData<List<Int>> = patientRepository.allUnregisteredPatientIds()

    // State holders for selected user ID and password input fields
    var selectedUserId = mutableStateOf("")
        private set
    var password = mutableStateOf("")
        private set

    /** Update the currently selected user ID */
    fun updateSelectedUserId(newId: String) {
        selectedUserId.value = newId
    }

    /** Update the password field */
    fun updatePassword(newPassword: String) {
        password.value = newPassword
    }

    // Registration status and feedback message states
    var registrationSuccessful = mutableStateOf(false)
        private set
    var registrationMessage = mutableStateOf<String?>(null)
        private set

    // Login status and feedback message states
    var loginSuccessful = mutableStateOf<Boolean?>(null)
        private set
    var loginMessage = mutableStateOf<String?>(null)
        private set

    // Loading indicator state
    var isLoading = mutableStateOf(false)
        private set

    /** Set loading state */
    fun setLoading(value: Boolean) {
        isLoading.value = value
    }

    // Registration form state variables
    var regSelectedUserId = mutableStateOf("")
    var regName = mutableStateOf("")
    var regPhone = mutableStateOf("")
    var regPassword = mutableStateOf("")
    var regConfirmPassword = mutableStateOf("")

    // Forgot password form state variables
    var forgotUserId = mutableStateOf("")
    var forgotPhone = mutableStateOf("")
    var forgotNewPassword = mutableStateOf("")
    var forgotConfirmPassword = mutableStateOf("")
    val forgotPasswordMessage = mutableStateOf<String?>(null)
    val forgotPasswordSuccessful = mutableStateOf(false)

    // Change password form state variables
    var changeSelectedUserId = mutableStateOf("")
    var changeNewPassword = mutableStateOf("")
    var changeConfirmPassword = mutableStateOf("")
    var oldPassword = mutableStateOf("")
    val changePasswordMessage = mutableStateOf<String?>(null)
    val changePasswordSuccessful = mutableStateOf(false)

    /**
     * Handles password reset for users who forgot their password.
     *
     * @param selectedUserId Patient ID whose password is to be reset.
     * @param inputPhoneNumber Phone number input for verification.
     * @param new New password input.
     * @param confirm Confirmation of new password.
     * @param context Application context.
     */
    fun forgotPassword(selectedUserId: Int, inputPhoneNumber: String, new: String, confirm: String, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val patient = patientRepository.getPatientById(selectedUserId)

            if (patient == null) {
                forgotPasswordMessage.value = "User not found."
                return@launch
            }

            // Verify phone number matches the patient's record
            if (patient.patientPhoneNumber.trim() != inputPhoneNumber.trim()) {
                forgotPasswordMessage.value = "Phone number does not match User Account."
                return@launch
            }

            // Validate new password format
            if (!PasswordUtils.isValidPassword(new)) {
                forgotPasswordMessage.value = "Password must be 8+ chars, with uppercase, lowercase, number & special (!@#\$%^&*)."
                return@launch
            }

            // Confirm new password matches
            if (new != confirm) {
                forgotPasswordMessage.value = "New passwords do not match."
                return@launch
            }

            // Update patient password with hashed version
            val updatedPatient = patient.copy(
                patientPassword = PasswordUtils.hashPassword(new.trim())
            )
            patientRepository.updatePatient(updatedPatient)

            forgotPasswordMessage.value = "Password updated successfully."
            forgotPasswordSuccessful.value = true
        }
    }

    /**
     * Handles password change for logged-in users.
     *
     * @param selectedUserId Patient ID whose password is to be changed.
     * @param oldPasswordInput Current password input for verification.
     * @param newPasswordInput New password input.
     * @param confirmNewPasswordInput Confirmation of new password.
     * @param context Application context.
     */
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

            // Verify old password matches stored password
            if (!PasswordUtils.passwordsMatch(oldPasswordInput, patient.patientPassword)) {
                changePasswordMessage.value = "Old password is incorrect."
                return@launch
            }

            // Validate new password format
            if (!PasswordUtils.isValidPassword(newPasswordInput)) {
                changePasswordMessage.value = "Password does not meet requirements."
                return@launch
            }

            // Check new password and confirmation match
            if (newPasswordInput != confirmNewPasswordInput) {
                changePasswordMessage.value = "New passwords do not match."
                return@launch
            }

            // Ensure new password differs from old password
            if (PasswordUtils.passwordsMatch(newPasswordInput, patient.patientPassword)) {
                changePasswordMessage.value = "New password must be different from old password."
                return@launch
            }

            // Update patient password with hashed new password
            val updatedPatient = patient.copy(
                patientPassword = PasswordUtils.hashPassword(newPasswordInput.trim())
            )
            patientRepository.updatePatient(updatedPatient)

            changePasswordMessage.value = "Password changed successfully."
            changePasswordSuccessful.value = true
        }
    }

    /**
     * Registers a new patient account by updating name and password.
     *
     * @param selectedId Patient ID as a string.
     * @param name Patient name input.
     * @param phone Phone number input for verification.
     * @param password Password input.
     * @param confirmPassword Confirmation of password.
     */
    fun appRegister(selectedId: String, name: String, phone: String, password: String, confirmPassword: String) {
        Log.d(MainActivity.TAG, "LoginViewModel: attempting appRegister")
        viewModelScope.launch(Dispatchers.IO) {
            registrationSuccessful.value = false
            val patientId = selectedId.toIntOrNull()
            if (patientId == null) {
                registrationMessage.value = "Invalid ID format."
                return@launch
            }

            val patient = patientRepository.getPatientById(patientId)

            // Verify patient exists and phone matches record
            if (patient == null || patient.patientPhoneNumber != phone) {
                registrationMessage.value = "ID or phone number is incorrect."
                return@launch
            }

            // Validate passwords match
            if (password != confirmPassword) {
                registrationMessage.value = "Passwords do not match."
                return@launch
            }

            // Validate password complexity
            if (!PasswordUtils.isValidPassword(password)) {
                registrationMessage.value = "Password does not meet requirements."
                return@launch
            }

            // Update patient record with name and hashed password
            val updated = patient.copy(
                patientName = name,
                patientPassword = PasswordUtils.hashPassword(password)
            )
            patientRepository.updatePatient(updated)
            registrationSuccessful.value = true
            registrationMessage.value = "Registration successful!"
        }
    }

    /**
     * Authenticates a user attempting to log in.
     *
     * @param userId User ID as string.
     * @param password Password input.
     * @param navController NavController for navigation after login.
     * @param context Application context.
     */
    fun appLogin(userId: String, password: String, navController: NavController, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            loginSuccessful.value = false
            val patientId = userId.toIntOrNull()
            if (patientId == null) {
                loginMessage.value = "Invalid ID format."
                return@launch
            }

            Log.v(MainActivity.TAG, "inside vm: $patientId")

            val patient = patientRepository.getPatientById(patientId)

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
                (!PasswordUtils.passwordsMatch(password, patient.patientPassword)) -> {
                    loginMessage.value = "Incorrect password."
                    return@launch
                }
                else -> {
                    // Log user in and navigate accordingly
                    AuthManager.login(patientId, context)
                    val foodIntake = foodIntakeRepository.getFoodIntake(patientId)
                    Log.v(MainActivity.TAG, "foodintake: $foodIntake")
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

    /**
     * Validates clinician login by checking a hardcoded key.
     *
     * @param inputKey Key entered by clinician.
     * @return True if key matches, false otherwise.
     */
    fun clinicianLogin(inputKey: String): Boolean {
        val validKey = "dollar-entry-apples"
        return inputKey == validKey
    }
}
