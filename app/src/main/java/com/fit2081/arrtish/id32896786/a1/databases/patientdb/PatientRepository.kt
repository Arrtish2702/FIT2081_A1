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

    suspend fun safeInsert(patient: Patient){
        val existing = getPatientById(patient.patientId)
        if (existing == null) {
            patientDao.insertPatient(patient)
        } else {
            patientDao.updatePatient(patient) // You'll need an update method too
        }
    }

    // Add more methods to wrap DAO calls as needed
}