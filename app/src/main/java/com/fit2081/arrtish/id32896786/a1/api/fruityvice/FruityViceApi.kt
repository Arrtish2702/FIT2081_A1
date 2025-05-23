package com.fit2081.arrtish.id32896786.a1.api.fruityvice

import retrofit2.http.GET
import retrofit2.http.Path

/**
 * FruityViceApi
 * -------------
 * Retrofit interface defining API endpoints for retrieving fruit data
 * from the FruityVice API.
 *
 * Base URL Example: https://www.fruityvice.com/
 */
interface FruityViceApi {

    /**
     * Fetches a list of all fruits available from the FruityVice API.
     *
     * Endpoint: GET /api/fruit/all
     * @return List of Fruit objects
     */
    @GET("api/fruit/all")
    suspend fun getAllFruits(): List<Fruit>

    /**
     * Fetches a single fruit by its name.
     *
     * Endpoint: GET /api/fruit/{name}
     * @param name The name of the fruit to retrieve (e.g., "banana")
     * @return A single Fruit object
     */
    @GET("api/fruit/{name}")
    suspend fun getFruitByName(@Path("name") name: String): Fruit
}


