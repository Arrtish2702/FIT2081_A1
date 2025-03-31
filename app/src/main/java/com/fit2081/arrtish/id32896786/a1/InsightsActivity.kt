package com.fit2081.arrtish.id32896786.a1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit2081.arrtish.id32896786.a1.ui.theme.A1Theme
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext


class InsightsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = intent.getStringExtra("user_id") ?: "default_user"

        setContent {
            A1Theme {
                InsightsScreen(userId)
            }
        }
    }
}

@Composable
fun InsightsScreen(userId: String) {

    val context = LocalContext.current
    val userPrefs = remember { UserSharedPreferences(context, userId) }

    // Attempt to load user choices
    val userChoices by remember {
        mutableStateOf(userPrefs.getUserChoices() ?: emptyMap())
    }

    Log.v("InsightsScreen", "User Choices: $userChoices") // Debugging

    // Categories & default max scores
    val categories = listOf(
        "Vegetables" to 10,
        "Fruits" to 10,
        "Grains & Cereals" to 10,
        "Whole Grains" to 10,
        "Meat & Alternatives" to 10,
        "Dairy" to 10,
        "Water" to 5,
        "Unsaturated Fats" to 10,
        "Sodium" to 10,
        "Sugar" to 10,
        "Alcohol" to 5,
        "Discretionary Foods" to 10
    )

    // Extract scores safely
    val userScores = categories.associate { (category, _) ->
        category to (userChoices[category]?.toString()?.toFloatOrNull() ?: 0f)
    }

    val totalScoreRaw = userScores.values.sum() // Sum of user scores
    val maxScoreRaw = categories.sumOf { it.second.toDouble() }.toFloat() // Convert max scores safely

    val totalScore = if (maxScoreRaw > 0) (totalScoreRaw / maxScoreRaw) * 100 else 0f



    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Insights: Food Score", fontSize = 20.sp, modifier = Modifier.padding(bottom = 16.dp))

            categories.forEach { (name, max) ->
                val score = userScores[name] ?: 0
                FoodScoreItem(name, score)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Total Food Quality Score", fontSize = 16.sp)
            Slider(
                value = totalScore.toFloat(),
                onValueChange = {},
                valueRange = 0f..100.toFloat(),
                enabled = false,
                colors = SliderDefaults.colors(
                    disabledThumbColor = MaterialTheme.colorScheme.primary,
                    disabledActiveTrackColor = MaterialTheme.colorScheme.primary
                )
            )
            Text(text = "$totalScore/100")

            Spacer(modifier = Modifier.height(4.dp))

            Button(onClick = { sharingInsights(context, userScores, totalScore, 100) }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Share with someone")
            }

            Spacer(modifier = Modifier.height(4.dp))
            Button(onClick = {
                Toast.makeText(context,"NutriCoach in Development", Toast.LENGTH_LONG).show()
            }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Improve my diet!")
            }
        }
    }
}

@Composable
fun FoodScoreItem(name: String, score: Number) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = name, modifier = Modifier.weight(1f))

        Spacer(modifier = Modifier.width(2.dp))

        Slider(
            value = score.toFloat(),
            onValueChange = {},
            valueRange = 0f..10f,
            enabled = false,
            modifier = Modifier.weight(1.2f).padding(),
            colors = SliderDefaults.colors(
                disabledThumbColor = MaterialTheme.colorScheme.primary,
                disabledActiveTrackColor = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.width(2.dp))

        Text(text = "${"%.2f".format(score)}/10", modifier = Modifier.padding(start = 8.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun InsightsScreenPreview() {
    A1Theme {
        InsightsScreen(userId = "0")
    }
}

fun sharingInsights(context: Context, userScores: Map<String, Float>, totalScore: Float, maxScore: Int) {
    val shareText = buildString {
        append("🌟 Insights: Food Score 🌟\n")
        userScores.forEach { (category, score) ->
            append("$category: $score/10\n")
        }
        append("\n🏆 Total Food Quality Score: $totalScore/$maxScore")
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }

    val chooser = Intent.createChooser(intent, "Share your insights via:")
    context.startActivity(chooser)
}
