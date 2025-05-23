package com.fit2081.arrtish.id32896786.a1.api

import com.fit2081.arrtish.id32896786.a1.api.fruityvice.FruityViceApi
import com.fit2081.arrtish.id32896786.a1.api.gpt.ChatGptApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton object that provides Retrofit client instances
 * for different APIs used in the app.
 *
 * Currently supports:
 * - FruityVice API (public fruit info API)
 * - OpenAI API (for ChatGPT integration)
 */
object RetrofitClient {

    // Base URL for FruityVice API
    private const val FRUITYVICE_BASE_URL = "https://www.fruityvice.com/"

    // Base URL for OpenAI API
    private const val OPENAI_BASE_URL = "https://api.openai.com/"

    /**
     * Creates and returns an instance of FruityViceApi using Retrofit.
     * This API provides fruit-related data.
     */
    fun createFruityViceApi(): FruityViceApi =
        Retrofit.Builder()
            .baseUrl(FRUITYVICE_BASE_URL)
            // Gson converter to parse JSON responses into Kotlin objects
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FruityViceApi::class.java)

    /**
     * Creates and returns an instance of ChatGptApi using Retrofit.
     * This API is used to communicate with OpenAI's ChatGPT endpoints.
     *
     * Note: Headers such as Authorization need to be added
     * at the API interface or client level (not included here).
     */
    fun createOpenAiApi(): ChatGptApi =
        Retrofit.Builder()
            .baseUrl(OPENAI_BASE_URL)
            // Gson converter to parse JSON responses
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChatGptApi::class.java)
}
