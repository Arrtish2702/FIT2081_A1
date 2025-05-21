package com.fit2081.arrtish.id32896786.a1.databases.patientdb

import androidx.lifecycle.LiveData

class PatientRepository(private val patientDao: PatientDao) {

    fun allPatientIds(): LiveData<List<Int>> {
        return patientDao.getAllPatientIds()
    }

    fun allRegisteredPatientIds(): LiveData<List<Int>> {
        return patientDao.getRegisteredPatientIds()
    }

    fun allUnregisteredPatientIds(): LiveData<List<Int>> {
        return patientDao.getUnregisteredPatientIds()
    }

    fun getAllPatients(): LiveData<List<Patient>> {
        return patientDao.getAllPatients()
    }

    fun getAllRegisteredPatients(): LiveData<List<Patient>> {
        return patientDao.getRegisteredPatients()
    }

    suspend fun getPatientById(id: Int): Patient? {
        return patientDao.findPatientById(id)
    }

    suspend fun updatePatient(patient: Patient) {
        patientDao.updatePatient(patient)
    }

    suspend fun safeInsert(patient: Patient) {
        val existing = getPatientById(patient.patientId)
        if (existing == null) {
            patientDao.insertPatient(patient)
        } else {
            patientDao.updatePatient(patient)
        }
    }
}
