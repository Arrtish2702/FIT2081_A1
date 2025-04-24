package com.fit2081.arrtish.id32896786.a1.authentication

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fit2081.arrtish.id32896786.a1.databases.AppDataBase
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.PatientRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AuthenticationViewModel(application: Application) : ViewModel() {

    private val patientDao = AppDataBase.getDatabase(application).patientDao()
    private val repository = PatientRepository(patientDao)
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


    fun login(userId: String, password: String) {
        viewModelScope.launch {
            loginSuccessful.value = false
            val patientId = userId.toIntOrNull()
            if (patientId == null) {
                loginMessage.value = "Invalid ID format."
                loginSuccessful.value = false
                return@launch
            }

            val patient = repository.getPatientById(patientId)

            if (patient == null) {
                loginMessage.value = "User ID not found."
                loginSuccessful.value = false
                return@launch
            }

            if (patient.patientPassword.isEmpty()) {
                loginMessage.value = "Account has not been registered."
                loginSuccessful.value = false
                return@launch
            }

            if (patient.patientPassword != password) {
                loginMessage.value = "Incorrect password."
                loginSuccessful.value = false
                return@launch
            }

            loginMessage.value = "Login successful!"
            loginSuccessful.value = true
        }
    }


    fun register(selectedId: String, name: String, phone: String, password: String, confirmPassword: String) {

        Log.d("AuthenticationViewModel", "attempting register")
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


    class AuthenticationViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AuthenticationViewModel(context.applicationContext as Application) as T
        }
    }

}