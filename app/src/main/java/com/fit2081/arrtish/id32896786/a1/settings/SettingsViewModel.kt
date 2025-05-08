package com.fit2081.arrtish.id32896786.a1.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.Patient
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.PatientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: PatientRepository) : ViewModel() {

    private val _patient = MutableLiveData<Patient?>()
    val patient: LiveData<Patient?> = _patient

    fun loadPatientScoresById(id: Int) {
        viewModelScope.launch {
            _patient.value = repository.getPatientById(id)
        }
    }
}