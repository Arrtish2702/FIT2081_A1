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
        fun getPreferences(context: Context, userId: String): SharedPreferences {
            return context.getSharedPreferences("${userId}_prefs", Context.MODE_PRIVATE)
        }
    }

    fun saveUserChoices(userChoices: Map<String, Any>) {
        sharedPreferences.edit {

            val newCategories = userChoices["selectedCategories"] as? List<String> ?: emptyList()
            val updatedCategoriesJson = gson.toJson(newCategories)
            putString("selectedCategories", updatedCategoriesJson)

            userChoices["biggestMealTime"]?.let { putString("biggestMealTime", it as String) }
            userChoices["sleepTime"]?.let { putString("sleepTime", it as String) }
            userChoices["wakeTime"]?.let { putString("wakeTime", it as String) }
            userChoices["selectedPersona"]?.let { putString("selectedPersona", it as String) }

        }
    }

    fun clearUserChoices() {
        sharedPreferences.edit {

            putString("selectedCategories", "[]")
            putString("biggestMealTime", "12:00 PM")
            putString("sleepTime", "10:00 PM")
            putString("wakeTime", "6:00 AM")
            putString("selectedPersona", "Select a persona")

            putBoolean("answered", false)

        }
    }

    fun getUserChoices(): Map<String, Any>? {
        val selectedCategoriesJson = sharedPreferences.getString("selectedCategories", null)
        val biggestMealTime = sharedPreferences.getString("biggestMealTime", "12:00 PM")
        val sleepTime = sharedPreferences.getString("sleepTime", "10:00 PM")
        val wakeTime = sharedPreferences.getString("wakeTime", "6:00 AM")
        val selectedPersona = sharedPreferences.getString("selectedPersona", "Select a persona")

        val selectedCategoriesType = object : TypeToken<List<String>>() {}.type
        val selectedCategories = gson.fromJson<List<String>>(selectedCategoriesJson, selectedCategoriesType) ?: emptyList()

        return mapOf(
            "selectedCategories" to selectedCategories,
            "biggestMealTime" to (biggestMealTime ?: "12:00 PM"),
            "sleepTime" to (sleepTime ?: "10:00 PM"),
            "wakeTime" to (wakeTime ?: "6:00 AM"),
            "selectedPersona" to (selectedPersona ?: "Select a persona")
        )
    }

    fun getInsights(): Map<String, Any> {
        val insightsJson = sharedPreferences.getString("insights", null)
        return if (insightsJson != null) {
            val type = object : TypeToken<Map<String, Any>>() {}.type
            gson.fromJson(insightsJson, type)
        } else {
            emptyMap()
        }
    }
}
