package com.fit2081.arrtish.id32896786.a1.internalpages.questionnaire

import android.app.TimePickerDialog
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fit2081.arrtish.id32896786.a1.MainActivity
import com.fit2081.arrtish.id32896786.a1.databases.foodintakedb.FoodIntake
import com.fit2081.arrtish.id32896786.a1.databases.foodintakedb.FoodIntakeRepository
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.Patient
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.PatientRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class QuestionnaireViewModel(
    private val foodIntakeRepository: FoodIntakeRepository,
    private val patientRepository: PatientRepository
) : ViewModel() {

    private val _patient = MutableLiveData<Patient?>()
    val patient: LiveData<Patient?> = _patient

    var questionnaireMessage = mutableStateOf<String?>(null)
        private set

    private var patientId: Int? = null

    private val _existingIntake = MutableLiveData<FoodIntake?>()
    val existingIntake: LiveData<FoodIntake?> = _existingIntake

    val selectedCategories = mutableStateOf<List<String>>(emptyList())
    val biggestMealTime = mutableStateOf("12:00 PM")
    val sleepTime = mutableStateOf("10:00 PM")
    val wakeTime = mutableStateOf("6:00 AM")
    val selectedPersona = mutableStateOf("")

    fun loadPatientDataByIdAndIntake(id: Int) {
        if (id != patientId) {
            patientId = id
            viewModelScope.launch(Dispatchers.IO) {
                val patientData = patientRepository.getPatientById(id)
                val intakeData = foodIntakeRepository.getFoodIntake(id)
                _patient.postValue(patientData)
                _existingIntake.postValue(intakeData)

                intakeData?.let { setExistingFoodIntake(it) }

                Log.v(MainActivity.TAG, "QuestionnaireVM: loaded intakeData: $intakeData")
                Log.v(MainActivity.TAG, "QuestionnaireVM: loaded existingIntake: $existingIntake")
            }

        } else {
            Log.v(MainActivity.TAG, "QuestionnaireVM: no intake")
        }
    }

    fun setExistingFoodIntake(intake: FoodIntake) {
        val fmt = SimpleDateFormat("hh:mm a", Locale.getDefault())
        biggestMealTime.value = fmt.format(intake.biggestMealTime)
        sleepTime.value = fmt.format(intake.sleepTime)
        wakeTime.value = fmt.format(intake.wakeTime)
        selectedPersona.value = intake.selectedPersona

        val categories = mutableListOf<String>()
        if (intake.eatsFruits) categories.add("Fruits")
        if (intake.eatsVegetables) categories.add("Vegetables")
        if (intake.eatsGrains) categories.add("Grains")
        if (intake.eatsRedMeat) categories.add("Red Meat")
        if (intake.eatsSeafood) categories.add("Seafood")
        if (intake.eatsPoultry) categories.add("Poultry")
        if (intake.eatsFish) categories.add("Fish")
        if (intake.eatsEggs) categories.add("Eggs")
        if (intake.eatsNutsOrSeeds) categories.add("Nuts/Seeds")
        selectedCategories.value = categories
    }

    fun saveFoodIntake(
        selectedCategories: List<String>,
        biggestMealTime: String,
        sleepTime: String,
        wakeTime: String,
        selectedPersona: String
    ) {
        val fmt = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val sleepDate = fmt.parse(sleepTime)
        val wakeDate = fmt.parse(wakeTime)
        val mealDate = fmt.parse(biggestMealTime)

        if (selectedCategories.isEmpty()) {
            questionnaireMessage.value = "Please select at least one food category."
            return
        }

        if (selectedPersona.isBlank()) {
            questionnaireMessage.value = "Please select your persona."
            return
        }

        if (sleepDate == wakeDate || wakeDate == mealDate || sleepDate == mealDate) {
            questionnaireMessage.value = "Sleep, wake, and meal times must be different."
            return
        }

        val mealMillis = mealDate!!.toMillisOfDay()
        val sleepMillis = sleepDate!!.toMillisOfDay()
        val wakeMillis = wakeDate!!.toMillisOfDay()

        val isMealDuringSleep = if (sleepMillis < wakeMillis) {
            // e.g., sleep at 10 PM, wake at 6 AM → same day range (invalid case)
            mealMillis in sleepMillis until wakeMillis
        } else {
            // e.g., sleep at 10 PM, wake at 6 AM → crosses midnight
            mealMillis >= sleepMillis || mealMillis < wakeMillis
        }
        Log.v(MainActivity.TAG,"isMealDuringSleep: $isMealDuringSleep")
        if (isMealDuringSleep) {
            questionnaireMessage.value = "Meal time cannot be during sleep time."
            return
        }

        val intake = FoodIntake(
            patientId = patientId ?: -1,
            sleepTime = sleepDate ?: Date(),
            wakeTime = wakeDate ?: Date(),
            biggestMealTime = mealDate ?: Date(),
            selectedPersona = selectedPersona,
            eatsFruits = "Fruits" in selectedCategories,
            eatsVegetables = "Vegetables" in selectedCategories,
            eatsGrains = "Grains" in selectedCategories,
            eatsRedMeat = "Red Meat" in selectedCategories,
            eatsSeafood = "Seafood" in selectedCategories,
            eatsPoultry = "Poultry" in selectedCategories,
            eatsFish = "Fish" in selectedCategories,
            eatsEggs = "Eggs" in selectedCategories,
            eatsNutsOrSeeds = "Nuts/Seeds" in selectedCategories
        )

        viewModelScope.launch(Dispatchers.IO) {
            foodIntakeRepository.insertFoodIntake(intake)
            withContext(Dispatchers.Main) {
                questionnaireMessage.value = "Responses saved successfully."
            }
        }
    }


    fun formatTime(hour: Int, minute: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)

        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return format.format(calendar.time)
    }

    fun openTimePicker(context: Context,initialTime: String, onTimeSet: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val initialHour = calendar.get(Calendar.HOUR_OF_DAY)
        val initialMinute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val time = formatTime(hourOfDay, minute)
                onTimeSet(time)
            },
            initialHour, initialMinute, false
        )
        timePickerDialog.show()
    }

    private fun Date.toMillisOfDay(): Long {
        val cal = Calendar.getInstance()
        cal.time = this
        return (cal.get(Calendar.HOUR_OF_DAY) * 60L + cal.get(Calendar.MINUTE)) * 60 * 1000
    }
}

