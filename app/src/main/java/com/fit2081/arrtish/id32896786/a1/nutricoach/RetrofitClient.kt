package com.fit2081.arrtish.id32896786.a1.nutricoach

import com.fit2081.arrtish.id32896786.a1.gpt.ChatGptApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val FRUITYVICE_BASE_URL = "https://www.fruityvice.com/"
    private const val OPENAI_BASE_URL    = "https://api.openai.com/"

    fun createFruityViceApi(): FruityViceApi =
        Retrofit.Builder()
            .baseUrl(FRUITYVICE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FruityViceApi::class.java)

    fun createOpenAiApi(apiKey: String): ChatGptApi {
        val authInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(OPENAI_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChatGptApi::class.java)
    }
}
