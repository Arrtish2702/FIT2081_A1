package com.fit2081.arrtish.id32896786.a1.internalpages.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.Patient
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.PatientRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: PatientRepository) : ViewModel() {

    private val _patient = MutableLiveData<Patient?>()
    val patient: LiveData<Patient?> = _patient

    private var currentUserId: Int? = null

    fun loadPatientDataById(id: Int) {
        if (id != currentUserId) {
            currentUserId = id
            viewModelScope.launch(Dispatchers.IO) {
                val patientData = repository.getPatientById(id)
                _patient.postValue(patientData)
            }
        }
    }
}