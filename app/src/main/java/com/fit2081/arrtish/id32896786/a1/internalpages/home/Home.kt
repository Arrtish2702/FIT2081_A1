package com.fit2081.arrtish.id32896786.a1.internalpages.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fit2081.arrtish.id32896786.a1.AppViewModelFactory
import com.fit2081.arrtish.id32896786.a1.R


@Composable
fun HomePage(userId: Int,
     modifier: Modifier = Modifier,
     navController: NavController,
     viewModelFactory: AppViewModelFactory
) {
    val viewModel: HomeViewModel = viewModel(factory = viewModelFactory)
    val patient by viewModel.patient.observeAsState()
    val scrollState = rememberScrollState()

    viewModel.loadPatientDataById(userId)

    patient?.let { patientData ->
        val foodQualityScore = patientData.totalScore
        val name = patientData.patientName

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
                .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            navController.navigate("questionnaire")
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Questionnaire")
                    }
                }
                Image(
                    painter = painterResource(
                        id = when {
                            foodQualityScore.toInt() >= 80 -> R.drawable.high_score_picture_removebg_preview
                            foodQualityScore.toInt() >= 40 -> R.drawable.medium_score_picture_removebg_preview
                            foodQualityScore.toInt() >= 0 -> R.drawable.low_score_picture_removebg_preview
                            else -> 0
                        }
                    ),
                    contentDescription = "Food Quality Score",
                    modifier = Modifier
                        .size(275.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Text(
                    text = "Hello, ${name}!",
                    fontSize = 24.sp,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Your Food Quality Score: $foodQualityScore",
                    fontSize = 20.sp,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Your Food Quality Score provides a snapshot of how well your eating patterns align with established food guidelines, helping you identify both strengths and opportunities for improvement in your diet.\n" +
                            "This personalized measurement considers various food groups, including vegetables, fruits, whole grains, and proteins, to give you practical insights for making healthier food choices.\n",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )

                Button(onClick = {
                    navController.navigate("insights")
                },
                shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Show all Scores")
                }
            }
        }
    }
}