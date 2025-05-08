package com.fit2081.arrtish.id32896786.a1.authentication

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.content.Context
import android.util.Log
import com.fit2081.arrtish.id32896786.a1.MainActivity

object AuthManager {
    private const val PREFS_NAME = "MyPrefs"
    private const val USER_ID_KEY = "userId"

    var _userId = mutableStateOf<Int?>(null)
        private set

    fun login(userId: Int, context: Context? = null) {
        _userId.value = userId
        context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)?.edit()?.apply {
            putInt(USER_ID_KEY, userId)
            apply()
        }
        val saved = context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)?.getInt(USER_ID_KEY, -1)
        Log.v(MainActivity.TAG, "Saved userId: $saved")
    }

    fun logout(context: Context? = null) {
        _userId.value = null
        context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)?.edit()?.apply {
            remove(USER_ID_KEY)
            apply()
        }
    }

    fun getStudentId(): Int? = _userId.value

    fun loadSession(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val storedId = prefs.getInt(USER_ID_KEY, -1)
        Log.v("AuthManager", "Loaded userId from SharedPrefs: $storedId")
        if (storedId != -1) {
            _userId.value = storedId
        }
    }
}



