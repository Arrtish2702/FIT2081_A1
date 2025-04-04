package com.fit2081.arrtish.id32896786.a1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit2081.arrtish.id32896786.a1.ui.theme.A1Theme

class InsightsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = intent.getStringExtra("user_id") ?: "default_user"
        Log.v("FIT2081-InsightsScreen", userId)

        setContent {
            ShowSystemBars()
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

    var userInsights = userPrefs.getInsights()
    
    val categories = listOf(
        "Vegetables" to 10, "Fruits" to 10, "Grains & Cereals" to 10, "Whole Grains" to 10,
        "Meat & Alternatives" to 10, "Dairy" to 10, "Water" to 5, "Unsaturated Fats" to 10,
        "Sodium" to 10, "Sugar" to 10, "Alcohol" to 5, "Discretionary Foods" to 10
    )

    val userScores = categories.associate { (category, _) ->
        category to (userInsights[category]?.toString()?.toFloatOrNull() ?: 0f)
    }

    val totalScoreRaw = userScores.values.sum()
    val maxScoreRaw = categories.sumOf { it.second.toDouble() }.toFloat()
    val totalScore = if (maxScoreRaw > 0) (totalScoreRaw / maxScoreRaw) * 100 else 0f

    InsightsContent(userScores, totalScore, context)
}


@Composable
fun InsightsContent(userScores: Map<String, Float>, totalScore: Float, context: Context) {
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

            userScores.forEach { (name, score) ->
                FoodScoreItem(name, score)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Total Food Quality Score", fontSize = 16.sp)
            Slider(
                value = totalScore,
                onValueChange = {},
                valueRange = 0f..100f,
                enabled = false,
                colors = SliderDefaults.colors(
                    disabledThumbColor = MaterialTheme.colorScheme.primary,
                    disabledActiveTrackColor = MaterialTheme.colorScheme.primary
                )
            )
            Text(text = "%.2f/100".format(totalScore))

            Spacer(modifier = Modifier.height(4.dp))

            Button(onClick = { sharingInsights(context, userScores, totalScore, 100) }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Share with someone")
            }

            Spacer(modifier = Modifier.height(4.dp))
            Button(onClick = {
                Toast.makeText(context, "NutriCoach in Development", Toast.LENGTH_LONG).show()
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
            modifier = Modifier
                .weight(1.2f)
                .padding(),
            colors = SliderDefaults.colors(
                disabledThumbColor = MaterialTheme.colorScheme.primary,
                disabledActiveTrackColor = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.width(2.dp))

        Text(text = "%.2f/10".format(score), modifier = Modifier.padding(start = 8.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun InsightsScreenPreview() {
    A1Theme {
        InsightsContent(userScores = emptyMap(), totalScore = 0f, context = LocalContext.current)
    }
}

fun sharingInsights(context: Context, userScores: Map<String, Float>, totalScore: Float, maxScore: Int) {
    val shareText = buildString {
        append("🌟 Insights: Food Score 🌟\n")
        userScores.forEach { (category, score) ->
            append("$category: %.2f/10\n".format(score))
        }
        append("\n🏆 Total Food Quality Score: %.2f/$maxScore".format(totalScore))
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }

    val chooser = Intent.createChooser(intent, "Share your insights via:")
    context.startActivity(chooser)
}
