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
                // Fetch current data snapshot
                val patients = allPatients.value ?: emptyList()
                val foodIntakes = allFoodIntakes.value ?: emptyList()

                Log.v(MainActivity.TAG,"$patients")
                Log.v(MainActivity.TAG,"$foodIntakes")

                if (patients.isEmpty() || foodIntakes.isEmpty()) {
                    _patterns.postValue(listOf("No data available to analyze."))
                    return@launch
                }

                // Build a summary text for the prompt
                val patientCount = patients.size

                val avgFruitBySex = patients.groupBy { it.patientSex.lowercase() }
                    .mapValues { (_, group) ->
                        group.map { it.fruits }.average()
                    }

                val avgVegBySex = patients.groupBy { it.patientSex.lowercase() }
                    .mapValues { (_, group) ->
                        group.map { it.vegetables }.average()
                    }

                // You can add more data points here...

                val prompt = """
                    Analyze the following dataset summary and provide 3 interesting patterns in simple sentences:

                    - Total patients: $patientCount
                    - Average fruit scores by sex: $avgFruitBySex
                    - Average vegetable scores by sex: $avgVegBySex

                    Use insights such as correlations, differences between groups, or other meaningful trends.
                    """.trimIndent()

                val messages = listOf(
                    Message(role = "system", content = "You are a helpful data analyst."),
                    Message(role = "user", content = prompt)
                )

                val request = ChatGptRequest(
                    model = "gpt-4.1",  // use your model
                    messages = messages,
                    temperature = 0.7
                )

                val response = openAiApi.getChatResponse(request)

                // Extract text response
                val content = response.choices.firstOrNull()?.message?.content ?: "No insights found."

                // Split into lines or sentences (basic split by newline or period)
                val patternsList = content.split("\n")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .take(3)

                _patterns.postValue(patternsList)

            } catch (e: Exception) {
                _patterns.postValue(listOf("Error generating patterns: ${e.message}"))
            }
        }
    }
}

