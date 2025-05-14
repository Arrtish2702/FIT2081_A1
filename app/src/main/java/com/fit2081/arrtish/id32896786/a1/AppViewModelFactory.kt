package com.fit2081.arrtish.id32896786.a1

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fit2081.arrtish.id32896786.a1.authentication.LoginViewModel
import com.fit2081.arrtish.id32896786.a1.clinician.ClinicianViewModel
import com.fit2081.arrtish.id32896786.a1.databases.AppDataBase
import com.fit2081.arrtish.id32896786.a1.databases.foodintakedb.FoodIntakeRepository
import com.fit2081.arrtish.id32896786.a1.insights.InsightsViewModel
import com.fit2081.arrtish.id32896786.a1.home.HomeViewModel
import com.fit2081.arrtish.id32896786.a1.settings.SettingsViewModel
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.PatientRepository
import com.fit2081.arrtish.id32896786.a1.nutricoach.NutriCoachViewModel
import com.fit2081.arrtish.id32896786.a1.nutricoach.RetrofitClient
import com.fit2081.arrtish.id32896786.a1.questionnaire.QuestionnaireViewModel

class AppViewModelFactory(
    context: Context
) : ViewModelProvider.Factory {
    val db = AppDataBase.getDatabase(context)
    val patientRepository = PatientRepository(db.patientDao())
    val foodIntakeRepository = FoodIntakeRepository(db.foodIntakeDao())

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(patientRepository) as T
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
                ClinicianViewModel(patientRepository) as T
            }
            modelClass.isAssignableFrom(NutriCoachViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                NutriCoachViewModel(
                    fruityViceApi = RetrofitClient.createFruityViceApi(),
                    openAiApi = RetrofitClient.createOpenAiApi(BuildConfig.OPEN_AI_API_KEY)
                ) as T
            }
            modelClass.isAssignableFrom(QuestionnaireViewModel::class.java) -> {
                QuestionnaireViewModel(foodIntakeRepository, patientRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
