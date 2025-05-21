package com.fit2081.arrtish.id32896786.a1

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.Patient
import com.fit2081.arrtish.id32896786.a1.databases.AppDataBase
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.PatientRepository

import kotlinx.coroutines.Dispatchers


import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Date
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fit2081.arrtish.id32896786.a1.MainActivity.Companion.PREFS_NAME
import com.fit2081.arrtish.id32896786.a1.authentication.AuthManager
import com.fit2081.arrtish.id32896786.a1.databases.foodintakedb.FoodIntakeDao
import com.fit2081.arrtish.id32896786.a1.databases.foodintakedb.FoodIntakeRepository
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.PatientDao

class MainViewModel(
    application: Application,
    private val patientRepository: PatientRepository,
    private val foodIntakeRepository: FoodIntakeRepository)
: AndroidViewModel(application) {

    val isDarkTheme = mutableStateOf(false)

    private val _hasAnsweredQuestionnaire = MutableLiveData(false)
    val hasAnsweredQuestionnaire: LiveData<Boolean> = _hasAnsweredQuestionnaire

    private val _questionnaireCheckComplete = MutableLiveData(false)
    val questionnaireCheckComplete: LiveData<Boolean> = _questionnaireCheckComplete

    fun checkIfQuestionnaireAnswered(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val intake = foodIntakeRepository.getFoodIntake(userId)
            _hasAnsweredQuestionnaire.postValue(intake != null)
            _questionnaireCheckComplete.postValue(true)
        }
    }

    fun loadAndInsertData(context: Context) {

        val sharedPreferences = context.getSharedPreferences(MainActivity.PREFS_NAME,Context.MODE_PRIVATE)
        val allEntries = sharedPreferences.all
        for ((key, value) in allEntries) {
            Log.v(MainActivity.TAG, "SharedPref entry: $key = $value")
        }
        val isDataLoaded = sharedPreferences.getBoolean("isDataLoaded", false)

        // If data has already been loaded, return early
        if (isDataLoaded) {
            Log.v(MainActivity.TAG, "MainViewModel: Data already loaded. Skipping CSV insertion.")
            return
        }

        Log.v(MainActivity.TAG, "MainViewModel:Getting csv data")
        val currentDate = Date()
        println("Current Date object: $currentDate")

        viewModelScope.launch(Dispatchers.IO) {
            val inputStream = context.assets.open("nutritrack_data.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val lines = reader.readLines()
            // Loop through each line in the CSV, skipping the header (first row)
            for (line in lines.drop(1)) {
                val tokens = line.split(",")
                if (tokens.size < 62) continue // skip malformed lines

                val phone = tokens[0]
                val patientId = tokens[1].trim().toInt()
                val name = ""
                val sex = tokens[2]
                val password = ""
                val isMale = sex.equals("Male", ignoreCase = true)

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
                patientRepository.safeInsert(patient)
            }

            // After data insertion, set the flag in SharedPreferences
            sharedPreferences.edit() {
                putBoolean("isDataLoaded", true)
            }

            Log.v(MainActivity.TAG, "MainViewModel:Data loaded and inserted successfully.")
        }
    }

    fun loadThemePreference(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        isDarkTheme.value = prefs.getBoolean("dark_mode", false)
    }

    fun saveThemePreference(context: Context, isDark: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean("dark_mode", isDark).apply()
        isDarkTheme.value = isDark
    }
}
