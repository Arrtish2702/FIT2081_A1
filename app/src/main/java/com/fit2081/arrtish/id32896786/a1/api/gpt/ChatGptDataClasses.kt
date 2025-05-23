package com.fit2081.arrtish.id32896786.a1.api.gpt

/**
 * Data class representing the request payload sent to the ChatGPT API.
 *
 * @param model The model ID (e.g., "gpt-4.1").
 * @param messages A list of messages used to generate the chat completion.
 * @param temperature Sampling temperature to control randomness (0.0 to 1.0).
 */
data class ChatGptRequest(
    val model: String = "gpt-4.1",           // Model used for generating the response
    val messages: List<Message>,            // Conversation history
    val temperature: Double = 0.7           // Controls the creativity of the output
)

/**
 * Data class representing a single message in the conversation.
 *
 * @param role The role of the message sender (e.g., "user", "assistant", "system").
 * @param content The textual content of the message.
 */
data class Message(
    val role: String,                       // "user", "assistant", or "system"
    val content: String                     // The actual message text
)

/**
 * Data class representing the full response from the ChatGPT API.
 *
 * @param choices A list of generated response choices from the API.
 */
data class ChatGptResponse(
    val choices: List<Choice>               // List of generated responses
)

/**
 * Represents a single choice returned by the ChatGPT API.
 *
 * @param message The assistant's message content.
 */
data class Choice(
    val message: Message                    // The assistant's response message
)
