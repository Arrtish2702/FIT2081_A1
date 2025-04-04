package com.fit2081.arrtish.id32896786.a1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit

object Authentication {
    private lateinit var authPrefs: SharedPreferences

    fun init(context: Context) {
        authPrefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        Log.v("FIT2081-Authentication", "Loaded auth_prefs for login validation")
    }

    fun login(context: Context, userId: String, phoneNumber: String): Boolean {
        val storedPhone = authPrefs.getString(userId, null)
        if (storedPhone == null || storedPhone != phoneNumber) {
            Log.v("FIT2081-Authentication", "Login failed: invalid credentials")
            return false
        }

        Log.v("FIT2081-Authentication", "Login successful for userId: $userId")


        val userPreferences = UserSharedPreferences.getPreferences(context, userId)

        if (!userPreferences.contains("first_login")) {
            userPreferences.edit {
                putBoolean("first_login", true)
            }
            Log.v("FIT2081-Authentication", "First time login for $userId")
        } else {
            Log.v("FIT2081-Authentication", "Returning user login for $userId")
        }

        Log.v("FIT2081-Authentication", "Routing to Home page")
        val intent = Intent(context, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("user_id", userId)
        }
        context.startActivity(intent)

        return true
    }

    fun logout(context: Context) {
        Log.v("FIT2081-Authentication", "Routing to Login page")
        val intent = Intent(context, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
    }
}
