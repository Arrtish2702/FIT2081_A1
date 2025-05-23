package com.fit2081.arrtish.id32896786.a1.authentication

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.content.Context
import android.util.Log
import com.fit2081.arrtish.id32896786.a1.MainActivity

/**
 * AuthManager handles user authentication state and session persistence.
 *
 * This singleton object stores the current logged-in user's ID in a Compose mutable state,
 * allowing reactive UI updates. It also saves and retrieves the user session from
 * SharedPreferences to maintain login state across app restarts.
 */
object AuthManager {

    // Name of SharedPreferences file to store user session info
    private const val PREFS_NAME = "MyPrefs"

    // Key used to store/retrieve the user ID in SharedPreferences
    private const val USER_ID_KEY = "userId"

    // Holds the current logged-in user ID as a Compose mutable state, nullable if logged out
    var _userId = mutableStateOf<Int?>(null)
        private set

    /**
     * Logs in a user by setting the current user ID state and saving it in SharedPreferences.
     *
     * @param userId The ID of the user logging in.
     * @param context Optional context for accessing SharedPreferences.
     */
    fun login(userId: Int, context: Context? = null) {
        // Update mutable state with new userId
        _userId.value = userId

        // Save userId to SharedPreferences if context is available
        context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)?.edit()?.apply {
            putInt(USER_ID_KEY, userId)
            apply()
        }

        // Log the saved userId for debugging purposes
        val saved = context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)?.getInt(USER_ID_KEY, -1)
        Log.v(MainActivity.TAG, "Saved userId: $saved")
    }

    /**
     * Logs out the current user by clearing the user ID state and removing it from SharedPreferences.
     *
     * @param context Optional context for accessing SharedPreferences.
     */
    fun logout(context: Context? = null) {
        // Clear the mutable state to indicate no logged-in user
        _userId.value = null

        // Remove stored userId from SharedPreferences if context is available
        context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)?.edit()?.apply {
            remove(USER_ID_KEY)
            apply()
        }
    }

    /**
     * Returns the current logged-in user ID, or null if no user is logged in.
     */
    fun getStudentId(): Int? = _userId.value

    /**
     * Loads the saved user session from SharedPreferences, if any, and updates the user ID state.
     *
     * @param context Context used to access SharedPreferences.
     */
    fun loadSession(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val storedId = prefs.getInt(USER_ID_KEY, -1)

        Log.v(MainActivity.TAG, "Saved User id: $storedId")

        if (storedId == -1) {
            // No user session found, typically route to welcome or login page
            Log.v(MainActivity.TAG, "AuthManager: No user session found, routing to welcome page.")
        } else {
            // User session found; update mutable state with stored userId
            _userId.value = storedId
            Log.v(MainActivity.TAG, "AuthManager:Loaded userId from SharedPrefs: $storedId")
        }
    }
}
