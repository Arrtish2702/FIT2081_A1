package com.fit2081.arrtish.id32896786.a1.databases.patientdb

import androidx.lifecycle.LiveData

class PatientRepository(private val patientDao: PatientDao) {

    fun allPatientIds(): LiveData<List<Int>> {
        return patientDao.getAllPatientIds()
    }

    fun getPatientByIdLive(id: Int): LiveData<Patient?> {
        return patientDao.getPatientById(id)
    }

    fun getPatientByPhoneNumber(phone: String): LiveData<Patient?> {
        return patientDao.getPatientByPhoneNumber(phone)
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

    fun getPatientsBySex(sex: String): LiveData<List<Patient>> {
        return patientDao.getPatientsBySex(sex)
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
