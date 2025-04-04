package com.fit2081.arrtish.id32896786.a1

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import androidx.core.content.edit
import org.json.JSONObject

// CsvExports object - Handles CSV data extraction and saving user insights to SharedPreferences
object CsvExports {
    // Method to retrieve user details and save insights to SharedPreferences
    fun getUserDetailsAndSave(context: Context, userId: String?) {
        // If userId is null or blank, exit the method
        if (userId.isNullOrBlank()) return

        // Get the user's shared preferences using their userId
        val sharedPreferences = UserSharedPreferences.getPreferences(context, userId)

        Log.v("FIT2081-CsvExports", "SharedPreferences: $sharedPreferences")

        // Open and read the CSV file from assets
        val inputStream = context.assets.open("nutritrack_data.csv")
        val reader = BufferedReader(InputStreamReader(inputStream))
        val lines = reader.readLines()

        // Mutable map to hold the user's insights
        val userInsights = mutableMapOf<String, Any>()

        // Extract headers from the first line of the CSV
        val headers = lines.first().split(",").map { it.trim() }
        // Get the rest of the data rows
        val dataRows = lines.drop(1)

        // Find the row corresponding to the userId
        val userRow = dataRows.map { it.split(",").map { value -> value.trim() } }
            .find { it.getOrNull(headers.indexOf("User_ID")) == userId } ?: return

        // Check if the user is male
        val isMale = userRow.getOrNull(headers.indexOf("Sex")) == "Male"
        Log.v("FIT2081-CsvExports", "$userRow and $isMale")

        // Retrieve the quality score based on gender
        val qualityScore = if (isMale) {
            userRow.getOrNull(headers.indexOf("HEIFAtotalscoreMale"))?.toFloatOrNull() ?: 0f
        } else {
            userRow.getOrNull(headers.indexOf("HEIFAtotalscoreFemale"))?.toFloatOrNull() ?: 0f
        }
        userInsights["qualityScore"] = qualityScore

        Log.v("FIT2081-CsvExports", "User insights: $userInsights")

        // Mapping of categories to corresponding column names in the CSV
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

        // Iterate through the categories and map each score from the CSV to the user insights map
        for ((category, baseColumn) in scoreMapping) {
            // Construct the column name for the gender-specific score
            val columnName = if (isMale) "${baseColumn}Male" else "${baseColumn}Female"
            val columnIndex = headers.indexOf(columnName).takeIf { it != -1 } ?: headers.indexOf(baseColumn)

            // Retrieve the score for the category from the corresponding column
            val score = userRow.getOrNull(columnIndex)?.toFloatOrNull() ?: 0f
            userInsights[category] = score
        }

        // Retrieve existing insights from SharedPreferences
        val existingJson = sharedPreferences.getString("insights", "{}")
        val existingInsights: MutableMap<String, Any> =
            try {
                JSONObject(existingJson).toMap().toMutableMap()
            } catch (e: Exception) {
                mutableMapOf()  // Return an empty map if the JSON is invalid
            }

        // Merge the new insights with the existing ones
        existingInsights.putAll(userInsights)

        // Convert the updated insights map to a JSON string
        val updatedJsonString = JSONObject(existingInsights).toString()

        // Save the updated insights back to SharedPreferences
        sharedPreferences.edit {
            putString("insights", updatedJsonString)  // Save the insights JSON string
            putBoolean("updated", true)  // Mark that the insights have been updated
        }

        // Log the saved data and updated flag
        val savedData = sharedPreferences.all
        val updatedFlag = sharedPreferences.getBoolean("updated", false)

        Log.v("FIT2081-CsvExports", "✅ User $userId - Retrieved from SharedPreferences: $savedData")
        Log.v("FIT2081-CsvExports", "✅ User $userId - Updated flag: $updatedFlag")
    }

    // Helper function to convert JSONObject to a map
    private fun JSONObject.toMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        val keys = keys()
        while (keys.hasNext()) {
            val key = keys.next()
            map[key] = this.get(key)
        }
        return map
    }

    // Method to extract user IDs from the CSV and save phone numbers in SharedPreferences
    fun extractUserIdsFromCSV(context: Context): List<String> {
        val userIds = mutableListOf<String>()
        try {
            // Open and read the CSV file from assets
            val inputStream = context.assets.open("nutritrack_data.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))

            // Get SharedPreferences for storing user IDs and phone numbers
            val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()  // Clear any existing data

            // Read each line, extract user ID and phone number, and store them in SharedPreferences
            reader.useLines { lines ->
                lines.drop(1).forEach { line ->  // Skip the header row
                    val values = line.split(",").map { it.trim() }
                    if (values.size >= 2) {
                        val phoneNumber = values[0]
                        val userId = values[1]
                        if (userId.isNotEmpty() && phoneNumber.isNotEmpty()) {
                            userIds.add(userId)
                            editor.putString(userId, phoneNumber)  // Save phone number associated with user ID
                        }
                    }
                }
            }

            // Apply the changes to SharedPreferences
            editor.apply()

            // Log all stored entries in SharedPreferences
            val allEntries = sharedPreferences.all

            Log.v("FIT2081-CsvExports", "---- auth_prefs contents ----")
            for ((key, value) in allEntries) {
                Log.v("FIT2081-CsvExports", "UserID: $key → PhoneNumber: $value")
            }
            Log.v("FIT2081-CsvExports", "------------------------------")

        } catch (e: Exception) {
            e.printStackTrace()  // Print stack trace in case of error
        }
        return userIds  // Return the list of extracted user IDs
    }
}
