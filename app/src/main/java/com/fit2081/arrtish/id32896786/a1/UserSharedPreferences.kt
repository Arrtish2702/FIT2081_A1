package com.fit2081.arrtish.id32896786.a1

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import org.json.JSONObject
import androidx.core.content.edit

class UserSharedPreferences(context: Context, userId: String?) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("${userId}_prefs", Context.MODE_PRIVATE)

    companion object {
        fun getPreferences(context: Context, userId: String): SharedPreferences {
            return context.getSharedPreferences("${userId}_prefs", Context.MODE_PRIVATE)
        }
    }

    fun doesUserPrefsExist(): Boolean {
        Log.v("Shared Preferences", "Existing user")
        return sharedPreferences.all.isNotEmpty()
    }

    fun saveUserChoices(choices: Map<String, Any>) {
        val jsonString = JSONObject(choices).toString()
        sharedPreferences.edit { putString("choices", jsonString) }
    }

    fun getUserChoices(): Map<String, Any>? {
        val jsonString = sharedPreferences.getString("choices", null) ?: return null
        val jsonObject = JSONObject(jsonString)
        val resultMap = mutableMapOf<String, Any>()

        jsonObject.keys().forEach {
            resultMap[it] = jsonObject.get(it)
        }

        return resultMap
    }

    fun getFoodQualityScore(): Int {
        val choices = getUserChoices() ?: return 0
        val gender = choices["gender"]?.toString()?.lowercase() ?: return 0  // Retrieve gender

        return if (gender == "male") {
            choices["QualityScoreMale"]?.toString()?.toIntOrNull() ?: 0
        } else {
            choices["QualityScoreFemale"]?.toString()?.toIntOrNull() ?: 0
        }
    }

}
