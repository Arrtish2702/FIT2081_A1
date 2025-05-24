package com.fit2081.arrtish.id32896786.a1.authentication.passwordmanager
/**
 * Disclaimer:
 * This file may include comments or documentation assisted by OpenAI's GPT model.
 * All code logic and architectural decisions were implemented and verified by the developer.
 */
import java.security.MessageDigest
import android.util.Base64

/**
 * Utility object for password-related operations such as hashing,
 * validation, and matching.
 */
object PasswordUtils {

    /**
     * Hashes the input password using SHA-256 and encodes the result in Base64.
     *
     * @param password The plain text password to hash.
     * @return A Base64 encoded string of the hashed password.
     */
    fun hashPassword(password: String): String {
        // Get SHA-256 MessageDigest instance
        val digest = MessageDigest.getInstance("SHA-256")
        // Compute the SHA-256 hash of the trimmed password bytes
        val hash = digest.digest(password.trim().toByteArray(Charsets.UTF_8))
        // Encode the hash byte array to a Base64 string without line wraps
        return Base64.encodeToString(hash, Base64.NO_WRAP)
    }

    /**
     * Compares an input password against a stored hashed password.
     *
     * @param input The plain text password input to verify.
     * @param storedHashed The stored hashed password to compare against.
     * @return True if the input password's hash matches the stored hash, false otherwise.
     */
    fun passwordsMatch(input: String, storedHashed: String): Boolean {
        // Hash the input password and compare it to the stored hashed password
        return hashPassword(input) == storedHashed
    }

    /**
     * Validates if a password meets defined security criteria.
     *
     * Criteria:
     * - Minimum length of 8 characters
     * - At least one uppercase letter
     * - At least one lowercase letter
     * - At least one digit
     * - At least one special character from the set "!@#$%^&*"
     *
     * @param password The password string to validate.
     * @return True if the password meets all criteria, false otherwise.
     */
    fun isValidPassword(password: String): Boolean {
        val minLength = 8
        val specialChars = "!@#\$%^&*"

        // Check for minimum length and presence of required character types
        return password.length >= minLength &&
                password.any { it.isUpperCase() } &&
                password.any { it.isLowerCase() } &&
                password.any { it.isDigit() } &&
                password.any { it in specialChars }
    }
}
