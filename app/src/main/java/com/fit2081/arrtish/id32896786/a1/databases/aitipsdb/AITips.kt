package com.fit2081.arrtish.id32896786.a1.databases.aitipsdb

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.fit2081.arrtish.id32896786.a1.databases.DateConverter
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.Patient
import java.util.Date

@Entity(
    tableName = "ai_tips",
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
data class AITips(
    @PrimaryKey(autoGenerate = true) var tipsId: Int,
    var patientId: Int,
    var responseTimeStamp: Date,
    var promptString: String,
    var responseString: String
)