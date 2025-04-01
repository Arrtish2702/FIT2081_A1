package com.fit2081.arrtish.id32896786.a1

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import org.json.JSONObject
import androidx.core.content.edit

class UserSharedPreferences(context: Context, private val userId: String) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("${userId}_prefs", Context.MODE_PRIVATE)

    companion object {
        // Static method to retrieve SharedPreferences object
        fun getPreferences(context: Context, userId: String): SharedPreferences {
            return context.getSharedPreferences("${userId}_prefs", Context.MODE_PRIVATE)
        }
    }

    fun saveUserChoices(choices: Map<String, Any>) {
        val jsonString = JSONObject(choices).toString()
        sharedPreferences.edit().putString("choices", jsonString).apply()
    }

    fun getUserChoices(): Map<String, Any>? {
        val jsonString = sharedPreferences.getString("choices", null) ?: return null
        val jsonObject = JSONObject(jsonString)
        return jsonObject.keys().asSequence().associateWith { jsonObject.get(it) }
    }
}
