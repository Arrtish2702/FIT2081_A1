package com.fit2081.arrtish.id32896786.a1.internalpages.insights
/**
 * Disclaimer:
 * This file may include comments or documentation assisted by OpenAI's GPT model.
 * All code logic and architectural decisions were implemented and verified by the developer.
 */
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

/**
 * InsightsViewModel
 *
 * ViewModel responsible for managing the patient's nutrition insights data.
 * It loads patient information from the PatientRepository and provides
 * functionality to share the user's nutrition insights via other apps.
 *
 * @param repository PatientRepository used for data access.
 */
class InsightsViewModel(private val repository: PatientRepository) : ViewModel() {

    // LiveData holding the current patient data loaded
    private val _patient = MutableLiveData<Patient?>()
    val patient: LiveData<Patient?> = _patient

    // Tracks the current user ID to avoid redundant data loading
    private var currentUserId: Int? = null

    /**
     * Loads patient data from the repository by the given patient ID.
     * Only loads if the ID differs from the last loaded user to avoid
     * unnecessary database calls.
     *
     * @param id The patient ID to load.
     */
    fun loadPatientDataById(id: Int) {
        if (id != currentUserId) {
            currentUserId = id
            viewModelScope.launch(Dispatchers.IO) {
                val patientData = repository.getPatientById(id)
                // Post value to LiveData on background thread
                _patient.postValue(patientData)
            }
        }
    }

    /**
     * Shares the user's nutrition insights via a system chooser intent.
     * Constructs a formatted summary text including category scores
     * and total score, then triggers a share Intent.
     *
     * @param context Context to start the share Intent.
     * @param userScores Map of nutrition categories to individual scores.
     * @param totalScore The total food quality score.
     * @param maxScore The maximum possible score.
     */
    fun sharingInsights(
        context: Context,
        userScores: Map<String, Float>,
        totalScore: Float,
        maxScore: Float
    ) {
        // Build the shareable insights text string
        val shareText = buildString {
            append("🌟 Insights: Food Score 🌟\n")
            userScores.forEach { (category, score) ->
                append("$category: %.2f/10\n".format(score))
            }
            append("\n🏆 Total Food Quality Score: %.2f/$maxScore".format(totalScore))
        }

        // Create an ACTION_SEND intent with the share text
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        // Wrap intent with a chooser to let user pick sharing app
        val chooser = Intent.createChooser(intent, "Share your insights via:")
        context.startActivity(chooser)
    }
}