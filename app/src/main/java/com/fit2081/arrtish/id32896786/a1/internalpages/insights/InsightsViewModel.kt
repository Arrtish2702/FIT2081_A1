package com.fit2081.arrtish.id32896786.a1.internalpages.insights

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.Patient
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.PatientRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InsightsViewModel(private val repository: PatientRepository) : ViewModel() {

    private val _patient = MutableLiveData<Patient?>()
    val patient: LiveData<Patient?> = _patient

    // This will hold the current userId
    private var currentUserId: Int? = null

    // Load patient data based on the userId
    fun loadPatientDataById(id: Int) {
        if (id != currentUserId) {
            currentUserId = id
            // Use Dispatchers.IO for database/networking operations
            viewModelScope.launch(Dispatchers.IO) {
                val patientData = repository.getPatientById(id)
                _patient.postValue(patientData)
            }
        }
    }

    fun sharingInsights(
        context: Context,
        userScores: Map<String, Float>,
        totalScore: Float,
        maxScore: Float
    ) {
        val shareText = buildString {
            append("🌟 Insights: Food Score 🌟\n")
            userScores.forEach { (category, score) ->
                append("$category: %.2f/10\n".format(score))
            }
            append("\n🏆 Total Food Quality Score: %.2f/$maxScore".format(totalScore))
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        val chooser = Intent.createChooser(intent, "Share your insights via:")
        context.startActivity(chooser)
    }
}

