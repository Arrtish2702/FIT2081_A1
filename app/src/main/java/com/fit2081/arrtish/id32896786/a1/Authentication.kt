import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.fit2081.arrtish.id32896786.a1.HomeActivity
import com.fit2081.arrtish.id32896786.a1.LoginActivity

object Authentication {
    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        Log.v("Authentication", "got the auth pref")
    }

    fun login(context: Context, userId: String, phoneNumber: String): Boolean {
        // Save login info
        sharedPreferences.edit {
            putString("user_id", userId)
            putString("phone_number", phoneNumber)
        }

        // Initialize user preferences
        val userPreferences = UserSharedPreferences(context, userId)
        if (!userPreferences.doesUserPrefsExist()) {
            userPreferences.saveUserChoices(mapOf("first_login" to true))  // Mark first login

            Log.v("Authentication", "first time login")
        }else{
            Log.v("Authentication", "existing user login")
        }

        Log.v("Authentication", "Routing to Home page")
        Log.v("Authentication", "$userPreferences")

        // Navigate to HomeActivity with userId as an extra
        val intent = Intent(context, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("user_id", userId)  // Passing userId to HomeActivity
        }
        context.startActivity(intent)
        return true
    }

    fun logout(context: Context) {
        sharedPreferences.edit {
            remove("user_id")
            remove("phone_number")
        }
        Log.v("Authentication", "Routing to Login page")
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.contains("user_id")
    }

    fun getUserId(): String? {
        return sharedPreferences.getString("user_id", null)
    }
}
