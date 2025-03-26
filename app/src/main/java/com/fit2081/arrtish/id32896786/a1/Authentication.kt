package com.fit2081.arrtish.id32896786.a1

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import androidx.core.content.edit
import java.io.BufferedReader
import java.io.InputStreamReader

object Authentication {
    private lateinit var sharedPreferences: SharedPreferences

    // Initialize SharedPreferences once
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("assignment_1", Context.MODE_PRIVATE)
    }

    fun login(context: Context, userId: String, phoneNumber: String): Boolean {
        val assets = context.assets
        var isValid = false

        try {
            val inputStream = assets.open("nutritrack_data.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))

            reader.useLines { lines ->
                lines.drop(1).forEach { line ->  // Skip header row
                    val values = line.split(",").map { it.trim() }
                    if (values.size >= 2) {
                        val csvPhoneNumber = values[0]
                        val csvUserId = values[1]

                        if (csvPhoneNumber == phoneNumber && csvUserId == userId) {
                            isValid = true
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (isValid) {
            sharedPreferences.edit {
                putString("user_id", userId)
                putString("phone_number", phoneNumber)
                apply()
            }

            Toast.makeText(context, "Login Successful", Toast.LENGTH_LONG).show()
            val intent = Intent(context, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "Incorrect Credentials", Toast.LENGTH_LONG).show()
        }

        return isValid
    }

    fun logout(context: Context) {
        sharedPreferences.edit {
            remove("user_id")
            remove("phone_number")
            apply()
        }

        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
        (context as? Activity)?.finish() // Ensure the current activity is finished
    }
}
