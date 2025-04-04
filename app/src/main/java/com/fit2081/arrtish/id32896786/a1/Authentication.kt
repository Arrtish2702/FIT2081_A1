package com.fit2081.arrtish.id32896786.a1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit

// Authentication object - Manages user login and logout functionality
object Authentication {
    // SharedPreferences object for storing authentication data
    private lateinit var authPrefs: SharedPreferences

    // Initialize the authentication preferences
    fun init(context: Context) {
        // Load the authentication preferences for login validation
        authPrefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        Log.v("FIT2081-Authentication", "Loaded auth_prefs for login validation")
    }

    // Login function - Verifies user credentials and routes to the HomeActivity if successful
    fun login(context: Context, userId: String, phoneNumber: String): Boolean {
        // Retrieve the stored phone number for the given userId from SharedPreferences
        val storedPhone = authPrefs.getString(userId, null)

        // If the phone number doesn't match, login fails
        if (storedPhone == null || storedPhone != phoneNumber) {
            Log.v("FIT2081-Authentication", "Login failed: invalid credentials")
            return false
        }

        // Login successful, log the event
        Log.v("FIT2081-Authentication", "Login successful for userId: $userId")

        // Get the user's shared preferences to check if it's the first login
        val userPreferences = UserSharedPreferences.getPreferences(context, userId)

        // Check if the "first_login" key exists in the user's preferences
        if (!userPreferences.contains("first_login")) {
            // If first login, set the "first_login" key to true
            userPreferences.edit {
                putBoolean("first_login", true)
            }
            Log.v("FIT2081-Authentication", "First time login for $userId")
        } else {
            // If the user has logged in before, log the event
            Log.v("FIT2081-Authentication", "Returning user login for $userId")
        }

        // Log the redirection to the HomeActivity
        Log.v("FIT2081-Authentication", "Routing to Home page")

        // Create and start the HomeActivity with the userId as an extra
        val intent = Intent(context, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK  // Clear previous activities
            putExtra("user_id", userId)  // Pass userId to the HomeActivity
        }
        context.startActivity(intent)

        return true  // Login successful
    }

    // Logout function - Routes to the LoginActivity
    fun logout(context: Context) {
        // Log the redirection to the LoginActivity
        Log.v("FIT2081-Authentication", "Routing to Login page")

        // Create and start the LoginActivity
        val intent = Intent(context, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK  // Clear previous activities
        }
        context.startActivity(intent)
    }
}