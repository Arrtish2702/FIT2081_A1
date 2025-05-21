package com.fit2081.arrtish.id32896786.a1.api.gpt


import retrofit2.http.Body
import retrofit2.http.POST

interface ChatGptApi {
    @POST("v1/chat/completions")
    suspend fun getChatResponse(@Body request: ChatGptRequest): ChatGptResponse
}
