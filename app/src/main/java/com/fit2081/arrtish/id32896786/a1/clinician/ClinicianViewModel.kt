package com.fit2081.arrtish.id32896786.a1.clinician

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.Patient
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.PatientRepository
import com.fit2081.arrtish.id32896786.a1.settings.SettingsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClinicianViewModel(private val repository: PatientRepository) : ViewModel()  {

    private val _patient = MutableStateFlow<Patient?>(null)
    val patient: StateFlow<Patient?> = _patient

    suspend fun generateAvgScores(): Pair<Float, Float> {
        val patients = repository.getAllPatientsOnce()

        val malePatients = patients.filter { it.patientSex.equals("male", ignoreCase = true) }
        val femalePatients = patients.filter { it.patientSex.equals("female", ignoreCase = true) }

        val maleScore = if (malePatients.isNotEmpty()) {
            malePatients.map { it.totalScore }.average().toFloat()
        } else 0f

        val femaleScore = if (femalePatients.isNotEmpty()) {
            femalePatients.map { it.totalScore }.average().toFloat()
        } else 0f

        return Pair(maleScore, femaleScore)
    }

    class ClinicianViewModelFactory(private val repository: PatientRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ClinicianViewModel::class.java)) {
                return ClinicianViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}