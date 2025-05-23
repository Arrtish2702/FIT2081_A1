package com.fit2081.arrtish.id32896786.a1.databases.foodintakedb

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.fit2081.arrtish.id32896786.a1.databases.DateConverter
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.Patient
import java.util.Date

@Entity(
    tableName = "food_intake",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["patientId"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["patientId"])]
)

@TypeConverters(DateConverter::class)
data class FoodIntake(
    @PrimaryKey(autoGenerate = true) val patientId: Int,
    var sleepTime: Date,
    var wakeTime: Date,
    var biggestMealTime: Date,
    var selectedPersona: String,
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

