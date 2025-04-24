package com.fit2081.arrtish.id32896786.a1.databases.scoresdb

import androidx.room.*

@Dao
interface HeifaScoresDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHeifaScores(score: HeifaScores)

    @Query("SELECT * FROM heifa_scores WHERE internalId = :internalId ORDER BY timestamp DESC")
    suspend fun getScoresForPatient(internalId: Long): List<HeifaScores>

    @Query("SELECT * FROM heifa_scores WHERE scoreId = :scoreId")
    suspend fun getScoresById(scoreId: Int): HeifaScores?

    @Query("SELECT * FROM heifa_scores")
    suspend fun getAllHeifaScores(): List<HeifaScores>

    @Delete
    suspend fun deleteScores(score: HeifaScores)
}
