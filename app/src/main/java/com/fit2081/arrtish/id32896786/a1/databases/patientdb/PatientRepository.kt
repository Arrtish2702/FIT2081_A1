package com.fit2081.arrtish.id32896786.a1.databases.patientdb

import kotlinx.coroutines.flow.Flow

class PatientRepository(private val patientDao: PatientDao) {

    // Fetch all patient IDs as a Flow
    fun allPatientIds(): Flow<List<Int>> {
        return patientDao.getAllPatientIds() // Return the Flow directly
    }

    // Add more methods to wrap DAO calls as needed
}