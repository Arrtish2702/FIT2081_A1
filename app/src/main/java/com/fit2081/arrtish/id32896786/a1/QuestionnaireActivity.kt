package com.fit2081.arrtish.id32896786.a1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.edit
import com.fit2081.arrtish.id32896786.a1.ui.theme.A1Theme

class QuestionnaireActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            A1Theme {
                QuestionnairePage()
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun QuestionnairePage(){
    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ){
        Button(
            onClick = { completeQuestionnaire(context) },
        ) {
            Text(text = "Save Responses")
        }

        Button(onClick = { tempEraseQuestionnaireData(context) }) {
            Text(text = "Clear Response Data")
        }
    }

}

fun completeQuestionnaire(context: Context){
    val sharedPreferences = context.getSharedPreferences("assignment_1", Context.MODE_PRIVATE)
    sharedPreferences.edit{
        putBoolean("answered", true)
    }
    onRouteToHome(context)
}

fun tempEraseQuestionnaireData(context: Context){
    val sharedPreferences = context.getSharedPreferences("assignment_1", Context.MODE_PRIVATE)
    sharedPreferences.edit{
        putBoolean("answered", false)
    }
    Toast.makeText(context,"Data Erased", Toast.LENGTH_LONG).show()
    onRouteToHome(context)
}

fun onRouteToHome(context: Context) {
    val intent = Intent(context, HomeActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(intent)
}