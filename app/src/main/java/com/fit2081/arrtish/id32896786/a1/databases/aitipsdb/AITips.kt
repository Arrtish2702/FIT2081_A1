package com.fit2081.arrtish.id32896786.a1.databases.aitipsdb
/**
 * Disclaimer:
 * This file may include comments or documentation assisted by OpenAI's GPT model.
 * All code logic and architectural decisions were implemented and verified by the developer.
 */
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.fit2081.arrtish.id32896786.a1.databases.DateConverter
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.Patient
import java.util.Date

/**
 * Entity class representing AI-generated tips for patients.
 *
 * This class maps to the "ai_tips" table in the Room database.
 * Each AI tip is linked to a specific patient via a foreign key relationship.
 *
 * @property tipsId The primary key for this AI tip entry, auto-generated.
 * @property patientId The ID of the patient this tip belongs to. This is a foreign key to the Patient table.
 * @property responseTimeStamp The timestamp when the AI response was generated.
 * @property promptString The input prompt string used to generate the AI tip.
 * @property responseString The actual AI-generated response or tip.
 */
@Entity(
    tableName = "ai_tips",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,           // Reference Patient entity as parent table
            parentColumns = ["patientId"],    // Parent primary key column
            childColumns = ["patientId"],     // Foreign key column in this table
            onDelete = ForeignKey.CASCADE     // Delete AI tips if the linked patient is deleted
        )
    ],
    indices = [Index(value = ["patientId"])]   // Index on patientId for efficient lookups
)
@TypeConverters(DateConverter::class)         // Use custom converter to store Date objects
data class AITips(
    @PrimaryKey(autoGenerate = true) var tipsId: Int,  // Auto-generated primary key for AI tips
    var patientId: Int,                                 // Foreign key to Patient entity
    var responseTimeStamp: Date,                        // Timestamp of the AI response
    var promptString: String,                           // Input prompt to the AI
    var responseString: String                           // AI-generated tip or response text
)