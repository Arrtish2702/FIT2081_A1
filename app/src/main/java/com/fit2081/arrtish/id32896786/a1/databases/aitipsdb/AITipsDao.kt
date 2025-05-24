package com.fit2081.arrtish.id32896786.a1.databases.aitipsdb
/**
 * Disclaimer:
 * This file may include comments or documentation assisted by OpenAI's GPT model.
 * All code logic and architectural decisions were implemented and verified by the developer.
 */
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Data Access Object (DAO) interface for accessing AI Tips data.
 * Provides methods to insert and query AI tips related to patients.
 */
@Dao
interface AITipsDao {

    /**
     * Inserts an AI tip into the database.
     * If a conflict occurs (e.g., same primary key), the existing entry is replaced.
     *
     * @param tip The AITips object to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTip(tip: AITips)

    /**
     * Retrieves all AI tips associated with a specific patient,
     * ordered by the response timestamp in descending order (most recent first).
     *
     * @param patientId The ID of the patient whose tips are requested.
     * @return A list of AITips for the specified patient.
     */
    @Query("SELECT * FROM ai_tips WHERE patientId = :patientId ORDER BY responseTimeStamp DESC")
    suspend fun getTipsByPatientId(patientId: Int): List<AITips>
}