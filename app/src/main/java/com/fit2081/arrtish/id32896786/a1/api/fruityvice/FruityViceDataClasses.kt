package com.fit2081.arrtish.id32896786.a1.api.fruityvice
/**
 * Disclaimer:
 * This file may include comments or documentation assisted by OpenAI's GPT model.
 * All code logic and architectural decisions were implemented and verified by the developer.
 */

/**
 * Data class representing a Fruit as returned by the FruityVice API.
 *
 * @property genus The genus of the fruit (e.g., "Musa")
 * @property name The common name of the fruit (e.g., "Banana")
 * @property id Unique ID for the fruit in the API
 * @property family The botanical family (e.g., "Musaceae")
 * @property order The botanical order (e.g., "Zingiberales")
 * @property nutritions Nutritional details of the fruit
 */
data class Fruit(
    val genus: String,
    val name: String,
    val id: Int,
    val family: String,
    val order: String,
    val nutritions: Nutritions
)

/**
 * Data class representing the nutritional information of a fruit.
 *
 * All values are typically given per 100g serving.
 *
 * @property carbohydrates Total carbohydrates in grams
 * @property protein Total protein in grams
 * @property fat Total fat in grams
 * @property calories Energy value in kilocalories
 * @property sugar Total sugars in grams
 */
data class Nutritions(
    val carbohydrates: Double,
    val protein: Double,
    val fat: Double,
    val calories: Int,
    val sugar: Double
)
