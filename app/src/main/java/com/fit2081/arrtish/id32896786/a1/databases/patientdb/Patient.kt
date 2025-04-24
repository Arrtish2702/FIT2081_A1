package com.fit2081.arrtish.id32896786.a1.databases.patientdb

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.fit2081.arrtish.id32896786.a1.databases.DateConverter
import java.util.Date

@Entity(tableName = "patients")
@TypeConverters(DateConverter::class)
data class Patient(
    @PrimaryKey(autoGenerate = true) var patientId: Int,
    var patientName: String,
    var patientSex: String,
    var patientPassword: String,
    var patientPhoneNumber: String,
    val vegetables: Float,
    val fruits: Float,
    val grainsAndCereals: Float,
    val wholeGrains: Float,
    val meatAndAlternatives: Float,
    val dairyAndAlternatives: Float,
    val water: Float,
    val unsaturatedFats: Float,
    val sodium: Float,
    val sugar: Float,
    val alcohol: Float,
    val discretionaryFoods: Float,
    val totalScore: Float
)