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
import com.fit2081.arrtish.id32896786.a1.databases.foodintakedb.FoodIntakeRepository
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.PatientRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class NutriCoachViewModel(
    private val patientRepository: PatientRepository,
    private val foodIntakeRepository: FoodIntakeRepository,
    private val aiTipsRepository: AITipsRepository,
    private val fruityViceApi: FruityViceApi,
    private val openAiApi: ChatGptApi
) : ViewModel() {

    // Expose the details map as LiveData / State
    private val _fruitDetails = MutableLiveData<Map<String, String>>(emptyMap())
    val fruitDetails: LiveData<Map<String, String>> = _fruitDetails

    private val _optimalFruitScore = MutableLiveData<Boolean>()
    val optimalFruitScore: LiveData<Boolean> = _optimalFruitScore

    private val _motivationalMessage = MutableLiveData<String>("")
    val motivationalMessage: LiveData<String> = _motivationalMessage

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    private val _tipsList = MutableLiveData<List<AITips>>(emptyList())
    val tipsList: LiveData<List<AITips>> = _tipsList

    private val _isGeneratingMessage = MutableLiveData(false)
    val isGeneratingMessage: LiveData<Boolean> = _isGeneratingMessage


    fun loadAllTips(patientId: Int) {
        viewModelScope.launch (Dispatchers.IO) {
            _tipsList.postValue(aiTipsRepository.getTipsByPatientId(patientId))
            val values = tipsList.value
            Log.v(MainActivity.TAG, "$values")
        }
    }

    fun optimalFruitScoreChecker(patientId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val patient = patientRepository.getPatientById(patientId)

            if (patient != null) {
                val variation = patient.fruitsVariation
                val servingSize = patient.fruitsServingSize
                val isOptimal = variation >= 5.0f && servingSize >= 2.0f
                _optimalFruitScore.postValue(isOptimal)
                Log.v(MainActivity.TAG, "optimal score: $isOptimal | variation=$variation, servingSize=$servingSize")
            } else {
                _optimalFruitScore.postValue(false)
            }
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

    fun generateInsightfulMessage(patientId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _isGeneratingMessage.postValue(true)
            _motivationalMessage.postValue("") // Clear previous message
            try {
                val patient = patientRepository.getPatientById(patientId)
                val foodIntake = foodIntakeRepository.getFoodIntake(patientId)

                if (patient != null && foodIntake != null) {
                    val prompt = """
                    Based on the following individual profile, provide 3 short dietary recommendations (max 2 sentences each) to improve overall health based on common dietary guidelines. Tailor tips to any weaknesses seen in the scores.

                    Name: ${patient.patientName}
                    Sex: ${patient.patientSex}
                    Sleep Time: ${foodIntake.sleepTime}
                    Wake Time: ${foodIntake.wakeTime}
                    Biggest Meal Time: ${foodIntake.biggestMealTime}
                    Selected Persona: ${foodIntake.selectedPersona}

                    Eats:
                    - Fruits: ${foodIntake.eatsFruits}
                    - Vegetables: ${foodIntake.eatsVegetables}
                    - Grains: ${foodIntake.eatsGrains}
                    - Red Meat: ${foodIntake.eatsRedMeat}
                    - Seafood: ${foodIntake.eatsSeafood}
                    - Poultry: ${foodIntake.eatsPoultry}
                    - Fish: ${foodIntake.eatsFish}
                    - Eggs: ${foodIntake.eatsEggs}
                    - Nuts/Seeds: ${foodIntake.eatsNutsOrSeeds}

                    Nutrition Scores:
                    - Vegetables: ${patient.vegetables}
                    - Fruits: ${patient.fruits}
                    - Fruit Variation: ${patient.fruitsVariation}
                    - Fruit Serving Size: ${patient.fruitsServingSize}
                    - Grains & Cereals: ${patient.grainsAndCereals}
                    - Whole Grains: ${patient.wholeGrains}
                    - Meat & Alternatives: ${patient.meatAndAlternatives}
                    - Dairy & Alternatives: ${patient.dairyAndAlternatives}
                    - Water: ${patient.water}
                    - Unsaturated Fats: ${patient.unsaturatedFats}
                    - Sodium: ${patient.sodium}
                    - Sugar: ${patient.sugar}
                    - Alcohol: ${patient.alcohol}
                    - Discretionary Foods: ${patient.discretionaryFoods}
                    - Total HEIFA Score: ${patient.totalScore}
                """.trimIndent()

                    val response = openAiApi.getChatResponse(
                        ChatGptRequest(
                            model = "gpt-4.1",
                            messages = listOf(
                                Message("system", "You are a friendly fitness and nutrition coach."),
                                Message("user", prompt)
                            )
                        )
                    )

                    val tipsRaw = response.choices.firstOrNull()?.message?.content ?: "Eat more whole foods, drink plenty of water, and stay active."
                    val tipsList = tipsRaw.split("\n")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() && (it.first().isDigit() || it.startsWith("-")) }
                        .map { it.removePrefix(Regex("""^\d+[\).]?\s*""").toString()) } // Remove leading numbering like "1. " or "1) "

                    val selectedTip = if (tipsList.isNotEmpty()) tipsList.random() else "Stay consistent and make small healthy changes every day."

                    _motivationalMessage.postValue(selectedTip)

                    aiTipsRepository.insertTip(
                        AITips(
                            tipsId = 0,
                            patientId = patientId,
                            responseTimeStamp = Date(),
                            promptString = prompt,
                            responseString = selectedTip
                        )
                    )
                } else {
                    _motivationalMessage.postValue("Data missing: Could not generate tip.")
                }

            } catch (e: Exception) {
                _motivationalMessage.postValue("Failed to fetch tip: ${e.localizedMessage}")
            } finally {
                _isGeneratingMessage.postValue(false)
            }
        }
    }
}
