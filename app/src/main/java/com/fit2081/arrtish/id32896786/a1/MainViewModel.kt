package com.fit2081.arrtish.id32896786.a1

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.Patient
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.PatientRepository
import com.fit2081.arrtish.id32896786.a1.databases.foodintakedb.FoodIntakeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Date
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * MainViewModel
 *
 * ViewModel for managing app-wide data, including:
 * - User preferences (dark theme)
 * - Loading and inserting nutrition patient data from CSV
 * - Checking if user has answered questionnaire based on Food Intake records
 *
 * Uses AndroidViewModel to have access to Application context.
 *
 * @param application The Application instance
 * @param patientRepository Repository for patient database operations
 * @param foodIntakeRepository Repository for food intake database operations
 */
class MainViewModel(
    application: Application,
    private val patientRepository: PatientRepository,
    private val foodIntakeRepository: FoodIntakeRepository
) : AndroidViewModel(application) {

    // Mutable state holding whether dark theme is enabled
    val isDarkTheme = mutableStateOf(false)

    // LiveData tracking if user has answered questionnaire
    private val _hasAnsweredQuestionnaire = MutableLiveData(false)
    val hasAnsweredQuestionnaire: LiveData<Boolean> = _hasAnsweredQuestionnaire

    // LiveData tracking if questionnaire check process is complete
    private val _questionnaireCheckComplete = MutableLiveData(false)
    val questionnaireCheckComplete: LiveData<Boolean> = _questionnaireCheckComplete


    /**
     * Checks asynchronously if a user with given userId has completed the questionnaire.
     * This is done by checking if there is any food intake data for that user.
     * Updates LiveData _hasAnsweredQuestionnaire and _questionnaireCheckComplete accordingly.
     *
     * @param userId The user ID to check for food intake records.
     */
    fun checkIfQuestionnaireAnswered(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val intake = foodIntakeRepository.getFoodIntake(userId)
            _hasAnsweredQuestionnaire.postValue(intake != null)
            _questionnaireCheckComplete.postValue(true)
        }
    }


    /**
     * Loads nutrition patient data from "nutritrack_data.csv" asset and inserts it into the patient database.
     * Uses shared preferences to ensure data is only loaded once (flag "isDataLoaded").
     *
     * Reads the CSV line by line, skips header, parses values, and constructs Patient objects.
     * Uses patient sex to determine indices of gender-specific nutrition data columns.
     *
     * @param context Context used to access assets and shared preferences.
     */
    fun loadAndInsertData(context: Context) {
        val sharedPreferences = context.getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE)
        val allEntries = sharedPreferences.all

        // Log all shared preferences entries for debugging
        for ((key, value) in allEntries) {
            Log.v(MainActivity.TAG, "SharedPref entry: $key = $value")
        }

        // Check if data is already loaded; if yes, skip loading
        val isDataLoaded = sharedPreferences.getBoolean("isDataLoaded", false)
        if (isDataLoaded) {
            Log.v(MainActivity.TAG, "MainViewModel: Data already loaded. Skipping CSV insertion.")
            return
        }

        Log.v(MainActivity.TAG, "MainViewModel:Getting csv data")
        val currentDate = Date()
        println("Current Date object: $currentDate")

        // Launch background coroutine to read CSV and insert patients
        viewModelScope.launch(Dispatchers.IO) {
            val inputStream = context.assets.open("nutritrack_data.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val lines = reader.readLines()

            // Skip header line, iterate through data rows
            for (line in lines.drop(1)) {
                val tokens = line.split(",")
                if (tokens.size < 62) continue  // Ensure enough tokens

                // Parse patient data fields from tokens
                val phone = tokens[0]
                val patientId = tokens[1].trim().toInt()
                val name = ""  // Name not provided in CSV, set empty string
                val sex = tokens[2]
                val password = ""  // Password empty by default
                val isMale = sex.equals("Male", ignoreCase = true)

                // Create Patient object with parsed values and gender-specific nutrition data
                val patient = Patient(
                    patientId = patientId,
                    patientName = name,
                    patientSex = sex,
                    patientPassword = password,
                    patientPhoneNumber = phone,
                    vegetables = tokens[if (isMale) 8 else 9].toFloat(),
                    fruits = tokens[if (isMale) 19 else 20].toFloat(),
                    fruitsVariation = tokens[21].toFloat(),
                    fruitsServingSize = tokens[22].toFloat(),
                    grainsAndCereals = tokens[if (isMale) 29 else 30].toFloat(),
                    wholeGrains = tokens[if (isMale) 33 else 34].toFloat(),
                    meatAndAlternatives = tokens[if (isMale) 36 else 37].toFloat(),
                    dairyAndAlternatives = tokens[if (isMale) 40 else 41].toFloat(),
                    water = tokens[if (isMale) 49 else 50].toFloat(),
                    unsaturatedFats = tokens[if (isMale) 60 else 61].toFloat(),
                    sodium = tokens[if (isMale) 43 else 44].toFloat(),
                    sugar = tokens[if (isMale) 54 else 55].toFloat(),
                    alcohol = tokens[if (isMale) 46 else 47].toFloat(),
                    discretionaryFoods = tokens[if (isMale) 5 else 6].toFloat(),
                    totalScore = tokens[if (isMale) 3 else 4].toFloat()
                )

                // Insert patient into database asynchronously
                patientRepository.safeInsert(patient)
            }

            // Mark data as loaded in shared preferences
            sharedPreferences.edit {
                putBoolean("isDataLoaded", true)
            }

            Log.v(MainActivity.TAG, "MainViewModel:Data loaded and inserted successfully.")
        }
    }


    /**
     * Loads the saved theme preference (dark mode enabled or not) from shared preferences.
     *
     * @param context Context used to access shared preferences.
     */
    fun loadThemePreference(context: Context) {
        val prefs = context.getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE)
        isDarkTheme.value = prefs.getBoolean("dark_mode", false)
    }


    /**
     * Saves the current theme preference (dark mode enabled or not) into shared preferences.
     * Updates the isDarkTheme mutable state to notify UI.
     *
     * @param context Context used to access shared preferences.
     * @param isDark Boolean indicating if dark mode is enabled.
     */
    fun saveThemePreference(context: Context, isDark: Boolean) {
        val prefs = context.getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putBoolean("dark_mode", isDark) }
        isDarkTheme.value = isDark
    }
}