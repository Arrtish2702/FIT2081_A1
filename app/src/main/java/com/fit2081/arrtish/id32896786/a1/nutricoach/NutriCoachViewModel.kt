package com.fit2081.arrtish.id32896786.a1.nutricoach

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fit2081.arrtish.id32896786.a1.MainActivity
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.PatientRepository
import kotlinx.coroutines.launch

class NutriCoachViewModel(
    private val api: FruityViceApi
) : ViewModel() {

    // Expose the details map as LiveData / State
    private val _fruitDetails = MutableLiveData<Map<String, String>>(emptyMap())
    val fruitDetails: LiveData<Map<String, String>> = _fruitDetails

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

//    fun fetchFruit(name: String) {
//        Log.v(MainActivity.TAG , "Making API call")
//        viewModelScope.launch {
////            try {
//                val allFruits = api.getAllFruits()
//                Log.v(MainActivity.TAG , "Api return: $allFruits")
//
//                val fruit = allFruits.find { it.name.equals(name.trim(), ignoreCase = true) }
//
//                if (fruit != null) {
//                    _fruitDetails.value = mapOf(
//                        "family" to fruit.family,
//                        "calories" to fruit.nutritions.calories.toString(),
//                        "fat" to fruit.nutritions.fat.toString(),
//                        "sugar" to fruit.nutritions.sugar.toString(),
//                        "carbohydrates" to fruit.nutritions.carbohydrates.toString(),
//                        "protein" to fruit.nutritions.protein.toString()
//                    )
//                    _errorMessage.value = null
//                } else {
//                    _errorMessage.value = "No fruit found for \"$name\""
//                    _fruitDetails.value = emptyMap()
//                }
//
////            } catch (t: Throwable) {
////                _errorMessage.value = "Network error: ${t.localizedMessage}"
////                _fruitDetails.value = emptyMap()
////            }
//        }
//    }


    fun fetchFruit(name: String) {
        viewModelScope.launch {
            try {
                val fruit = api.getFruitByName(name.trim().lowercase())

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

}
