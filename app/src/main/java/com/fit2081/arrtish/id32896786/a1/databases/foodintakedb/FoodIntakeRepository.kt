package com.fit2081.arrtish.id32896786.a1.databases.foodintakedb

/**
 * Repository class that abstracts access to FoodIntake data sources.
 * Handles data operations and provides a clean API to the rest of the app.
 *
 * @param foodIntakeDao DAO for accessing FoodIntake table.
 */
class FoodIntakeRepository(private val foodIntakeDao: FoodIntakeDao) {

    /**
     * Get the FoodIntake questionnaire data for a given patient.
     * Returns null if no data exists.
     *
     * @param patientId The patient's ID.
     * @return FoodIntake or null
     */
    fun getFoodIntake(patientId: Int): FoodIntake? {
        return foodIntakeDao.getFoodIntakeForPatient(patientId)
    }

    /**
     * Get a list of all FoodIntake records asynchronously.
     *
     * @return List of all FoodIntake entries.
     */
    suspend fun getAllFoodIntakes(): List<FoodIntake> {
        return foodIntakeDao.getAllFoodIntakes()
    }

    /**
     * Insert or update a FoodIntake record asynchronously.
     *
     * @param foodIntake The FoodIntake entity to insert or update.
     */
    suspend fun insertFoodIntake(foodIntake: FoodIntake) {
        foodIntakeDao.insert(foodIntake)
    }

    /**
     * Delete the FoodIntake record for a specific patient asynchronously.
     *
     * @param patientId The patient's ID whose data should be deleted.
     */
    suspend fun deleteFoodIntake(patientId: Int) {
        foodIntakeDao.deleteByPatient(patientId)
    }
}
