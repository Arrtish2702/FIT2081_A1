package com.fit2081.arrtish.id32896786.a1.authentication


import java.security.MessageDigest
import android.util.Base64

object PasswordUtils {
    fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.trim().toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(hash, Base64.NO_WRAP)
    }

    fun passwordsMatch(input: String, storedHashed: String): Boolean {
        return hashPassword(input) == storedHashed
    }
}
