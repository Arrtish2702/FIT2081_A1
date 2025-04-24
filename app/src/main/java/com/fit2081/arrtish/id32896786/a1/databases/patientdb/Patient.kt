package com.fit2081.arrtish.id32896786.a1.databases.patientdb

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "patients")
data class Patient(
    @PrimaryKey(autoGenerate = true) val internalId: Long = 0,
    //need to change to foreign key
    var patientId: Int,
    var patientName: String,
    var patientSex: String,
    var patientPassword: String,
    var patientPhoneNumber: String
)