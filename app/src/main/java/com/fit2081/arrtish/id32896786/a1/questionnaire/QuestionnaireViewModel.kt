package com.fit2081.arrtish.id32896786.a1.questionnaire

import android.app.TimePickerDialog
import android.content.Context
import android.util.Log
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// 3) ViewModel
class QuestionnaireViewModel(
    private val foodIntakeRepository: FoodIntakeRepository,
    private val patientRepository: PatientRepository           // inject via factory or pass in after login
) : ViewModel() {

    private val _patient = MutableLiveData<Patient?>()
    val patient: LiveData<Patient?> = _patient

    // This will hold the current userId
    private var patientId: Int? = null

    private val _existingIntake = MutableLiveData<FoodIntake?>()
    val existingIntake: LiveData<FoodIntake?> = _existingIntake

    fun loadPatientDataByIdAndIntake(id: Int) {
        if (id != patientId) {
            patientId = id
            viewModelScope.launch(Dispatchers.IO) {
                val patientData = patientRepository.getPatientById(id)
                val intakeData = foodIntakeRepository.getFoodIntake(id)
//                Log.v(MainActivity.TAG, "QuestionnaireVM: loaded intakeData from method: $intakeData")
                _patient.postValue(patientData)
                _existingIntake.postValue(intakeData)
                Log.v(MainActivity.TAG, "QuestionnaireVM: loaded intakeData: $intakeData")
                Log.v(MainActivity.TAG, "QuestionnaireVM: loaded existingIntake: $existingIntake")
            }
        } else {
            Log.v(MainActivity.TAG, "QuestionnaireVM: no intake")
        }
    }


    /** Saves or replaces the patient’s FoodIntake */
    fun saveFoodIntake(
        selectedCategories: List<String>,
        biggestMealTime: String,
        sleepTime: String,
        wakeTime: String,
        selectedPersona: String
    ) {
        val fmt = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val intake = FoodIntake(
            patientId        = patientId?:-1,
            sleepTime        = fmt.parse(sleepTime)     ?: Date(),
            wakeTime         = fmt.parse(wakeTime)      ?: Date(),
            biggestMealTime  = fmt.parse(biggestMealTime) ?: Date(),
            selectedPersona  = selectedPersona,
            eatsFruits       = "Fruits"      in selectedCategories,
            eatsVegetables   = "Vegetables"  in selectedCategories,
            eatsGrains       = "Grains"      in selectedCategories,
            eatsRedMeat      = "Red Meat"    in selectedCategories,
            eatsSeafood      = "Seafood"     in selectedCategories,
            eatsPoultry      = "Poultry"     in selectedCategories,
            eatsFish         = "Fish"        in selectedCategories,
            eatsEggs         = "Eggs"        in selectedCategories,
            eatsNutsOrSeeds  = "Nuts/Seeds"  in selectedCategories
        )
        Log.v(MainActivity.TAG, "QuestionnaireVM: intake to save $intake ")

        viewModelScope.launch {
            foodIntakeRepository.insertFoodIntake(intake)
        }
        Log.v(MainActivity.TAG, "QuestionnaireVM: data saved to db")
    }

    /** Clears the saved questionnaire for this patient */
    fun clearFoodIntake() {
        viewModelScope.launch {
            foodIntakeRepository.deleteFoodIntake(patientId?:-1)
        }
        Log.v(MainActivity.TAG, "QuestionnaireVM: data cleared from db")
    }

    // Function to format time to "hh:mm a" format
    fun formatTime(hour: Int, minute: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)

        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())  // Date format for time
        return format.format(calendar.time)  // Return formatted time
    }

    // Function to open a time picker dialog and set the time
    fun openTimePicker(context: Context,initialTime: String, onTimeSet: (String) -> Unit) {
        val calendar = Calendar.getInstance()  // Get current calendar instance
        val initialHour = calendar.get(Calendar.HOUR_OF_DAY)  // Initial hour of the day
        val initialMinute = calendar.get(Calendar.MINUTE)  // Initial minute of the day

        // Create and show time picker dialog
        val timePickerDialog = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val time = formatTime(hourOfDay, minute)  // Format time
                onTimeSet(time)  // Set the chosen time
            },
            initialHour, initialMinute, false
        )
        timePickerDialog.show()  // Show time picker dialog
    }
}

