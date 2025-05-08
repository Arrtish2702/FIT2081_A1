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

    @Query("SELECT * FROM patients WHERE patientId = :id")
    fun getPatientById(id: Int): LiveData<Patient?>

    @Query("SELECT * FROM patients WHERE patientPhoneNumber = :phoneNumber")
    fun getPatientByPhoneNumber(phoneNumber: String): LiveData<Patient?>

    @Query("SELECT * FROM patients WHERE patientSex = :sex")
    fun getPatientsBySex(sex: String): LiveData<List<Patient>>

    @Query("SELECT patientId FROM patients")
    fun getAllPatientIds(): LiveData<List<Int>>
}