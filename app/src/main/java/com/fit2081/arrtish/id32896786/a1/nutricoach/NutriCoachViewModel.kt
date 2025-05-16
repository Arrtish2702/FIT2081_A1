package com.fit2081.arrtish.id32896786.a1.nutricoach

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fit2081.arrtish.id32896786.a1.MainActivity
import com.fit2081.arrtish.id32896786.a1.databases.aitipsdb.AITips
import com.fit2081.arrtish.id32896786.a1.databases.aitipsdb.AITipsRepository
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.PatientRepository
import com.fit2081.arrtish.id32896786.a1.gpt.ChatGptApi
import com.fit2081.arrtish.id32896786.a1.gpt.ChatGptRequest
import com.fit2081.arrtish.id32896786.a1.gpt.Message
import kotlinx.coroutines.launch
import java.util.Date

class NutriCoachViewModel(
    private val aiTipsRepository: AITipsRepository,
    private val fruityViceApi: FruityViceApi,
    private val openAiApi: ChatGptApi
) : ViewModel() {

    // Expose the details map as LiveData / State
    private val _fruitDetails = MutableLiveData<Map<String, String>>(emptyMap())
    val fruitDetails: LiveData<Map<String, String>> = _fruitDetails

    private val _motivationalMessage = MutableLiveData<String>("")
    val motivationalMessage: LiveData<String> = _motivationalMessage

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    private val _tipsList = MutableLiveData<List<AITips>>(emptyList())
    val tipsList: LiveData<List<AITips>> = _tipsList

    fun loadAllTips(patientId: Int) {
        viewModelScope.launch {
            _tipsList.value = aiTipsRepository.getTipsByPatientId(patientId)
            val valuse = tipsList.value
            Log.v(MainActivity.TAG, "$valuse")
        }
    }

    fun fetchFruit(name: String) {
        viewModelScope.launch {
            try {
                val fruits = fruityViceApi.getAllFruits() // get all fruits
                val fruit = fruits.firstOrNull { it.name.equals(name.trim(), ignoreCase = true) }

                if (fruit != null) {
                    Log.v(MainActivity.TAG, "fruit: $fruit")

                    _fruitDetails.value = mapOf(
                        "family" to fruit.family,
                        "calories" to fruit.nutritions.calories.toString(),
                        "fat" to fruit.nutritions.fat.toString(),
                        "sugar" to fruit.nutritions.sugar.toString(),
                        "carbohydrates" to fruit.nutritions.carbohydrates.toString(),
                        "protein" to fruit.nutritions.protein.toString()
                    )
                    _errorMessage.value = null
                } else {
                    _fruitDetails.value = emptyMap()
                    _errorMessage.value = "Fruit not found: \"$name\""
                }

            } catch (t: Throwable) {
                _fruitDetails.value = emptyMap()
                _errorMessage.value = "Network error: ${t.localizedMessage}"
            }
        }
    }

    fun generateMotivationalMessage(patientId: Int) {
        viewModelScope.launch {
            try {
                val responseText = openAiApi.getChatResponse(
                    ChatGptRequest(
                        model = "gpt-4.1",
                        messages = listOf(
                            Message("system", "You are a friendly fitness and nutrition coach."),
                            Message("user", "Give me a short inspirational message about healthy eating.")
                        )
                    )
                ).choices.firstOrNull()?.message?.content ?: "Stay healthy!"

                _motivationalMessage.value = responseText

                val newTip = AITips(
                    tipsId = 0, // autoGenerate
                    patientId = patientId,
                    responseTimeStamp = Date(),
                    promptString = "Give me a short inspirational message about healthy eating.",
                    responseString = responseText
                )

                aiTipsRepository.insertTip(newTip)

            } catch (e: Exception) {
                _motivationalMessage.value = "Failed to fetch inspiration: ${e.localizedMessage}"
            }
        }
    }
}
