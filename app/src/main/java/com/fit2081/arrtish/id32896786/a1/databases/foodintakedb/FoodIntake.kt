package com.fit2081.arrtish.id32896786.a1.databases.foodintakedb

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.fit2081.arrtish.id32896786.a1.databases.DateConverter
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.Patient
import java.util.Date

/**
 * Data entity representing a patient's food intake questionnaire.
 * Each record is linked to a Patient via foreign key on patientId.
 * Uses a TypeConverter for Date fields to store them properly in the database.
 */
@Entity(
    tableName = "food_intake",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["patientId"], // Primary key column in Patient entity
            childColumns = ["patientId"],  // Foreign key column in this entity
            onDelete = ForeignKey.CASCADE  // Delete food intake records if Patient is deleted
        )
    ],
    indices = [Index(value = ["patientId"])] // Index for fast lookups by patientId
)
@TypeConverters(DateConverter::class)
data class FoodIntake(
    @PrimaryKey(autoGenerate = true) val patientId: Int, // Primary key, linked to Patient
    var sleepTime: Date,        // Time patient goes to sleep
    var wakeTime: Date,         // Time patient wakes up
    var biggestMealTime: Date,  // Time of patient's biggest meal of the day
    var selectedPersona: String,// Selected persona or dietary profile for the patient
    // Boolean flags for food consumption habits
    var eatsFruits: Boolean = false,
    var eatsVegetables: Boolean = false,
    var eatsGrains: Boolean = false,
    var eatsRedMeat: Boolean = false,
    var eatsSeafood: Boolean = false,
    var eatsPoultry: Boolean = false,
    var eatsFish: Boolean = false,
    var eatsEggs: Boolean = false,
    var eatsNutsOrSeeds: Boolean = false
)