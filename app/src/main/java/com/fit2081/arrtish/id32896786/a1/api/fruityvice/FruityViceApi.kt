package com.fit2081.arrtish.id32896786.a1.api.fruityvice

import retrofit2.http.GET
import retrofit2.http.Path

interface FruityViceApi {
    @GET("api/fruit/all")
    suspend fun getAllFruits(): List<Fruit>

    @GET("api/fruit/{name}")
    suspend fun getFruitByName(@Path("name") name: String): Fruit

}

