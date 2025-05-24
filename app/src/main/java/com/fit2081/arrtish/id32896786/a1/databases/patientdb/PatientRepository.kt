package com.fit2081.arrtish.id32896786.a1.databases.patientdb
/**
 * Disclaimer:
 * This file may include comments or documentation assisted by OpenAI's GPT model.
 * All code logic and architectural decisions were implemented and verified by the developer.
 */
import androidx.lifecycle.LiveData

/**
 * Repository class to abstract access to the Patient data source.
 *
 * Handles querying the PatientDao and exposes data via LiveData or suspend functions.
 */
class PatientRepository(private val patientDao: PatientDao) {

    /**
     * Returns LiveData of all patient IDs in the database.
     */
    fun allPatientIds(): LiveData<List<Int>> {
        return patientDao.getAllPatientIds()
    }

    /**
     * Returns LiveData of all registered patient IDs.
     */
    fun allRegisteredPatientIds(): LiveData<List<Int>> {
        return patientDao.getRegisteredPatientIds()
    }

    /**
     * Returns LiveData of all unregistered patient IDs.
     */
    fun allUnregisteredPatientIds(): LiveData<List<Int>> {
        return patientDao.getUnregisteredPatientIds()
    }

    /**
     * Returns LiveData list of all patients.
     */
    fun getAllPatients(): LiveData<List<Patient>> {
        return patientDao.getAllPatients()
    }

    /**
     * Returns LiveData list of all registered patients.
     */
    fun getAllRegisteredPatients(): LiveData<List<Patient>> {
        return patientDao.getRegisteredPatients()
    }

    /**
     * Finds a patient by their ID in a suspend function.
     * @param id The patient ID to look for.
     * @return The Patient object or null if not found.
     */
    suspend fun getPatientById(id: Int): Patient? {
        return patientDao.findPatientById(id)
    }

    /**
     * Updates an existing patient record.
     * @param patient The patient entity with updated data.
     */
    suspend fun updatePatient(patient: Patient) {
        patientDao.updatePatient(patient)
    }

    /**
     * Inserts a new patient if they don't exist, otherwise updates the existing record.
     * @param patient The patient entity to insert or update.
     */
    suspend fun safeInsert(patient: Patient) {
        val existing = getPatientById(patient.patientId)
        if (existing == null) {
            patientDao.insertPatient(patient)
        } else {
            patientDao.updatePatient(patient)
        }
    }
}
