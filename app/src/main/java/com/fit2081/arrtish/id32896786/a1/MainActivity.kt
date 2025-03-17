package com.fit2081.arrtish.id32896786.a1

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.fit2081.arrtish.id32896786.a1.ui.theme.A1Theme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            A1Theme {
                WelcomePage(
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }

    private fun openMonashClinic() {
        val url = "https://www.monash.edu/medicine/scs/nutrition-clinic"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomePage(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Logo
        Image(
            painter = painterResource(id = R.drawable.logo_for_an_app_called_nutritrack),
            contentDescription = "NutriTrack Logo",
            modifier = Modifier.size(150.dp)
        )

        // App Name
        Text(
            text = "NutriTrack",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Disclaimer
        Text(
            text = "Disclaimer: NutriTrack provides general nutritional guidance and does not replace professional medical advice.",
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // External Link to Monash Nutrition Clinic
        Text(
            text = "Visit Monash Nutrition Clinic",
            fontSize = 16.sp,
            color = Color.Blue,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .clickable { openMonashClinic(context) }
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Login Button
        Button(
            onClick = {
                context.startActivity(Intent(context, LoginActivity::class.java))
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Login", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Student Name + ID
        Text(
            text = "Arrtish Suthan (32896786)",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
    }
}

private fun openMonashClinic(context: Context) {
    val url = "https://www.monash.edu/medicine/scs/nutrition-clinic"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}