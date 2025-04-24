package com.fit2081.arrtish.id32896786.a1.databases.patientdb

import androidx.room.*

@Dao
interface PatientDao {

    // Insert a new patient into the database
    @Insert
    suspend fun insertPatient(patient: Patient): Long

    // Update an existing patient's data
    @Update
    suspend fun updatePatient(patient: Patient)

    // Delete a patient from the database
    @Delete
    suspend fun deletePatient(patient: Patient)

    // Get all patients from the database
    @Query("SELECT * FROM patients")
    suspend fun getAllPatients(): List<Patient>

    // Get a patient by ID
    @Query("SELECT * FROM patients WHERE patientId = :id")
    suspend fun getPatientById(id: Int): Patient?

    // Get a patient by phone number
    @Query("SELECT * FROM patients WHERE patientPhoneNumber = :phoneNumber")
    suspend fun getPatientByPhoneNumber(phoneNumber: String): Patient?

    // Get patients based on their sex
    @Query("SELECT * FROM patients WHERE patientSex = :sex")
    suspend fun getPatientsBySex(sex: String): List<Patient>
}
