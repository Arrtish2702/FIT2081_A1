package com.fit2081.arrtish.id32896786.a1.nutricoach

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fit2081.arrtish.id32896786.a1.AppViewModelFactory
import com.fit2081.arrtish.id32896786.a1.MainActivity
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog

@Composable
fun NutriCoachPage(
    userId: Int,
    modifier: Modifier = Modifier,
    viewModel: NutriCoachViewModel = viewModel(
       factory = AppViewModelFactory(LocalContext.current)
    )
) {
    var fruitName by remember { mutableStateOf("") }
    val motivationalMessage by viewModel.motivationalMessage.observeAsState("")
    val fruitDetails by viewModel.fruitDetails.observeAsState(emptyMap())
    val errorMessage by viewModel.errorMessage.observeAsState()
    val tipsList by viewModel.tipsList.observeAsState(emptyList())

    var showTipsDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
            .fillMaxHeight()
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)

        ) {
            Text("NutriCoach", fontSize = 24.sp, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = fruitName,
                onValueChange = { fruitName = it },
                label = { Text("Fruit Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    viewModel.fetchFruit(fruitName)
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Details")
            }

            Spacer(Modifier.height(16.dp))

            // Show error if any
            errorMessage?.let { err ->
                Text(
                    text = err,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
            }

            // Render the details
            if (fruitDetails.isNotEmpty()) {
                fruitDetails.forEach { (label, value) ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text("$label :", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        Text(value, modifier = Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(4.dp))
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.generateMotivationalMessage(userId)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Star, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Motivational Message (AI)")
            }

            Spacer(Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Text(
                    motivationalMessage,
                    modifier = Modifier.padding(12.dp),
                    fontStyle = FontStyle.Italic
                )
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    viewModel.loadAllTips(userId)
                    showTipsDialog = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Show All Tips")
            }


            if (showTipsDialog) {
                Dialog(onDismissRequest = { showTipsDialog = false }) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        tonalElevation = 4.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                                .heightIn(min = 100.dp, max = 500.dp)
                        ) {
                            Text("Saved Tips", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Spacer(Modifier.height(8.dp))

                            if (tipsList.isNotEmpty()) {
                                LazyColumn(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    items(items = tipsList, key = { it.tipsId }) { tip ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            elevation = CardDefaults.cardElevation(2.dp)
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Text(
                                                    text = tip.responseString,
                                                    fontStyle = FontStyle.Italic
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "Saved on: ${tip.responseTimeStamp}",
                                                    fontSize = 12.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                Text("No tips available.")
                            }

                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = { showTipsDialog = false },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Close")
                            }
                        }
                    }
                }
            }
        }
    }
}


