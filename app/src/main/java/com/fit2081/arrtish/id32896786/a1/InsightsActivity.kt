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
import androidx.compose.ui.platform.LocalContext
import java.io.BufferedReader
import java.io.InputStreamReader
import com.google.accompanist.systemuicontroller.rememberSystemUiController


class InsightsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            A1Theme {
                InsightsScreen()
            }
        }
    }
}

@Preview(showBackground=true)
@Composable
fun InsightsScreen() {

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Insights: Food Score", fontSize = 20.sp, modifier = Modifier.padding(bottom = 16.dp))

            val categories = listOf(
                "Vegetables" to 10,
                "Fruits" to 10,
                "Grains & Cereals" to 10,
                "Whole Grains" to 10,
                "Meat & Alternatives" to 10,
                "Dairy" to 10,
                "Water" to 2,
                "Unsaturated Fats" to 10,
                "Sodium" to 10,
                "Sugar" to 10,
                "Alcohol" to 2,
                "Discretionary Foods" to 8
            )

            categories.forEach { (name, score) ->
                FoodScoreItem(name, score)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Total Food Quality Score", fontSize = 16.sp)
            Slider(
                value = 40f,
                onValueChange = {},
                valueRange = 0f..100f,
                enabled = false,
                colors = SliderDefaults.colors(
                    disabledThumbColor = MaterialTheme.colorScheme.primary,
                    disabledActiveTrackColor = MaterialTheme.colorScheme.primary
                )
            )
            Text(text = "40/100")

            Spacer(modifier = Modifier.height(4.dp))

            Button(onClick = { sharingInsights(context) }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Share with someone")
            }

            Spacer(modifier = Modifier.height(4.dp))
            Button(onClick = { /* Navigate to NutriCoach in future */ }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Improve my diet!")
            }
        }

    }
}

@Composable
fun FoodScoreItem(name: String, score: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = name, modifier = Modifier.weight(1f))
        Slider(
            value = score.toFloat(),
            onValueChange = {},
            valueRange = 0f..10f,
            enabled = false,
            modifier = Modifier.weight(1.5f).padding(),
            colors = SliderDefaults.colors(
                disabledThumbColor = MaterialTheme.colorScheme.primary,
                disabledActiveTrackColor = MaterialTheme.colorScheme.primary
            )
        )
        Text(text = "$score/10", modifier = Modifier.padding(start = 8.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun InsightsScreenPreview() {
    A1Theme {
        InsightsScreen()
    }
}


fun sharingInsights(context: Context) {
    val shareText = """
        🌟 Insights: Food Score 🌟
        Vegetables: 10/10
        Fruits: 10/10
        Grains & Cereals: 10/10
        Whole Grains: 10/10
        Meat & Alternatives: 10/10
        Dairy: 10/10
        Water: 2/5
        Unsaturated Fats: 10/10
        Sodium: 10/10
        Sugar: 10/10
        Alcohol: 2/5
        Discretionary Foods: 8/10
        
        🏆 Total Food Quality Score: 40/100
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }

    // Show the chooser to let the user pick an app to share
    val chooser = Intent.createChooser(intent, "Share your insights via:")
    context.startActivity(chooser)
}

fun getUserScores(context: Context, userId: Int): Map<String, Int>? {
    try {
        val inputStream = context.assets.open("nutritrack_data.csv") // Load from assets
        val reader = InputStreamReader(inputStream)
        val rows = csvReader().readAllWithHeader(reader)
        inputStream.close() // Close to prevent memory leaks

        // Find user row
        val userRow = rows.find { it["User_ID"]?.toIntOrNull() == userId } ?: return null
        val isMale = userRow["Sex"] == "Male"

        // Map category names to the correct column based on gender
        return mapOf(
            "Vegetables" to (userRow[if (isMale) "VegetablesHEIFAscoreMale" else "VegetablesHEIFAscoreFemale"]?.toIntOrNull() ?: 0),
            "Fruits" to (userRow[if (isMale) "FruitsHEIFAscoreMale" else "FruitsHEIFAscoreFemale"]?.toIntOrNull() ?: 0),
            "Grains & Cereals" to (userRow[if (isMale) "GrainsHEIFAscoreMale" else "GrainsHEIFAscoreFemale"]?.toIntOrNull() ?: 0),
            "Whole Grains" to (userRow[if (isMale) "WholeGrainsHEIFAscoreMale" else "WholeGrainsHEIFAscoreFemale"]?.toIntOrNull() ?: 0),
            "Meat & Alternatives" to (userRow[if (isMale) "MeatHEIFAscoreMale" else "MeatHEIFAscoreFemale"]?.toIntOrNull() ?: 0),
            "Dairy" to (userRow[if (isMale) "DairyHEIFAscoreMale" else "DairyHEIFAscoreFemale"]?.toIntOrNull() ?: 0),
            "Water" to (userRow["WaterHEIFAscore"]?.toIntOrNull() ?: 0),
            "Unsaturated Fats" to (userRow[if (isMale) "UnsaturatedFatHEIFAscoreMale" else "UnsaturatedFatHEIFAscoreFemale"]?.toIntOrNull() ?: 0),
            "Sodium" to (userRow["SodiumHEIFAscore"]?.toIntOrNull() ?: 0),
            "Sugar" to (userRow[if (isMale) "SugarHEIFAscoreMale" else "SugarHEIFAscoreFemale"]?.toIntOrNull() ?: 0),
            "Alcohol" to (userRow["AlcoholHEIFAscore"]?.toIntOrNull() ?: 0),
            "Discretionary Foods" to (userRow[if (isMale) "DiscretionaryHEIFAscoreMale" else "DiscretionaryHEIFAscoreFemale"]?.toIntOrNull() ?: 0)
        )
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}