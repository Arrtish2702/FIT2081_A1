package com.fit2081.arrtish.id32896786.a1.gpt

data class ChatGptRequest(
    val model: String = "gpt-4.1",
    val messages: List<Message>,
    val temperature: Double = 0.7
)

data class Message(
    val role: String,
    val content: String
)

data class ChatGptResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)
