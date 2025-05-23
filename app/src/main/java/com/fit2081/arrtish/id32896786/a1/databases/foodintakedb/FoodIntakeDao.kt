package com.fit2081.arrtish.id32896786.a1.databases.foodintakedb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Data Access Object (DAO) for performing CRUD operations on FoodIntake entities.
 * Defines database queries for FoodIntake table.
 */
@Dao
interface FoodIntakeDao {

    /**
     * Get the FoodIntake record for the specified patientId.
     * Returns null if no record exists.
     *
     * @param patientId The patient's ID to query by.
     * @return FoodIntake or null
     */
    @Query("SELECT * FROM food_intake WHERE patientId = :patientId LIMIT 1")
    fun getFoodIntakeForPatient(patientId: Int): FoodIntake?

    /**
     * Get all FoodIntake records in the database.
     *
     * @return List of all FoodIntake records.
     */
    @Query("SELECT * FROM food_intake")
    suspend fun getAllFoodIntakes(): List<FoodIntake>

    /**
     * Insert a FoodIntake record into the database.
     * If a record with the same primary key exists, it will be replaced.
     *
     * @param foodIntake The FoodIntake entity to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(foodIntake: FoodIntake)

    /**
     * Delete the FoodIntake record for the specified patient.
     *
     * @param patientId The ID of the patient whose record should be deleted.
     */
    @Query("DELETE FROM food_intake WHERE patientId = :patientId")
    suspend fun deleteByPatient(patientId: Int)
}
