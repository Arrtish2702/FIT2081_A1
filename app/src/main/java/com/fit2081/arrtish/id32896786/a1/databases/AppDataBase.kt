package com.fit2081.arrtish.id32896786.a1.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fit2081.arrtish.id32896786.a1.databases.aitipsdb.AITips
import com.fit2081.arrtish.id32896786.a1.databases.aitipsdb.AITipsDao
import com.fit2081.arrtish.id32896786.a1.databases.foodintakedb.FoodIntake
import com.fit2081.arrtish.id32896786.a1.databases.foodintakedb.FoodIntakeDao
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.PatientDao
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.Patient

@Database(entities = [Patient::class, FoodIntake::class, AITips::class], version = 4)
abstract class AppDataBase : RoomDatabase() {

    abstract fun patientDao(): PatientDao
    abstract fun foodIntakeDao(): FoodIntakeDao
    abstract fun aiTipsDao(): AITipsDao

    companion object {
        @Volatile private var INSTANCE: AppDataBase? = null

        fun getDatabase(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
