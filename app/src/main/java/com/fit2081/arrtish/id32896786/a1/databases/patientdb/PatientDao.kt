package com.fit2081.arrtish.id32896786.a1.databases.patientdb

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import androidx.lifecycle.LiveData

/**
 * DAO interface defining database operations for Patient entities.
 *
 * Uses Room annotations to generate SQL queries automatically.
 */
@Dao
interface PatientDao {

    /**
     * Insert a new patient into the database.
     * @param patient The patient entity to insert.
     */
    @Insert
    suspend fun insertPatient(patient: Patient)

    /**
     * Update an existing patient's data.
     * @param patient The patient entity with updated values.
     */
    @Update
    suspend fun updatePatient(patient: Patient)

    /**
     * Delete a patient from the database.
     * @param patient The patient entity to delete.
     */
    @Delete
    suspend fun deletePatient(patient: Patient)

    /**
     * Find a patient by their unique patient ID.
     * @param id The patient ID to search for.
     * @return The patient entity or null if not found.
     */
    @Query("SELECT * FROM patients WHERE patientId = :id")
    suspend fun findPatientById(id: Int): Patient?

    /**
     * Get a LiveData list of all patients.
     */
    @Query("SELECT * FROM patients")
    fun getAllPatients(): LiveData<List<Patient>>

    /**
     * Get IDs of patients who have registered with a non-null and non-empty name and password.
     */
    @Query("SELECT patientId FROM patients WHERE patientName IS NOT NULL AND TRIM(patientName) != '' AND patientPassword IS NOT NULL AND TRIM(patientPassword) != ''")
    fun getRegisteredPatientIds(): LiveData<List<Int>>

    /**
     * Get all registered patients (with non-empty name and password).
     */
    @Query("SELECT * FROM patients WHERE patientName IS NOT NULL AND TRIM(patientName) != '' AND patientPassword IS NOT NULL AND TRIM(patientPassword) != ''")
    fun getRegisteredPatients(): LiveData<List<Patient>>

    /**
     * Get IDs of patients who are unregistered (either name or password is null or empty).
     */
    @Query("SELECT patientId FROM patients WHERE patientName IS NULL OR TRIM(patientName) = '' AND patientPassword IS NULL OR TRIM(patientPassword) = ''")
    fun getUnregisteredPatientIds(): LiveData<List<Int>>

    /**
     * Get a patient as LiveData by their ID.
     */
    @Query("SELECT * FROM patients WHERE patientId = :id")
    fun getPatientById(id: Int): LiveData<Patient?>

    /**
     * Get a patient as LiveData by their phone number.
     */
    @Query("SELECT * FROM patients WHERE patientPhoneNumber = :phoneNumber")
    fun getPatientByPhoneNumber(phoneNumber: String): LiveData<Patient?>

    /**
     * Get a list of patients filtered by sex.
     */
    @Query("SELECT * FROM patients WHERE patientSex = :sex")
    fun getPatientsBySex(sex: String): LiveData<List<Patient>>

    /**
     * Get LiveData list of all patient IDs.
     */
    @Query("SELECT patientId FROM patients")
    fun getAllPatientIds(): LiveData<List<Int>>
}