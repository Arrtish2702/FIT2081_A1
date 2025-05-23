package com.fit2081.arrtish.id32896786.a1.internalpages.questionnaire

import android.app.TimePickerDialog
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.fit2081.arrtish.id32896786.a1.MainActivity
import com.fit2081.arrtish.id32896786.a1.databases.foodintakedb.FoodIntake
import com.fit2081.arrtish.id32896786.a1.databases.foodintakedb.FoodIntakeRepository
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.Patient
import com.fit2081.arrtish.id32896786.a1.databases.patientdb.PatientRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

/**
 * ViewModel for managing questionnaire state and logic related to food intake and patient data.
 * It handles both UI-bound state (selected times, categories, persona) and database operations.
 */
class QuestionnaireViewModel(
    private val foodIntakeRepository: FoodIntakeRepository,
    private val patientRepository: PatientRepository
) : ViewModel() {

    // LiveData holding patient info based on ID
    private val _patient = MutableLiveData<Patient?>()
    val patient: LiveData<Patient?> = _patient

    // State holding messages for UI (success or validation feedback)
    var questionnaireMessage = mutableStateOf<String?>(null)
        private set

    // Tracks which patient ID is currently being viewed/edited
    private var patientId: Int? = null

    // Holds any existing food intake data for the patient
    private val _existingIntake = MutableLiveData<FoodIntake?>()
    val existingIntake: LiveData<FoodIntake?> = _existingIntake

    // State variables for selected options from UI
    val selectedCategories = mutableStateOf<List<String>>(emptyList())
    val biggestMealTime = mutableStateOf("12:00 PM")
    val sleepTime = mutableStateOf("10:00 PM")
    val wakeTime = mutableStateOf("6:00 AM")
    val selectedPersona = mutableStateOf("")

    /**
     * Loads patient and food intake data from the database using patient ID.
     * If intake exists, updates internal state accordingly.
     */
    fun loadPatientDataByIdAndIntake(id: Int) {
        if (id != patientId) {
            patientId = id
            viewModelScope.launch(Dispatchers.IO) {
                val patientData = patientRepository.getPatientById(id)
                val intakeData = foodIntakeRepository.getFoodIntake(id)
                _patient.postValue(patientData)
                _existingIntake.postValue(intakeData)

                // Populate UI state if intake already exists
                intakeData?.let { setExistingFoodIntake(it) }

                Log.v(MainActivity.TAG, "QuestionnaireVM: loaded intakeData: $intakeData")
                Log.v(MainActivity.TAG, "QuestionnaireVM: loaded existingIntake: $existingIntake")
            }
        } else {
            Log.v(MainActivity.TAG, "QuestionnaireVM: no intake")
        }
    }

    /**
     * Updates ViewModel state based on an existing FoodIntake object.
     */
    fun setExistingFoodIntake(intake: FoodIntake) {
        val fmt = SimpleDateFormat("hh:mm a", Locale.getDefault())
        biggestMealTime.value = fmt.format(intake.biggestMealTime)
        sleepTime.value = fmt.format(intake.sleepTime)
        wakeTime.value = fmt.format(intake.wakeTime)
        selectedPersona.value = intake.selectedPersona

        // Collects all selected food categories into state
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

    /**
     * Saves the user's food intake responses after validation.
     */
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

        // Input validation
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

        // Check if the meal time overlaps with the sleep period
        val mealMillis = mealDate!!.toMillisOfDay()
        val sleepMillis = sleepDate!!.toMillisOfDay()
        val wakeMillis = wakeDate!!.toMillisOfDay()

        val isMealDuringSleep = if (sleepMillis < wakeMillis) {
            mealMillis in sleepMillis until wakeMillis
        } else {
            mealMillis >= sleepMillis || mealMillis < wakeMillis
        }

        Log.v(MainActivity.TAG, "isMealDuringSleep: $isMealDuringSleep")
        if (isMealDuringSleep) {
            questionnaireMessage.value = "Meal time cannot be during sleep time."
            return
        }

        // Create FoodIntake object and save it in DB
        val intake = FoodIntake(
            patientId = patientId ?: -1,
            sleepTime = sleepDate,
            wakeTime = wakeDate,
            biggestMealTime = mealDate,
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

    /**
     * Converts given hour and minute into a formatted time string.
     */
    fun formatTime(hour: Int, minute: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)

        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return format.format(calendar.time)
    }

    /**
     * Opens a TimePickerDialog and returns the selected time to the caller.
     */
    fun openTimePicker(context: Context, initialTime: String, onTimeSet: (String) -> Unit) {
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

    /**
     * Extension function to get the number of milliseconds since midnight for a Date object.
     */
    private fun Date.toMillisOfDay(): Long {
        val cal = Calendar.getInstance()
        cal.time = this
        return (cal.get(Calendar.HOUR_OF_DAY) * 60L + cal.get(Calendar.MINUTE)) * 60 * 1000
    }
}