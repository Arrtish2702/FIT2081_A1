package com.fit2081.arrtish.id32896786.a1.clinician

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.Patient
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.PatientRepository

import kotlinx.coroutines.launch
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.*
import com.fit2081.arrtish.id32896786.a1.MainActivity
import com.fit2081.arrtish.id32896786.a1.api.gpt.ChatGptApi
import com.fit2081.arrtish.id32896786.a1.api.gpt.ChatGptRequest
import com.fit2081.arrtish.id32896786.a1.api.gpt.Message
import com.fit2081.arrtish.id32896786.a1.databases.foodintakedb.FoodIntake
import com.fit2081.arrtish.id32896786.a1.databases.foodintakedb.FoodIntakeRepository


class ClinicianViewModel(
    private val patientRepository: PatientRepository,
    private val foodIntakeRepository: FoodIntakeRepository,
    private val openAiApi: ChatGptApi
) : ViewModel() {

    val allPatients: LiveData<List<Patient>> = patientRepository.getAllRegisteredPatients()
    val allFoodIntakes: LiveData<List<FoodIntake>> = foodIntakeRepository.getAllFoodIntakes()

    private val _patterns = MutableLiveData<List<String>>()
    val patterns: LiveData<List<String>> = _patterns

    // Automatically compute average scores when patient list changes
    val generateAvgScores: LiveData<Pair<Float, Float>> = allPatients.map { patients ->
        val malePatients = patients.filter { it.patientSex.equals("male", ignoreCase = true) }
        val femalePatients = patients.filter { it.patientSex.equals("female", ignoreCase = true) }

        val maleAvg = malePatients.takeIf { it.isNotEmpty() }
            ?.map { it.totalScore }?.average()?.toFloat() ?: 0f

        val femaleAvg = femalePatients.takeIf { it.isNotEmpty() }
            ?.map { it.totalScore }?.average()?.toFloat() ?: 0f

        Pair(maleAvg, femaleAvg)
    }


    fun generateInterestingPatterns() {
        viewModelScope.launch {
            try {
                val patients = allPatients.value ?: emptyList()
                val foodIntakes = allFoodIntakes.value ?: emptyList()

                Log.v(MainActivity.TAG,"$patients")
                Log.v(MainActivity.TAG,"$foodIntakes")

                if (patients.isEmpty() || foodIntakes.isEmpty()) {
                    _patterns.postValue(listOf("No data available to analyze."))
                    return@launch
                }

                val patientCount = patients.size
                val avgTotalScore = patients.map { it.totalScore }.average()
                val avgFruitBySex = patients.groupBy { it.patientSex.lowercase() }
                    .mapValues { (_, group) -> group.map { it.fruits }.average() }

                val avgVegBySex = patients.groupBy { it.patientSex.lowercase() }
                    .mapValues { (_, group) -> group.map { it.vegetables }.average() }

                val eatsFruitsPercent = foodIntakes.count { it.eatsFruits } * 100 / foodIntakes.size
                val lateSleepers = foodIntakes.count { it.sleepTime.hours > 22 }  // crude example
                val commonPersona = foodIntakes.groupBy { it.selectedPersona }
                    .maxByOrNull { it.value.size }?.key ?: "Not specified"

                val prompt = """
                You are a clinical nutrition data analyst. Analyze the summary data below and extract 5 key clinician insights. Focus on identifying trends, health flags, and meaningful behavior or score patterns.

                - Total patients: $patientCount
                - Average total HEIFA score: ${"%.2f".format(avgTotalScore)}
                - Average fruit score by sex: $avgFruitBySex
                - Average vegetable score by sex: $avgVegBySex
                - Percentage of patients who eat fruits: $eatsFruitsPercent%
                - Most common dietary persona: $commonPersona
                - Number of patients sleeping after 10 PM: $lateSleepers

                Return 5 unique, comprehensive, and useful clinician insights based on this data.
            """.trimIndent()

                val messages = listOf(
                    Message(role = "system", content = "You are a helpful data analyst."),
                    Message(role = "user", content = prompt)
                )

                val request = ChatGptRequest(
                    model = "gpt-4.1",
                    messages = messages,
                    temperature = 0.7
                )

                val response = openAiApi.getChatResponse(request)
                val content = response.choices.firstOrNull()?.message?.content ?: "No insights found."

                val allInsights = content
                    .split("\n")
                    .map { it.trim().removePrefix("-").removePrefix("•").trim() }
                    .filter { it.isNotEmpty() }

                // Randomly pick 3 insights
                val selected = allInsights.shuffled().take(3)

                _patterns.postValue(selected)

            } catch (e: Exception) {
                _patterns.postValue(listOf("Error generating patterns: ${e.message}"))
            }
        }
    }
}

