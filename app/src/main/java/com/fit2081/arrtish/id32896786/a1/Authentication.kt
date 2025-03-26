package com.fit2081.arrtish.id32896786.a1

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.edit
import java.io.BufferedReader
import java.io.InputStreamReader

object Authentication {
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
            val sharedPreferences = context.getSharedPreferences("assignment_1", Context.MODE_PRIVATE)
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
        val sharedPreferences = context.getSharedPreferences("assignment_1", Context.MODE_PRIVATE)
        sharedPreferences.edit {
            putString("user_id", null)
            putString("phone_number", null)
        }

        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
        (context as? Activity)?.finish() // Ensure the current activity is finished
    }
}