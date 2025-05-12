package com.fit2081.arrtish.id32896786.a1.clinician

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.Patient
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.PatientRepository

import kotlinx.coroutines.launch
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.*


class ClinicianViewModel(private val repository: PatientRepository) : ViewModel() {

    val allPatients: LiveData<List<Patient>> = repository.getAllPatients()

    // Automatically compute average scores when patient list changes
    val generateAvgScores: LiveData<Pair<Float, Float>> = allPatients.map { patients ->
        val malePatients = patients.filter { it.patientSex.equals("male", ignoreCase = true) }
        val femalePatients = patients.filter { it.patientSex.equals("female", ignoreCase = true) }

        val maleAvg = malePatients.takeIf { it.isNotEmpty() }
            ?.map { it.totalScore }?.average()?.toFloat() ?: 0f

        val femaleAvg = femalePatients.takeIf { it.isNotEmpty() }
            ?.map { it.totalScore }?.average()?.toFloat() ?: 0f

        Pair(maleAvg, femaleAvg)
    }

    // Store currently selected patient ID
    private val selectedPatientId = MutableLiveData<Int>()

    // Reactively fetch the selected patient from repository
    val selectedPatient: LiveData<Patient?> = selectedPatientId.switchMap { id ->
        repository.getPatientByIdLive(id)
    }

    fun selectPatient(id: Int) {
        selectedPatientId.value = id
    }
}

