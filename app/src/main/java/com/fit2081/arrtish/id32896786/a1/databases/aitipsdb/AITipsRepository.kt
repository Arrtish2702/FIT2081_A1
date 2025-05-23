package com.fit2081.arrtish.id32896786.a1.databases.aitipsdb

import android.util.Log
import com.fit2081.arrtish.id32896786.a1.MainActivity

/**
 * Repository class that abstracts access to the AITips data source.
 * Acts as a mediator between the data source (DAO) and the rest of the app.
 *
 * @property aiTipsDao The DAO used to perform database operations.
 */
class AITipsRepository(private val aiTipsDao: AITipsDao) {

    /**
     * Inserts a new AI tip into the database.
     * Delegates to the DAO insertTip function.
     *
     * @param tip The AITips object to insert.
     */
    suspend fun insertTip(tip: AITips) {
        aiTipsDao.insertTip(tip)
    }

    /**
     * Retrieves a list of AI tips for a specific patient.
     * Logs the retrieved tips for debugging purposes.
     *
     * @param patientId The ID of the patient to fetch tips for.
     * @return A list of AITips objects for the patient.
     */
    suspend fun getTipsByPatientId(patientId: Int): List<AITips> {
        val test = aiTipsDao.getTipsByPatientId(patientId)
        Log.v(MainActivity.TAG,"$test")
        return aiTipsDao.getTipsByPatientId(patientId)
    }
}