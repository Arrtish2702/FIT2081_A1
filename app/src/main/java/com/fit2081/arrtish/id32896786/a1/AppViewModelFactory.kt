package com.fit2081.arrtish.id32896786.a1

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

class AppViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    val db = AppDataBase.getDatabase(context)
    val patientRepository = PatientRepository(db.patientDao())
    val foodIntakeRepository = FoodIntakeRepository(db.foodIntakeDao())
    val aiTipsRepository = AITipsRepository(db.aiTipsDao())
    val openAIApi = RetrofitClient.createOpenAiApi()
    val fruityViceApi = RetrofitClient.createFruityViceApi()

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(context.applicationContext as Application, patientRepository, foodIntakeRepository) as T
            }
            modelClass.isAssignableFrom(AuthenticationViewModel::class.java) -> {
                AuthenticationViewModel(foodIntakeRepository, patientRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(patientRepository) as T
            }
            modelClass.isAssignableFrom(InsightsViewModel::class.java) -> {
                InsightsViewModel(patientRepository) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(patientRepository) as T
            }
            modelClass.isAssignableFrom(ClinicianViewModel::class.java) -> {
                ClinicianViewModel(patientRepository, foodIntakeRepository, openAIApi ) as T
            }
            modelClass.isAssignableFrom(NutriCoachViewModel::class.java) -> {
                NutriCoachViewModel(patientRepository, foodIntakeRepository, aiTipsRepository, fruityViceApi, openAIApi) as T
            }
            modelClass.isAssignableFrom(QuestionnaireViewModel::class.java) -> {
                QuestionnaireViewModel(foodIntakeRepository, patientRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
