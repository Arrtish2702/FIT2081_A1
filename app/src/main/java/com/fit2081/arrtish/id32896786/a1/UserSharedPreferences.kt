package com.fit2081.arrtish.id32896786.a1

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UserSharedPreferences(context: Context, userId: String) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("${userId}_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        // Static method to retrieve SharedPreferences object
        fun getPreferences(context: Context, userId: String): SharedPreferences {
            return context.getSharedPreferences("${userId}_prefs", Context.MODE_PRIVATE)
        }
    }


    // Method to save user choices
    fun saveUserChoices(userChoices: Map<String, Any>) {
        sharedPreferences.edit {

            // Retrieve existing selected categories
            val existingCategoriesJson = sharedPreferences.getString("selectedCategories", "[]")
            val existingCategories: MutableList<String> =
                gson.fromJson(
                    existingCategoriesJson,
                    object : TypeToken<MutableList<String>>() {}.type
                ) ?: mutableListOf()

            // Get new categories
            val newCategories = userChoices["selectedCategories"] as? List<String> ?: emptyList()

            // Merge new categories with existing ones (avoid duplicates)
            existingCategories.addAll(newCategories.filter { it !in existingCategories })

            // Save updated categories
            val updatedCategoriesJson = gson.toJson(existingCategories)
            putString("selectedCategories", updatedCategoriesJson)

            // Preserve old values while updating new ones
            userChoices["biggestMealTime"]?.let { putString("biggestMealTime", it as String) }
            userChoices["sleepTime"]?.let { putString("sleepTime", it as String) }
            userChoices["wakeTime"]?.let { putString("wakeTime", it as String) }
            userChoices["selectedPersona"]?.let { putString("selectedPersona", it as String) }

        }
    }

    // Method to clear all user choices and reset to defaults
    fun clearUserChoices() {
        sharedPreferences.edit {

            // Reset all user choices to default values
            putString("selectedCategories", "[]") // Empty list of categories
            putString("biggestMealTime", "12:00 PM")
            putString("sleepTime", "10:00 PM")
            putString("wakeTime", "6:00 AM")
            putString("selectedPersona", "Select a persona")

            // Optionally clear insights and answered status as well
//        editor.remove("insights")  // Clear insights
            putBoolean("answered", false)  // Set answered to false

        }
    }

    // Retrieve the user choices
    fun getUserChoices(): Map<String, Any>? {
        val selectedCategoriesJson = sharedPreferences.getString("selectedCategories", null)
        val biggestMealTime = sharedPreferences.getString("biggestMealTime", "12:00 PM")
        val sleepTime = sharedPreferences.getString("sleepTime", "10:00 PM")
        val wakeTime = sharedPreferences.getString("wakeTime", "6:00 AM")
        val selectedPersona = sharedPreferences.getString("selectedPersona", "Select a persona")

        // Deserialize the selectedCategories JSON back to a List<String>
        val selectedCategoriesType = object : TypeToken<List<String>>() {}.type
        val selectedCategories = gson.fromJson<List<String>>(selectedCategoriesJson, selectedCategoriesType) ?: emptyList()

        // Return the saved choices as a map
        return mapOf(
            "selectedCategories" to selectedCategories,
            "biggestMealTime" to (biggestMealTime ?: "12:00 PM"),
            "sleepTime" to (sleepTime ?: "10:00 PM"),
            "wakeTime" to (wakeTime ?: "6:00 AM"),
            "selectedPersona" to (selectedPersona ?: "Select a persona")
        )
    }

    // Method to get the insights data (sub-dictionary under "insights")
    fun getInsights(): Map<String, Any> {
        val insightsJson = sharedPreferences.getString("insights", null)
        return if (insightsJson != null) {
            // Deserialize the JSON string to a Map
            val type = object : TypeToken<Map<String, Any>>() {}.type
            gson.fromJson(insightsJson, type)
        } else {
            emptyMap()  // Return empty map if no insights exist
        }
    }
}
