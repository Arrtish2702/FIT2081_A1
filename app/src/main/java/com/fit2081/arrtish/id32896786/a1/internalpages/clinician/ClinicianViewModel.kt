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
import com.fit2081.arrtish.id32896786.a1.databases.foodintakedb.FoodIntakeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * ClinicianViewModel
 *
 * ViewModel responsible for managing clinician-facing data:
 * - Fetching all registered patients
 * - Calculating average nutrition scores by sex
 * - Generating interesting population-level nutrition insights using OpenAI's GPT API
 *
 * @param patientRepository Repository for patient data operations
 * @param foodIntakeRepository Repository for food intake data operations
 * @param openAiApi API interface for OpenAI Chat GPT requests
 */
class ClinicianViewModel(
    private val patientRepository: PatientRepository,
    private val foodIntakeRepository: FoodIntakeRepository,
    private val openAiApi: ChatGptApi
) : ViewModel() {

    // LiveData holding the list of all registered patients from the database
    val allPatients: LiveData<List<Patient>> = patientRepository.getAllRegisteredPatients()

    // MutableLiveData holding generated interesting patterns/insights as a list of strings
    private val _patterns = MutableLiveData<List<String>>()
    val patterns: LiveData<List<String>> = _patterns

    // MutableLiveData to indicate if insight generation/loading is in progress
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    /**
     * LiveData representing a Pair of average total HEIFA scores for male and female patients.
     * Calculated by filtering patients by sex and averaging their totalScore.
     */
    val generateAvgScores: LiveData<Pair<Float, Float>> = allPatients.map { patients ->
        val malePatients = patients.filter { it.patientSex.equals("male", ignoreCase = true) }
        val femalePatients = patients.filter { it.patientSex.equals("female", ignoreCase = true) }

        val maleAvg = malePatients.takeIf { it.isNotEmpty() }
            ?.map { it.totalScore }?.average()?.toFloat() ?: 0f

        val femaleAvg = femalePatients.takeIf { it.isNotEmpty() }
            ?.map { it.totalScore }?.average()?.toFloat() ?: 0f

        Pair(maleAvg, femaleAvg)
    }


    /**
     * Generates interesting nutrition patterns/insights for clinicians based on patient
     * and food intake data, by sending a summary prompt to the OpenAI GPT API.
     *
     * Performs multiple checks to ensure sufficient data:
     * - At least 3 patients in total
     * - At least one male and one female patient
     * - Non-empty food intake data
     *
     * Shows a Toast message if data is insufficient.
     * Posts a list of insight strings to _patterns LiveData when successful.
     *
     * @param context Context for showing Toast messages
     */
    fun generateInterestingPatterns(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true  // Indicate loading started
            try {
                // Get current patient list, fallback to empty list if null
                val patients = allPatients.value ?: emptyList()

                // Get all food intake data on IO thread
                val foodIntakes = withContext(Dispatchers.IO) {
                    foodIntakeRepository.getAllFoodIntakes()
                }

                // Count number of female and male patients
                val femaleCount = patients.count { it.patientSex.equals("female", ignoreCase = true) }
                val maleCount = patients.count { it.patientSex.equals("male", ignoreCase = true) }

                // Log data for debugging
                Log.v(MainActivity.TAG, "patients: $patients")
                Log.v(MainActivity.TAG, "foodintakes: $foodIntakes")
                Log.v(MainActivity.TAG, "femaleCount: $femaleCount")
                Log.v(MainActivity.TAG, "maleCount: $maleCount")

                // Check for minimum patient data requirements
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

                // Check if food intake data is empty
                if (foodIntakes.isEmpty()) {
                    _patterns.postValue(listOf("No food intake data available to analyze."))
                    return@launch
                }

                val patientCount = patients.size

                // Calculate average total HEIFA score by sex
                val avgTotalScoreBySex = patients.groupBy { it.patientSex.lowercase() }
                    .mapValues { (_, group) -> group.map { it.totalScore }.average() }

                // Calculate average fruit intake score by sex
                val avgFruitBySex = patients.groupBy { it.patientSex.lowercase() }
                    .mapValues { (_, group) -> group.map { it.fruits }.average() }

                // Calculate average vegetable intake score by sex
                val avgVegBySex = patients.groupBy { it.patientSex.lowercase() }
                    .mapValues { (_, group) -> group.map { it.vegetables }.average() }

                // Calculate percent of patients who eat fruits
                val eatsFruitsPercent = foodIntakes.count { it.eatsFruits } * 100 / foodIntakes.size

                // Count how many patients sleep after 10 PM (22:00 hours)
                val lateSleepers = foodIntakes.count { it.sleepTime.hours > 22 }

                // Determine the most common dietary persona from food intake data
                val commonPersona = foodIntakes.groupBy { it.selectedPersona }
                    .maxByOrNull { it.value.size }?.key ?: "Not specified"

                // Construct prompt string summarizing key data points for GPT analysis
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

                // Create list of chat messages for OpenAI API
                val messages = listOf(
                    Message(role = "system", content = "You are a clinical nutritionist and population health data analyst. You identify patterns from aggregated diet and behavior data and generate insights for health practitioners."),
                    Message(role = "user", content = prompt)
                )

                // Create the GPT chat request with the specified model and temperature
                val request = ChatGptRequest(
                    model = "gpt-4.1",
                    messages = messages,
                    temperature = 0.7
                )

                // Prepare authorization header with API key from BuildConfig
                val apiKey = "Bearer " + BuildConfig.OPEN_AI_API_KEY

                // Call OpenAI API to get chat response
                val response = openAiApi.getChatResponse(apiKey, request)

                // Extract the content from the first choice message or fallback text
                val content = response.choices.firstOrNull()?.message?.content ?: "No insights found."

                // Split response content into individual insights by lines, cleaning prefixes
                val allInsights = content
                    .split("\n")
                    .map { it.trim().removePrefix("-").removePrefix("•").trim() }
                    .filter { it.isNotEmpty() }

                // Post the insights list to LiveData for UI to observe
                _patterns.postValue(allInsights)

            } catch (e: Exception) {
                // Post error message in case of failure
                _patterns.postValue(listOf("Error generating patterns: ${e.message}"))
            } finally {
                // Loading complete regardless of success or failure
                _isLoading.value = false
            }
        }
    }
}