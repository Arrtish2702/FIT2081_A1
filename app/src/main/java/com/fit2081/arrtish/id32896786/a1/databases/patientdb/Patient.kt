package com.fit2081.arrtish.id32896786.a1.databases.patientdb
/**
 * Disclaimer:
 * This file may include comments or documentation assisted by OpenAI's GPT model.
 * All code logic and architectural decisions were implemented and verified by the developer.
 */
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.fit2081.arrtish.id32896786.a1.databases.DateConverter
import java.util.Date

/**
 * Data class representing a Patient entity in the Room database.
 *
 * Each patient has a unique auto-generated ID and multiple nutrition-related fields.
 */
@Entity(tableName = "patients")
@TypeConverters(DateConverter::class)
data class Patient(
    @PrimaryKey(autoGenerate = true) var patientId: Int, // Unique patient ID, auto-generated
    var patientName: String,                             // Patient's full name
    var patientSex: String,                              // Patient's gender/sex
    var patientPassword: String,                         // Patient's password for authentication
    var patientPhoneNumber: String,                      // Patient's phone number
    val vegetables: Float,                               // Vegetable intake score
    val fruits: Float,                                   // Fruit intake score
    val fruitsVariation: Float,                          // Variation in fruit intake
    val fruitsServingSize: Float,                        // Serving size of fruits
    val grainsAndCereals: Float,                         // Grains and cereals intake score
    val wholeGrains: Float,                              // Whole grains intake score
    val meatAndAlternatives: Float,                      // Meat and alternatives intake score
    val dairyAndAlternatives: Float,                     // Dairy and alternatives intake score
    val water: Float,                                    // Water intake score
    val unsaturatedFats: Float,                          // Unsaturated fats intake score
    val sodium: Float,                                   // Sodium intake score
    val sugar: Float,                                    // Sugar intake score
    val alcohol: Float,                                  // Alcohol intake score
    val discretionaryFoods: Float,                       // Discretionary foods intake score
    val totalScore: Float                                // Total nutrition score
)