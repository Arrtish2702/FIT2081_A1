package com.fit2081.arrtish.id32896786.a1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.fit2081.arrtish.id32896786.a1.ui.theme.A1Theme
import androidx.compose.material3.Button
import androidx.compose.ui.platform.LocalContext

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            A1Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomePage(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePage(modifier: Modifier = Modifier){
    val context = LocalContext.current
    Button(onClick = {

        val sharedPreferences = context.getSharedPreferences("assignment_1", Context.MODE_PRIVATE)
        val preferenceEditor = sharedPreferences.edit()

        val savedUserId = sharedPreferences.getString("user_id", null)
        val savedPhoneNumber = sharedPreferences.getString("phone_number", null)
        if (savedUserId != null && savedPhoneNumber != null) {
            preferenceEditor.putString("user_id", null)
            preferenceEditor.putString("phone_number", null)
            preferenceEditor.apply()
        }

        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }){
        Text("Log Out Button")
    }
}