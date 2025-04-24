package com.fit2081.arrtish.id32896786.a1.authentication

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fit2081.arrtish.id32896786.a1.HomeActivity
import com.fit2081.arrtish.id32896786.a1.UserSharedPreferences

class AuthenticationViewModel(application: Application) : ViewModel() {

    // Function to validate phone number (supports Australian and Malaysian numbers)
    fun isValidNumber(number: String): Boolean {
        val aussieRegex = Regex("^(61[2-478]\\d{8})$") // Australian phone number regex
        val malaysianRegex = Regex("^(60[1-9]\\d{7,9})$") // Malaysian phone number regex
        return aussieRegex.matches(number) || malaysianRegex.matches(number) // Return true if valid
    }


    // Login function - Verifies user credentials and routes to the HomeActivity if successful
    fun login(context: Context, userId: String, phoneNumber: String): Boolean {
//        // Retrieve the stored phone number for the given userId from SharedPreferences
//        val storedPhone = authPrefs.getString(userId, null)
//
//        // If the phone number doesn't match, login fails
//        if (storedPhone == null || storedPhone != phoneNumber) {
//            Log.v("FIT2081-Authentication", "Login failed: invalid credentials")
//            return false
//        }
//
//        // Login successful, log the event
//        Log.v("FIT2081-Authentication", "Login successful for userId: $userId")
//
//        // Get the user's shared preferences to check if it's the first login
//        val userPreferences = UserSharedPreferences.getPreferences(context, userId)
//
//        // Check if the "first_login" key exists in the user's preferences
//        if (!userPreferences.contains("first_login")) {
//            // If first login, set the "first_login" key to true
//            userPreferences.edit {
//                putBoolean("first_login", true)
//            }
//            Log.v("FIT2081-Authentication", "First time login for $userId")
//        } else {
//            // If the user has logged in before, log the event
//            Log.v("FIT2081-Authentication", "Returning user login for $userId")
//        }
//
//        // Log the redirection to the HomeActivity
//        Log.v("FIT2081-Authentication", "Routing to Home page")
//
//        // Create and start the HomeActivity with the userId as an extra
//        val intent = Intent(context, HomeActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK  // Clear previous activities
//            putExtra("user_id", userId)  // Pass userId to the HomeActivity
//        }
//        context.startActivity(intent)
//
        return true  // Login successful
    }



    fun register(selectedId: String, phone: String, password: String, confirmPassword: String){

    }

    class AuthenticationViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AuthenticationViewModel(context.applicationContext as Application) as T
        }
    }

}