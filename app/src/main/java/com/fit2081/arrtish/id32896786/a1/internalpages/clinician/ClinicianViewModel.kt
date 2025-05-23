package com.fit2081.arrtish.id32896786.a1.internalpages.clinician

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.Patient
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.PatientRepository

import kotlinx.coroutines.launch
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.*
import com.fit2081.arrtish.id32896786.a1.BuildConfig
import com.fit2081.arrtish.id32896786.a1.MainActivity
import com.fit2081.arrtish.id32896786.a1.api.gpt.ChatGptApi
import com.fit2081.arrtish.id32896786.a1.api.gpt.ChatGptRequest
import com.fit2081.arrtish.id32896786.a1.api.gpt.Message
import com.fit2081.arrtish.id32896786.a1.databases.foodintakedb.FoodIntake
import com.fit2081.arrtish.id32896786.a1.databases.foodintakedb.FoodIntakeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class ClinicianViewModel(
    private val patientRepository: PatientRepository,
    private val foodIntakeRepository: FoodIntakeRepository,
    private val openAiApi: ChatGptApi
) : ViewModel() {

    val allPatients: LiveData<List<Patient>> = patientRepository.getAllRegisteredPatients()

    private val _patterns = MutableLiveData<List<String>>()
    val patterns: LiveData<List<String>> = _patterns

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    val generateAvgScores: LiveData<Pair<Float, Float>> = allPatients.map { patients ->
        val malePatients = patients.filter { it.patientSex.equals("male", ignoreCase = true) }
        val femalePatients = patients.filter { it.patientSex.equals("female", ignoreCase = true) }

        val maleAvg = malePatients.takeIf { it.isNotEmpty() }
            ?.map { it.totalScore }?.average()?.toFloat() ?: 0f

        val femaleAvg = femalePatients.takeIf { it.isNotEmpty() }
            ?.map { it.totalScore }?.average()?.toFloat() ?: 0f

        Pair(maleAvg, femaleAvg)
    }


    fun generateInterestingPatterns(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val patients = allPatients.value ?: emptyList()
                val foodIntakes = withContext(Dispatchers.IO) {
                    foodIntakeRepository.getAllFoodIntakes()
                }

                val femaleCount = patients.count { it.patientSex.equals("female", ignoreCase = true) }
                val maleCount = patients.count { it.patientSex.equals("male", ignoreCase = true) }

                Log.v(MainActivity.TAG, "patients: $patients")
                Log.v(MainActivity.TAG, "foodintakes: $foodIntakes")
                Log.v(MainActivity.TAG, "femaleCount: $femaleCount")
                Log.v(MainActivity.TAG, "maleCount: $maleCount")

                if (patients.size < 3 || femaleCount < 1 || maleCount < 1) {
                    val fallbackMessage = """
                    Insufficient number of patients to generate personalized nutrition insights yet. 
                """.trimIndent()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, fallbackMessage, Toast.LENGTH_LONG).show()
                    }
                    _isLoading.value = false
                    return@launch
                }

                if (foodIntakes.isEmpty()) {
                    _patterns.postValue(listOf("No food intake data available to analyze."))
                    return@launch
                }

                val patientCount = patients.size

                val avgTotalScoreBySex = patients.groupBy { it.patientSex.lowercase() }
                    .mapValues { (_, group) -> group.map { it.totalScore }.average() }

                val avgFruitBySex = patients.groupBy { it.patientSex.lowercase() }
                    .mapValues { (_, group) -> group.map { it.fruits }.average() }

                val avgVegBySex = patients.groupBy { it.patientSex.lowercase() }
                    .mapValues { (_, group) -> group.map { it.vegetables }.average() }

                val eatsFruitsPercent = foodIntakes.count { it.eatsFruits } * 100 / foodIntakes.size
                val lateSleepers = foodIntakes.count { it.sleepTime.hours > 22 }

                val commonPersona = foodIntakes.groupBy { it.selectedPersona }
                    .maxByOrNull { it.value.size }?.key ?: "Not specified"

                val prompt = """
                Analyze the following population-level nutrition and behavioral data. Extract **3 distinct, evidence-driven insights** that can assist a clinical nutritionist in guiding interventions or recommendations.

                Prioritize:
                - Behavioral trends
                - At-risk patterns
                - Sex-based differences
                - Diet quality and fruit/vegetable intake
                - Lifestyle correlations (e.g., sleep and nutrition)

                Be specific and base insights strictly on the data provided.

                --- DATA SUMMARY ---
                - Total patients: $patientCount
                - Average total HEIFA score by sex: $avgTotalScoreBySex
                - Average fruit score by sex: $avgFruitBySex
                - Average vegetable score by sex: $avgVegBySex
                - % of patients who eat fruits: $eatsFruitsPercent%
                - Most common dietary persona: $commonPersona
                - Number sleeping after 10 PM: $lateSleepers
            """.trimIndent()

                val messages = listOf(
                    Message(role = "system", content = "You are a clinical nutritionist and population health data analyst. You identify patterns from aggregated diet and behavior data and generate insights for health practitioners."),
                    Message(role = "user", content = prompt)
                )

                val request = ChatGptRequest(
                    model = "gpt-4.1",
                    messages = messages,
                    temperature = 0.7
                )
                val apiKey = "Bearer " + BuildConfig.OPEN_AI_API_KEY
                val response = openAiApi.getChatResponse(apiKey, request)
                val content = response.choices.firstOrNull()?.message?.content ?: "No insights found."

                val allInsights = content
                    .split("\n")
                    .map { it.trim().removePrefix("-").removePrefix("•").trim() }
                    .filter { it.isNotEmpty() }

                _patterns.postValue(allInsights)

            } catch (e: Exception) {
                _patterns.postValue(listOf("Error generating patterns: ${e.message}"))
            } finally {
                _isLoading.value = false
            }
        }
    }
}

