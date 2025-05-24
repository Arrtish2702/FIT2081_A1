package com.fit2081.arrtish.id32896786.a1.internalpages.home
/**
 * Disclaimer:
 * This file may include comments or documentation assisted by OpenAI's GPT model.
 * All code logic and architectural decisions were implemented and verified by the developer.
 */
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.Patient
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.PatientRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * HomeViewModel
 *
 * ViewModel responsible for loading and exposing patient data for the home/internal page.
 * Uses a PatientRepository to retrieve Patient data from the database.
 *
 * @param repository PatientRepository instance for database operations.
 */
class HomeViewModel(private val repository: PatientRepository) : ViewModel() {

    // Backing LiveData holding the current Patient data (nullable if no data loaded)
    private val _patient = MutableLiveData<Patient?>()

    // Public LiveData for observers (e.g., UI) to get patient data updates
    val patient: LiveData<Patient?> = _patient

    // Holds the currently loaded user ID to avoid redundant reloads
    private var currentUserId: Int? = null

    /**
     * Loads the patient data by user ID asynchronously.
     * If the requested ID differs from the currently loaded one,
     * launches a coroutine to fetch the patient data from the repository.
     *
     * @param id The patient/user ID to load data for.
     */
    fun loadPatientDataById(id: Int) {
        if (id != currentUserId) {
            currentUserId = id
            viewModelScope.launch(Dispatchers.IO) {
                // Fetch patient data from database (may return null if not found)
                val patientData = repository.getPatientById(id)

                // Post the result to LiveData so UI can observe and update
                _patient.postValue(patientData)
            }
        }
    }
}