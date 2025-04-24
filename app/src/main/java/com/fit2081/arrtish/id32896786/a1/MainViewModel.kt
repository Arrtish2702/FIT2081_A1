package com.fit2081.arrtish.id32896786.a1

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.Patient
import com.fit2081.arrtish.id32896786.a1.databases.AppDataBase
import com.fit2081.arrtish.id32896786.a1.databases.scoresdb.HeifaScores

import kotlinx.coroutines.Dispatchers


import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val patientDao = AppDataBase.getDatabase(application).patientDao()
    private val scoresDao = AppDataBase.getDatabase(application).heifaScoreDao()

    fun loadAndInsertData(context: Context) {

        val currentDate = Date()
        println("Current Date object: $currentDate")

        viewModelScope.launch(Dispatchers.IO) {
            val inputStream = context.assets.open("nutritrack_data.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val lines = reader.readLines()

            // List to store inserted patient and score objects for logging later
            val patientsInserted = mutableListOf<Patient>()
            val scoresInserted = mutableListOf<HeifaScores>()

            // Loop through each line in the CSV, skipping the header (first row)
            for (line in lines.drop(1)) {
                val tokens = line.split(",")
                if (tokens.size < 62) continue // skip malformed lines

                val phone = tokens[0]
                val patientId = tokens[1].trim().toInt()
                val name = ""
                val sex = tokens[2]
                val password = "password"

                val patient = Patient(
                    patientId = patientId,
                    patientName = name,
                    patientSex = sex,
                    patientPassword = password,
                    patientPhoneNumber = phone
                )

                val generatedId = patientDao.insertPatient(patient)

                Log.d("MainViewModel", "Inserted patient with internalId = $generatedId")


                val isMale = sex.equals("Male", ignoreCase = true)

                val scores = HeifaScores(
                    internalId = generatedId,
                    timestamp = currentDate,
//                    vegetables = "1".toFloat(),
//                    fruits = "1".toFloat(),
//                    grainsAndCereals = "1".toFloat(),
//                    wholeGrains = "1".toFloat(),
//                    meatAndAlternatives = "1".toFloat(),
//                    dairyAndAlternatives = "1".toFloat(),
//                    water = "1".toFloat(),
//                    unsaturatedFats = "1".toFloat(),
//                    sodium = "1".toFloat(),
//                    sugar = "1".toFloat(),
//                    alcohol = "1".toFloat(),
//                    discretionaryFoods = "1".toFloat(),
//                    totalScore = "1".toFloat(),
                    vegetables = tokens[if (isMale) 8 else 9].toFloat(),
                    fruits = tokens[if (isMale) 19 else 20].toFloat(),
                    grainsAndCereals = tokens[if (isMale) 28 else 29].toFloat(),
                    wholeGrains = tokens[if (isMale) 32 else 33].toFloat(),
                    meatAndAlternatives = tokens[if (isMale) 35 else 36].toFloat(),
                    dairyAndAlternatives = tokens[if (isMale) 39 else 40].toFloat(),
                    water = tokens[if (isMale) 48 else 49].toFloat(),
                    unsaturatedFats = tokens[if (isMale) 60 else 61].toFloat(),
                    sodium = tokens[if (isMale) 43 else 44].toFloat(),
                    sugar = tokens[if (isMale) 54 else 55].toFloat(),
                    alcohol = tokens[if (isMale) 45 else 46].toFloat(),
                    discretionaryFoods = tokens[if (isMale) 5 else 6].toFloat(),
                    totalScore = tokens[if (isMale) 3 else 4].toFloat()
                )

                scoresDao.insertHeifaScores(scores)
            }

            // After all the data has been inserted, log the contents of the databases
            // Fetch all patients and their scores and log them
            val allPatients = patientDao.getAllPatients()
//            val allScores = scoresDao.getAllHeifaScores()
//
//            // Log Patients data
//            Log.d("MainViewModel", "---- All Patients ----")
//            allPatients.forEach { patient ->
//                Log.d("MainViewModel", "Patient: $patient")
//            }

//            // Log HeifaScores data
//            Log.d("MainViewModel", "---- All Heifa Scores ----")
//            allScores.forEach { scores ->
//                Log.d("MainViewModel", "Scores: $scores")
//            }
        }
    }
}
