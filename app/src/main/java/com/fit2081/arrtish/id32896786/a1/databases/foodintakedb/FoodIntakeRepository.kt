package com.fit2081.arrtish.id32896786.a1.databases.foodintakedb

import androidx.lifecycle.LiveData

class FoodIntakeRepository(private val foodIntakeDao: FoodIntakeDao) {

    /** LiveData stream of the patient’s questionnaire (or null if not answered) */
    fun getFoodIntake(patientId: Int): LiveData<FoodIntake?> =
        foodIntakeDao.getFoodIntakeForPatient(patientId)

    /** Insert or update the patient’s questionnaire */
    suspend fun insertFoodIntake(foodIntake: FoodIntake) {
        foodIntakeDao.insert(foodIntake)
    }

    /** Delete the patient’s questionnaire (clearData) */
    suspend fun deleteFoodIntake(patientId: Int) {
        foodIntakeDao.deleteByPatient(patientId)
    }
}

