package com.fit2081.arrtish.id32896786.a1.databases.aitipsdb

import android.util.Log
import com.fit2081.arrtish.id32896786.a1.MainActivity

class AITipsRepository(private val aiTipsDao: AITipsDao) {

    suspend fun insertTip(tip: AITips) {
        aiTipsDao.insertTip(tip)
    }

    suspend fun getTipsByPatientId(patientId: Int): List<AITips> {
        val test = aiTipsDao.getTipsByPatientId(patientId)
        Log.v(MainActivity.TAG,"$test")
        return aiTipsDao.getTipsByPatientId(patientId)
    }
}