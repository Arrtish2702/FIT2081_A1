package com.fit2081.arrtish.id32896786.a1

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import androidx.core.content.edit

object CsvExports {
    fun getUserDetailsAndSave(context: Context, userId: String?) {
        if (userId.isNullOrBlank()) return  // Handle null/empty userId

        // ✅ Obtain existing user preferences
        val sharedPreferences = UserSharedPreferences.getPreferences(context, userId)

        Log.v("CsvExports", "SharedPreferences: $sharedPreferences")

        val inputStream = context.assets.open("nutritrack_data.csv")
        val reader = BufferedReader(InputStreamReader(inputStream))
        val lines = reader.readLines()

        val userChoices = mutableMapOf<String, Any>()

        val headers = lines.first().split(",").map { it.trim() } // Extract headers
        val dataRows = lines.drop(1) // Skip header row

        // Find the user's row
        val userRow = dataRows.map { it.split(",").map { value -> value.trim() } }
            .find { it.getOrNull(headers.indexOf("User_ID")) == userId } ?: return

        val isMale = userRow.getOrNull(headers.indexOf("Sex")) == "Male"
        Log.v("CsvExports","$userRow and $isMale")
        // Extract quality score

        Log.v("CsvExports", "HEIFA Male index: ${headers.indexOf("HEIFAtotalscoreMale")}, HEIFA Female index: ${headers.indexOf("HEIFAtotalscoreFemale")}")

        Log.v("CsvExports", "Raw qualityScore value: ${userRow.getOrNull(headers.indexOf(if (isMale) "HEIFAtotalscoreMale" else "HEIFAtotalscoreFemale"))}")

        val qualityScore = if (isMale) {
            userRow.getOrNull(headers.indexOf("HEIFAtotalscoreMale"))?.toFloatOrNull() ?: 0f
        } else {
            userRow.getOrNull(headers.indexOf("HEIFAtotalscoreFemale"))?.toFloatOrNull() ?: 0f
        }
        userChoices["qualityScore"] = qualityScore

        Log.v("CsvExports", "User choices: $userChoices")

        // HEIFA Score Mapping
        val scoreMapping = mapOf(
            "Vegetables" to "VegetablesHEIFAscore",
            "Fruits" to "FruitHEIFAscore",
            "Grains & Cereals" to "GrainsandcerealsHEIFAscore",
            "Whole Grains" to "WholegrainsHEIFAscore",
            "Meat & Alternatives" to "MeatandalternativesHEIFAscore",
            "Dairy" to "DairyandalternativesHEIFAscore",
            "Water" to "WaterHEIFAscore",
            "Unsaturated Fats" to "UnsaturatedFatHEIFAscore",
            "Sodium" to "SodiumHEIFAscore",
            "Sugar" to "SugarHEIFAscore",
            "Alcohol" to "AlcoholHEIFAscore",
            "Discretionary Foods" to "DiscretionaryHEIFAscore"
        )

        for ((category, baseColumn) in scoreMapping) {
            val columnName = if (isMale) "${baseColumn}Male" else "${baseColumn}Female"
            val columnIndex = headers.indexOf(columnName).takeIf { it != -1 } ?: headers.indexOf(baseColumn)

            val score = userRow.getOrNull(columnIndex)?.toFloatOrNull() ?: 0f
            userChoices[category] = score
        }

        // ✅ Save user choices using SharedPreferences
        sharedPreferences.edit {
            userChoices.forEach { (key, value) ->
                when (value) {
                    is Int -> putInt(key, value)
                    is Float -> putFloat(key, value)
                    is Boolean -> putBoolean(key, value)
                    is String -> putString(key, value)
                }
            }
            putBoolean("updated", true) // ✅ Mark as updated
        }

        // 🔥 Retrieve and log the saved data to verify it's stored correctly
        val savedData = sharedPreferences.all
        val updatedFlag = sharedPreferences.getBoolean("updated", false) // Check if updated

        Log.v("CsvExports", "✅ User $userId - Retrieved from SharedPreferences: $savedData")
        Log.v("CsvExports", "✅ User $userId - Updated flag: $updatedFlag")
    }
}
