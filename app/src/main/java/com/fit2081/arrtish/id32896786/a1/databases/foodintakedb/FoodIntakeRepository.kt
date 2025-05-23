package com.fit2081.arrtish.id32896786.a1.databases.foodintakedb


class FoodIntakeRepository(private val foodIntakeDao: FoodIntakeDao) {

    /** Get the patient’s questionnaire data (or null if not answered) */
    fun getFoodIntake(patientId: Int): FoodIntake? {
        return foodIntakeDao.getFoodIntakeForPatient(patientId)
    }

    /** LiveData stream of all food intake records */
    suspend fun getAllFoodIntakes(): List<FoodIntake> {
        return foodIntakeDao.getAllFoodIntakes()
    }

    /** Insert or update the patient’s questionnaire */
    suspend fun insertFoodIntake(foodIntake: FoodIntake) {
        foodIntakeDao.insert(foodIntake)
    }

    /** Delete the patient’s questionnaire (clearData) */
    suspend fun deleteFoodIntake(patientId: Int) {
        foodIntakeDao.deleteByPatient(patientId)
    }
}

