package com.fit2081.arrtish.id32896786.a1.questionnaire

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fit2081.arrtish.id32896786.a1.databases.foodintakedb.FoodIntake
import com.fit2081.arrtish.id32896786.a1.databases.foodintakedb.FoodIntakeRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QuestionnaireViewModel(private val repository: FoodIntakeRepository) : ViewModel() {

    fun saveFoodIntake(
        userId: Int,
        selectedCategories: List<String>,
        biggestMealTime: String,
        sleepTime: String,
        wakeTime: String,
        selectedPersona: String
    ) {
        val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val intake = FoodIntake(
            patientId = userId,
            biggestMealTime = formatter.parse(biggestMealTime) ?: Date(),
            sleepTime = formatter.parse(sleepTime) ?: Date(),
            wakeTime = formatter.parse(wakeTime) ?: Date(),
            selectedPersona = selectedPersona,
            eatsFruits = "Fruits" in selectedCategories,
            eatsVegetables = "Vegetables" in selectedCategories,
            eatsGrains = "Grains" in selectedCategories,
            eatsRedMeat = "Red Meat" in selectedCategories,
            eatsSeafood = "Seafood" in selectedCategories,
            eatsPoultry = "Poultry" in selectedCategories,
            eatsFish = "Fish" in selectedCategories,
            eatsEggs = "Eggs" in selectedCategories,
            eatsNutsOrSeeds = "Nuts/Seeds" in selectedCategories
        )
        viewModelScope.launch {
            repository.insertFoodIntake(intake)
        }
    }

    fun eraseFoodIntake(){

    }
}
