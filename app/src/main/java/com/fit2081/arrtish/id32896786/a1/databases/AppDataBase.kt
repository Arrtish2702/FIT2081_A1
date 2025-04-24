package com.fit2081.arrtish.id32896786.a1.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.PatientDao
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.Patient
import com.fit2081.arrtish.id32896786.a1.databases.scoresdb.HeifaScores
import com.fit2081.arrtish.id32896786.a1.databases.scoresdb.HeifaScoresDao

@Database(entities = [Patient::class, HeifaScores::class], version = 1)
abstract class AppDataBase : RoomDatabase() {

    abstract fun patientDao(): PatientDao
    abstract fun heifaScoreDao(): HeifaScoresDao

    companion object {
        @Volatile private var INSTANCE: AppDataBase? = null

        fun getDatabase(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "patient_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
