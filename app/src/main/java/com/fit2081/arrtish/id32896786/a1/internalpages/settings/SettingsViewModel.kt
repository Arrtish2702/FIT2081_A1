package com.fit2081.arrtish.id32896786.a1.internalpages.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.Patient
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.PatientRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for handling user settings data.
 * Manages fetching and exposing patient details by user ID.
 *
 * @param repository Injected PatientRepository for data operations
 */
class SettingsViewModel(private val repository: PatientRepository) : ViewModel() {

    // LiveData for exposing the patient to the UI
    private val _patient = MutableLiveData<Patient?>()
    val patient: LiveData<Patient?> = _patient

    // Cache to avoid redundant data loads
    private var currentUserId: Int? = null

    /**
     * Loads patient data from repository by ID.
     * Avoids reloading if the same ID is requested again.
     *
     * @param id The patient ID to load
     */
    fun loadPatientDataById(id: Int) {
        if (id != currentUserId) {
            currentUserId = id
            viewModelScope.launch(Dispatchers.IO) {
                val patientData = repository.getPatientById(id)
                _patient.postValue(patientData) // Update LiveData on main thread
            }
        }
    }
}