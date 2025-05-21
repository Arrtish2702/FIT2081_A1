package com.fit2081.arrtish.id32896786.a1.internalpages.questionnaire

import com.fit2081.arrtish.id32896786.a1.R

enum class PersonaEnum(val displayName: String, val description: String, val imageResId: Int) {
    HEALTH_DEVOTEE(
        "Health Devotee",
        "You are highly committed to your health and wellness goals.",
        R.drawable.persona_1
    ),
    MINDFUL_EATER(
        "Mindful Eater",
        "You pay close attention to your food choices and eat with awareness.",
        R.drawable.persona_2
    ),
    WELLNESS_STRIVER(
        "Wellness Striver",
        "You make efforts to improve your well-being but seek more guidance.",
        R.drawable.persona_3
    ),
    BALANCE_SEEKER(
        "Balance Seeker",
        "You value a balanced lifestyle and strive for moderation in eating.",
        R.drawable.persona_4
    ),
    HEALTH_PROCRASTINATOR(
        "Health Procrastinator",
        "You want to be healthier but often postpone taking action.",
        R.drawable.persona_5
    ),
    FOOD_CAREFREE(
        "Food Carefree",
        "You enjoy food freely without strict rules or limitations.",
        R.drawable.persona_6
    ),
    UNKNOWN(
        "Unknown",
        "Invalid Persona",
        R.drawable.default_image
    );

    companion object {
        fun fromDisplayName(name: String): PersonaEnum {
            return values().find { it.displayName == name } ?: UNKNOWN
        }
    }
}
