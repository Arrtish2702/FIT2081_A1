package com.fit2081.arrtish.id32896786.a1
/**
 * Disclaimer:
 * This file may include comments or documentation assisted by OpenAI's GPT model.
 * All code logic and architectural decisions were implemented and verified by the developer.
 */
import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fit2081.arrtish.id32896786.a1.authentication.AuthenticationViewModel
import com.fit2081.arrtish.id32896786.a1.internalpages.clinician.ClinicianViewModel
import com.fit2081.arrtish.id32896786.a1.databases.AppDataBase
import com.fit2081.arrtish.id32896786.a1.databases.aitipsdb.AITipsRepository
import com.fit2081.arrtish.id32896786.a1.databases.foodintakedb.FoodIntakeRepository
import com.fit2081.arrtish.id32896786.a1.internalpages.insights.InsightsViewModel
import com.fit2081.arrtish.id32896786.a1.internalpages.home.HomeViewModel
import com.fit2081.arrtish.id32896786.a1.internalpages.settings.SettingsViewModel
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.PatientRepository
import com.fit2081.arrtish.id32896786.a1.internalpages.nutricoach.NutriCoachViewModel
import com.fit2081.arrtish.id32896786.a1.api.RetrofitClient
import com.fit2081.arrtish.id32896786.a1.internalpages.questionnaire.QuestionnaireViewModel

/**
 * Factory class for creating ViewModel instances with dependencies injected.
 *
 * Provides repositories and API clients required by ViewModels.
 * Uses the application context to get a singleton instance of the database.
 *
 * @param context Context used to get the database instance.
 */
class AppViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    // Initialize the Room database singleton instance
    val db = AppDataBase.getDatabase(context)

    // Initialize repositories with respective DAOs from the database
    val patientRepository = PatientRepository(db.patientDao())
    val foodIntakeRepository = FoodIntakeRepository(db.foodIntakeDao())
    val aiTipsRepository = AITipsRepository(db.aiTipsDao())

    // Initialize Retrofit API clients for OpenAI and FruityVice
    val openAIApi = RetrofitClient.createOpenAiApi()
    val fruityViceApi = RetrofitClient.createFruityViceApi()

    /**
     * Creates the specified ViewModel class instance with the required dependencies.
     *
     * @param modelClass The class type of the ViewModel to create.
     * @return An instance of the requested ViewModel class.
     * @throws IllegalArgumentException if the ViewModel class is unknown.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                // MainViewModel requires Application context and repositories
                MainViewModel(context.applicationContext as Application, patientRepository, foodIntakeRepository) as T
            }
            modelClass.isAssignableFrom(AuthenticationViewModel::class.java) -> {
                // AuthenticationViewModel requires food intake and patient repositories
                AuthenticationViewModel(foodIntakeRepository, patientRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                // HomeViewModel requires patient repository
                HomeViewModel(patientRepository) as T
            }
            modelClass.isAssignableFrom(InsightsViewModel::class.java) -> {
                // InsightsViewModel requires patient repository
                InsightsViewModel(patientRepository) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                // SettingsViewModel requires patient repository
                SettingsViewModel(patientRepository) as T
            }
            modelClass.isAssignableFrom(ClinicianViewModel::class.java) -> {
                // ClinicianViewModel requires patient repo, food intake repo, and OpenAI API
                ClinicianViewModel(patientRepository, foodIntakeRepository, openAIApi ) as T
            }
            modelClass.isAssignableFrom(NutriCoachViewModel::class.java) -> {
                // NutriCoachViewModel requires patient repo, food intake repo, AI tips repo, FruityVice API, and OpenAI API
                NutriCoachViewModel(patientRepository, foodIntakeRepository, aiTipsRepository, fruityViceApi, openAIApi) as T
            }
            modelClass.isAssignableFrom(QuestionnaireViewModel::class.java) -> {
                // QuestionnaireViewModel requires food intake repo and patient repo
                QuestionnaireViewModel(foodIntakeRepository, patientRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
