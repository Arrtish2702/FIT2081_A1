package com.fit2081.arrtish.id32896786.a1.nutricoach

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://www.fruityvice.com/#3"  // Base path :contentReference[oaicite:8]{index=8}

    val api: FruityViceApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)                                      // Set base URL :contentReference[oaicite:9]{index=9}
            .addConverterFactory(GsonConverterFactory.create())      // JSON converter :contentReference[oaicite:10]{index=10}
            .build()
            .create(FruityViceApi::class.java)
    }
}
