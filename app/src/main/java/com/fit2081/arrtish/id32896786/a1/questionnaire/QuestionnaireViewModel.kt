package com.fit2081.arrtish.id32896786.a1.questionnaire

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fit2081.arrtish.id32896786.a1.databases.foodintakedb.FoodIntakeRepository

class QuestionnaireViewModel(private val repository: FoodIntakeRepository): ViewModel() {


    class QuestionnaireViewModelFactory(private val repository: FoodIntakeRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(QuestionnaireViewModel::class.java)) {
                return QuestionnaireViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}