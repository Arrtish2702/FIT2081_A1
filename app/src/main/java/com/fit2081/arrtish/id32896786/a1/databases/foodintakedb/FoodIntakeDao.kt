package com.fit2081.arrtish.id32896786.a1.databases.foodintakedb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FoodIntakeDao {

    /** Observe the (single) FoodIntake record for this patient, or null if none exists */
    @Query("SELECT * FROM food_intake WHERE patientId = :patientId LIMIT 1")
    fun getFoodIntakeForPatient(patientId: Int): FoodIntake?

    @Query("SELECT * FROM food_intake")
    fun getAllFoodIntakes(): LiveData<List<FoodIntake>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(foodIntake: FoodIntake)

    /** Delete the questionnaire response for this patient (clearData) */
    @Query("DELETE FROM food_intake WHERE patientId = :patientId")
    suspend fun deleteByPatient(patientId: Int)
}
