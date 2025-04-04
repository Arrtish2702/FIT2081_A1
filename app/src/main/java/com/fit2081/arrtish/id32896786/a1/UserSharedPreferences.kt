package com.fit2081.arrtish.id32896786.a1

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// UserSharedPreferences class - Handles storing and retrieving user preferences for a given userId
class UserSharedPreferences(context: Context, userId: String) {
    // SharedPreferences instance for storing user-specific preferences
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("${userId}_prefs", Context.MODE_PRIVATE)

    // Gson instance for converting objects to JSON and vice versa
    private val gson = Gson()

    companion object {
        // Static method to retrieve the SharedPreferences for a given userId
        fun getPreferences(context: Context, userId: String): SharedPreferences {
            return context.getSharedPreferences("${userId}_prefs", Context.MODE_PRIVATE)
        }
    }

    // Method to save user preferences (choices)
    fun saveUserChoices(userChoices: Map<String, Any>) {
        // Edit the SharedPreferences to save new values
        sharedPreferences.edit {

            // Retrieve and store selected categories as a JSON string
            val newCategories = userChoices["selectedCategories"] as? List<String> ?: emptyList()
            val updatedCategoriesJson = gson.toJson(newCategories)
            putString("selectedCategories", updatedCategoriesJson)

            // Store other user choices like meal time, sleep time, wake time, and selected persona
            userChoices["biggestMealTime"]?.let { putString("biggestMealTime", it as String) }
            userChoices["sleepTime"]?.let { putString("sleepTime", it as String) }
            userChoices["wakeTime"]?.let { putString("wakeTime", it as String) }
            userChoices["selectedPersona"]?.let { putString("selectedPersona", it as String) }

        }
    }

    // Method to clear all user choices and reset them to default values
    fun clearUserChoices() {
        // Edit SharedPreferences to reset all values to their defaults
        sharedPreferences.edit {

            putString("selectedCategories", "[]")  // Empty list of selected categories
            putString("biggestMealTime", "12:00 PM")  // Default meal time
            putString("sleepTime", "10:00 PM")  // Default sleep time
            putString("wakeTime", "6:00 AM")  // Default wake time
            putString("selectedPersona", "Select a persona")  // Default persona selection

            putBoolean("answered", false)  // Set answered flag to false

        }
    }

    // Method to retrieve user choices from SharedPreferences
    fun getUserChoices(): Map<String, Any>? {
        // Retrieve stored values, with fallback defaults if not set
        val selectedCategoriesJson = sharedPreferences.getString("selectedCategories", null)
        val biggestMealTime = sharedPreferences.getString("biggestMealTime", "12:00 PM")
        val sleepTime = sharedPreferences.getString("sleepTime", "10:00 PM")
        val wakeTime = sharedPreferences.getString("wakeTime", "6:00 AM")
        val selectedPersona = sharedPreferences.getString("selectedPersona", "Select a persona")

        // Deserialize the selected categories from JSON
        val selectedCategoriesType = object : TypeToken<List<String>>() {}.type
        val selectedCategories = gson.fromJson<List<String>>(selectedCategoriesJson, selectedCategoriesType) ?: emptyList()

        // Return all user choices as a map
        return mapOf(
            "selectedCategories" to selectedCategories,
            "biggestMealTime" to (biggestMealTime ?: "12:00 PM"),
            "sleepTime" to (sleepTime ?: "10:00 PM"),
            "wakeTime" to (wakeTime ?: "6:00 AM"),
            "selectedPersona" to (selectedPersona ?: "Select a persona")
        )
    }

    // Method to retrieve insights from SharedPreferences
    fun getInsights(): Map<String, Any> {
        // Retrieve stored insights as a JSON string
        val insightsJson = sharedPreferences.getString("insights", null)

        // If insights are found, deserialize and return them, otherwise return an empty map
        return if (insightsJson != null) {
            val type = object : TypeToken<Map<String, Any>>() {}.type
            gson.fromJson(insightsJson, type)
        } else {
            emptyMap()  // Return empty map if no insights are available
        }
    }
}