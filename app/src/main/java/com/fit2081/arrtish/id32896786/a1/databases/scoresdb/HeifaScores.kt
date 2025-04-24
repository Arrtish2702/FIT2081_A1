package com.fit2081.arrtish.id32896786.a1.databases.scoresdb

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.fit2081.arrtish.id32896786.a1.databases.DateConverter
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.Patient
import java.util.Date


@Entity(
    tableName = "heifa_scores",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["internalId"],
            childColumns = ["internalId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
            deferred = true
        )
    ],
    indices = [Index(value = ["internalId"])]
)

@TypeConverters(DateConverter::class)
data class HeifaScores(
    @PrimaryKey(autoGenerate = true) val scoreId: Long = 0,
    val internalId: Long,
    val timestamp: Date,
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