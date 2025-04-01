package com.fit2081.arrtish.id32896786.a1

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import org.json.JSONObject
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UserSharedPreferences(context: Context, private val userId: String) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("${userId}_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        // Static method to retrieve SharedPreferences object
        fun getPreferences(context: Context, userId: String): SharedPreferences {
            return context.getSharedPreferences("${userId}_prefs", Context.MODE_PRIVATE)
        }
    }

    // Save user choices with selected categories serialized to JSON
    fun saveUserChoices(userChoices: Map<String, Any>) {
        val editor = sharedPreferences.edit()

        // Serialize the selectedCategories list into a JSON string
        val selectedCategoriesJson = gson.toJson(userChoices["selectedCategories"])

        // Save each choice
        editor.putString("selectedCategories", selectedCategoriesJson)
        editor.putString("biggestMealTime", userChoices["biggestMealTime"] as String)
        editor.putString("sleepTime", userChoices["sleepTime"] as String)
        editor.putString("wakeTime", userChoices["wakeTime"] as String)
        editor.putString("selectedPersona", userChoices["selectedPersona"] as String)

        editor.apply()
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
