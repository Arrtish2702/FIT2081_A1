package com.fit2081.arrtish.id32896786.a1.databases.patientdb

import kotlinx.coroutines.flow.Flow

class PatientRepository(private val patientDao: PatientDao) {

    // Fetch all patient IDs as a Flow
    fun allPatientIds(): Flow<List<Int>> {
        return patientDao.getAllPatientIds() // Return the Flow directly
    }

    suspend fun getPatientById(id: Int): Patient? {
        return patientDao.findPatientById(id)
    }

    suspend fun updatePatient(patient: Patient) {
        patientDao.updatePatient(patient)
    }

    // Add more methods to wrap DAO calls as needed
}