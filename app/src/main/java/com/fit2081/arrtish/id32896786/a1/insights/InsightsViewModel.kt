package com.fit2081.arrtish.id32896786.a1.insights

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.Patient
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.PatientRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InsightsViewModel(private val repository: PatientRepository) : ViewModel() {

    private val _patient = MutableLiveData<Patient?>()
    val patient: LiveData<Patient?> = _patient

    fun loadPatientScoresById(id: Int) {
        viewModelScope.launch {
            _patient.value = repository.getPatientById(id)
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

