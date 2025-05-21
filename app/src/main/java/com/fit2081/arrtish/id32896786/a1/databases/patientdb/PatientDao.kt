package com.fit2081.arrtish.id32896786.a1.databases.patientdb

import androidx.room.*
import kotlinx.coroutines.flow.Flow

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PatientDao {

    @Insert
    suspend fun insertPatient(patient: Patient)

    @Update
    suspend fun updatePatient(patient: Patient)

    @Delete
    suspend fun deletePatient(patient: Patient)

    @Query("SELECT * FROM patients WHERE patientId = :id")
    suspend fun findPatientById(id: Int): Patient?

    @Query("SELECT * FROM patients")
    fun getAllPatients(): LiveData<List<Patient>>

    @Query("SELECT patientId FROM patients WHERE patientName IS NOT NULL AND TRIM(patientName) != '' AND patientPassword IS NOT NULL AND TRIM(patientPassword) != ''")
    fun getRegisteredPatientIds(): LiveData<List<Int>>

    @Query("SELECT * FROM patients WHERE patientName IS NOT NULL AND TRIM(patientName) != '' AND patientPassword IS NOT NULL AND TRIM(patientPassword) != ''")
    fun getRegisteredPatients(): LiveData<List<Patient>>

    @Query("SELECT patientId FROM patients WHERE patientName IS NULL OR TRIM(patientName) = '' AND patientPassword IS NULL OR TRIM(patientPassword) = ''")
    fun getUnregisteredPatientIds(): LiveData<List<Int>>

    @Query("SELECT * FROM patients WHERE patientId = :id")
    fun getPatientById(id: Int): LiveData<Patient?>

    @Query("SELECT * FROM patients WHERE patientPhoneNumber = :phoneNumber")
    fun getPatientByPhoneNumber(phoneNumber: String): LiveData<Patient?>

    @Query("SELECT * FROM patients WHERE patientSex = :sex")
    fun getPatientsBySex(sex: String): LiveData<List<Patient>>

    @Query("SELECT patientId FROM patients")
    fun getAllPatientIds(): LiveData<List<Int>>
}