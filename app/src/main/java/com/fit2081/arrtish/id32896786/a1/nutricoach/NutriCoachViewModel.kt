package com.fit2081.arrtish.id32896786.a1.nutricoach

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fit2081.arrtish.id32896786.a1.MainActivity
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.PatientRepository
import com.fit2081.arrtish.id32896786.a1.gpt.ChatGptApi
import com.fit2081.arrtish.id32896786.a1.gpt.ChatGptRequest
import com.fit2081.arrtish.id32896786.a1.gpt.Message
import kotlinx.coroutines.launch

class NutriCoachViewModel(
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

    fun fetchFruit(name: String) {
        viewModelScope.launch {
            try {
                val fruit = fruityViceApi.getFruitByName(name.trim().lowercase())

                _fruitDetails.value = mapOf(
                    "family" to fruit.family,
                    "calories" to fruit.nutritions.calories.toString(),
                    "fat" to fruit.nutritions.fat.toString(),
                    "sugar" to fruit.nutritions.sugar.toString(),
                    "carbohydrates" to fruit.nutritions.carbohydrates.toString(),
                    "protein" to fruit.nutritions.protein.toString()
                )
                _errorMessage.value = null

            } catch (e: retrofit2.HttpException) {
                if (e.code() == 404) {
                    _errorMessage.value = "Fruit not found: \"$name\""
                } else {
                    _errorMessage.value = "HTTP error: ${e.code()}"
                }
                _fruitDetails.value = emptyMap()

            } catch (t: Throwable) {
                _errorMessage.value = "Network error: ${t.localizedMessage}"
                _fruitDetails.value = emptyMap()
            }
        }
    }

    fun generateMotivationalMessage() {
        viewModelScope.launch {
            try {
                val response = openAiApi.getChatResponse(
                    ChatGptRequest(
                        model = "gpt-4.1",
                        messages = listOf(
                            Message("system", "You are a friendly fitness and nutrition coach."),
                            Message("user", "Give me a short inspirational message about healthy eating.")
                        )
                    )
                )
                _motivationalMessage.value =
                    response.choices.firstOrNull()?.message?.content ?: "Stay healthy!"
            } catch (e: Exception) {
                _motivationalMessage.value = "Failed to fetch inspiration: ${e.localizedMessage}"
            }
        }
    }
}
