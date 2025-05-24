package com.fit2081.arrtish.id32896786.a1.databases
/**
 * Disclaimer:
 * This file may include comments or documentation assisted by OpenAI's GPT model.
 * All code logic and architectural decisions were implemented and verified by the developer.
 */
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

/**
 * Room database class defining the application's database.
 *
 * This class ties together all entities and DAOs for the app.
 * - Entities: Patient, FoodIntake, AITips
 * - DAOs: PatientDao, FoodIntakeDao, AITipsDao
 *
 * Uses version 4 with fallbackToDestructiveMigration enabled for handling migrations.
 */
@Database(entities = [Patient::class, FoodIntake::class, AITips::class], version = 4)
abstract class AppDataBase : RoomDatabase() {

    // Abstract method to access PatientDao
    abstract fun patientDao(): PatientDao

    // Abstract method to access FoodIntakeDao
    abstract fun foodIntakeDao(): FoodIntakeDao

    // Abstract method to access AITipsDao
    abstract fun aiTipsDao(): AITipsDao

    companion object {
        // Volatile INSTANCE to ensure visibility across threads
        @Volatile private var INSTANCE: AppDataBase? = null

        /**
         * Get a singleton instance of the database.
         *
         * Uses synchronized block to ensure only one instance is created.
         * Falls back to destructive migration on version mismatch.
         *
         * @param context Application context for creating the database.
         * @return Singleton AppDataBase instance.
         */
        fun getDatabase(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "app_database"            // Database file name
                )
                    .fallbackToDestructiveMigration(true)  // Clears DB on version change (use with caution)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
