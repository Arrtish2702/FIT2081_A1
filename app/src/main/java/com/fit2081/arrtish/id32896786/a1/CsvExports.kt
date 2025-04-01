package com.fit2081.arrtish.id32896786.a1

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import androidx.core.content.edit
import org.json.JSONObject
object CsvExports {
    fun getUserDetailsAndSave(context: Context, userId: String?) {
        if (userId.isNullOrBlank()) return // Handle null/empty userId

        // ✅ Obtain existing user preferences
        val sharedPreferences = UserSharedPreferences.getPreferences(context, userId)

        Log.v("FIT2081-CsvExports", "SharedPreferences: $sharedPreferences")

        val inputStream = context.assets.open("nutritrack_data.csv")
        val reader = BufferedReader(InputStreamReader(inputStream))
        val lines = reader.readLines()

        val userInsights = mutableMapOf<String, Any>()

        val headers = lines.first().split(",").map { it.trim() } // Extract headers
        val dataRows = lines.drop(1) // Skip header row

        // Find the user's row
        val userRow = dataRows.map { it.split(",").map { value -> value.trim() } }
            .find { it.getOrNull(headers.indexOf("User_ID")) == userId } ?: return

        val isMale = userRow.getOrNull(headers.indexOf("Sex")) == "Male"
        Log.v("FIT2081-CsvExports", "$userRow and $isMale")

        // Extract quality score
        val qualityScore = if (isMale) {
            userRow.getOrNull(headers.indexOf("HEIFAtotalscoreMale"))?.toFloatOrNull() ?: 0f
        } else {
            userRow.getOrNull(headers.indexOf("HEIFAtotalscoreFemale"))?.toFloatOrNull() ?: 0f
        }
        userInsights["qualityScore"] = qualityScore

        Log.v("FIT2081-CsvExports", "User insights: $userInsights")

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
            userInsights[category] = score
        }

        // ✅ Retrieve existing insights from SharedPreferences
        val existingJson = sharedPreferences.getString("insights", "{}")
        val existingInsights: MutableMap<String, Any> =
            try {
                JSONObject(existingJson).toMap().toMutableMap()
            } catch (e: Exception) {
                mutableMapOf()
            }

        // ✅ Merge new insights with existing ones
        existingInsights.putAll(userInsights)

        // ✅ Save merged insights
        val updatedJsonString = JSONObject(existingInsights).toString()

        sharedPreferences.edit {
            putString("insights", updatedJsonString) // Save merged insights
            putBoolean("updated", true) // ✅ Mark as updated
        }

        // 🔥 Retrieve and log the saved data to verify it's stored correctly
        val savedData = sharedPreferences.all
        val updatedFlag = sharedPreferences.getBoolean("updated", false)

        Log.v("FIT2081-CsvExports", "✅ User $userId - Retrieved from SharedPreferences: $savedData")
        Log.v("FIT2081-CsvExports", "✅ User $userId - Updated flag: $updatedFlag")
    }

    // Helper function to convert JSONObject to a Map
    private fun JSONObject.toMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        val keys = keys()
        while (keys.hasNext()) {
            val key = keys.next()
            map[key] = this.get(key)
        }
        return map
    }
}
