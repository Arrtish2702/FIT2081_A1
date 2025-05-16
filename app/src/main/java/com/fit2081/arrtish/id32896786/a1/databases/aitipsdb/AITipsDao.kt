package com.fit2081.arrtish.id32896786.a1.databases.aitipsdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AITipsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTip(tip: AITips)

    @Query("SELECT * FROM ai_tips WHERE patientId = :patientId ORDER BY responseTimeStamp DESC")
    suspend fun getTipsByPatientId(patientId: Int): List<AITips>

}