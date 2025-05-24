package com.fit2081.arrtish.id32896786.a1.api.gpt
/**
 * Disclaimer:
 * This file may include comments or documentation assisted by OpenAI's GPT model.
 * All code logic and architectural decisions were implemented and verified by the developer.
 */
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Retrofit interface for interacting with the OpenAI ChatGPT API.
 */
interface ChatGptApi {
    @POST("v1/chat/completions")
    suspend fun getChatResponse(
        @Header("Authorization") authorization: String,
        @Body request: ChatGptRequest
    ): ChatGptResponse
}
