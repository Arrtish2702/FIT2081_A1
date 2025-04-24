package com.fit2081.arrtish.id32896786.a1.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.Patient
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.PatientRepository
import com.fit2081.arrtish.id32896786.a1.insights.InsightsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: PatientRepository): ViewModel() {

    private val _patient = MutableStateFlow<Patient?>(null)
    val patient: StateFlow<Patient?> = _patient

    fun loadPatientScoresById(id: Int) {
        viewModelScope.launch {
            _patient.value = repository.getPatientById(id)
        }
    }

    class SettingsViewModelFactory(private val repository: PatientRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(InsightsViewModel::class.java)) {
                return InsightsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}