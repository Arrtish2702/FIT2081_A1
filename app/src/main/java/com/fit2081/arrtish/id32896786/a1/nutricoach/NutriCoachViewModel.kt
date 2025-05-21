package com.fit2081.arrtish.id32896786.a1.nutricoach

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fit2081.arrtish.id32896786.a1.MainActivity
import com.fit2081.arrtish.id32896786.a1.api.fruityvice.FruityViceApi
import com.fit2081.arrtish.id32896786.a1.databases.aitipsdb.AITips
import com.fit2081.arrtish.id32896786.a1.databases.aitipsdb.AITipsRepository
import com.fit2081.arrtish.id32896786.a1.api.gpt.ChatGptApi
import com.fit2081.arrtish.id32896786.a1.api.gpt.ChatGptRequest
import com.fit2081.arrtish.id32896786.a1.api.gpt.Message
import kotlinx.coroutines.Dispatchers
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
        viewModelScope.launch (Dispatchers.IO) {
            _tipsList.postValue(aiTipsRepository.getTipsByPatientId(patientId))
            val values = tipsList.value
            Log.v(MainActivity.TAG, "$values")
        }
    }

    fun fetchFruit(name: String) {
        viewModelScope.launch (Dispatchers.IO) {
            try {
                val fruits = fruityViceApi.getAllFruits() // get all fruits
                val fruit = fruits.firstOrNull { it.name.equals(name.trim(), ignoreCase = true) }

                if (fruit != null) {
                    Log.v(MainActivity.TAG, "fruit: $fruit")

                    _fruitDetails.postValue(mapOf(
                        "family" to fruit.family,
                        "calories" to fruit.nutritions.calories.toString(),
                        "fat" to fruit.nutritions.fat.toString(),
                        "sugar" to fruit.nutritions.sugar.toString(),
                        "carbohydrates" to fruit.nutritions.carbohydrates.toString(),
                        "protein" to fruit.nutritions.protein.toString()
                    ))
                    _errorMessage.postValue(null)
                } else {
                    _fruitDetails.postValue(emptyMap())
                    _errorMessage.postValue("Fruit not found: \"$name\"")
                }

            } catch (t: Throwable) {
                _fruitDetails.postValue(emptyMap())
                _errorMessage.postValue("Network error: ${t.localizedMessage}")
            }
        }
    }

    fun generateMotivationalMessage(patientId: Int) {
        viewModelScope.launch (Dispatchers.IO){
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

                _motivationalMessage.postValue(responseText)

                val newTip = AITips(
                    tipsId = 0, // autoGenerate
                    patientId = patientId,
                    responseTimeStamp = Date(),
                    promptString = "Give me a short inspirational message about healthy eating.",
                    responseString = responseText
                )

                aiTipsRepository.insertTip(newTip)

            } catch (e: Exception) {
                _motivationalMessage.postValue("Failed to fetch inspiration: ${e.localizedMessage}")
            }
        }
    }
}
