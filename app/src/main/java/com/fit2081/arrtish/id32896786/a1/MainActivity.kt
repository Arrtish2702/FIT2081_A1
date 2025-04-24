package com.fit2081.arrtish.id32896786.a1

import android.content.Context
import android.content.Intent
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit2081.arrtish.id32896786.a1.ui.theme.A1Theme
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.ViewModelProvider
import com.fit2081.arrtish.id32896786.a1.authentication.LoginPage
import com.fit2081.arrtish.id32896786.a1.authentication.RegisterPage


class MainActivity : ComponentActivity() {

    // Create MainViewModel using ViewModelProvider
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Calling the loadAndInsertData method of MainViewModel
        viewModel.loadAndInsertData(this)

        // Enables edge-to-edge display (immersive mode) for better UI experience
        enableEdgeToEdge()

        // Set the content of the screen to the WelcomePage Composable
        setContent {
            A1Theme {
                // Display the WelcomePage composable with modifier to fill the screen
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}


@Composable
fun AppNavigation(modifier: Modifier) {
    val navController = rememberNavController()

    // Define the NavHost with the start destination and routes
    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") { WelcomePage(modifier, navController) }
        composable("login") { LoginPage(modifier, navController) }
        composable("register") { RegisterPage(modifier, navController) }
    }
}

// Preview function for the WelcomePage Composable
//@Preview(showBackground = true)
@Composable
fun WelcomePage(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    // Get the current context to use it for navigation actions
    val context = LocalContext.current

    // Column layout to arrange components vertically
    Column(
        modifier = modifier
            .fillMaxSize() // Fill the entire screen
            .padding(16.dp), // Add padding around the edges
        verticalArrangement = Arrangement.Center, // Center the items vertically
        horizontalAlignment = Alignment.CenterHorizontally // Center the items horizontally
    ) {
        // Display the app logo as an image
        Image(
            painter = painterResource(id = R.drawable.logo_for_an_app_called_nutritrack), // Logo resource
            contentDescription = "NutriTrack Logo", // Description for accessibility
            modifier = Modifier.size(150.dp) // Set the image size to 150dp
        )

        // Display the app name as a large, bold text
        Text(
            text = "NutriTrack",
            fontSize = 28.sp, // Font size of 28sp
            fontWeight = FontWeight.Bold, // Bold font weight
            modifier = Modifier.padding(top = 8.dp) // Add top padding for spacing
        )

        // Spacer to add vertical space between components
        Spacer(modifier = Modifier.height(24.dp))

        // Display a disclaimer text about the app's purpose
        Text(
            text = "This app provides general health and nutrition information for educational purposes only. It is not intended as medical advice, diagnosis, or treatment. Always consult a qualified healthcare professional before making any changes to your diet, exercise, or health regimen. Use this app at your own risk. If you’d like to an Accredited Practicing Dietitian (APD), please visit the Monash Nutrition/Dietetics Clinic (discounted rates for students):",
            fontSize = 14.sp, // Font size of 14sp
            textAlign = TextAlign.Center, // Center-align the text
            modifier = Modifier.padding(horizontal = 16.dp) // Horizontal padding for spacing
        )

        // Spacer to add vertical space between components
        Spacer(modifier = Modifier.height(16.dp))

        // Display a clickable text to open the Monash Nutrition Clinic website
        Text(
            text = "Visit Monash Nutrition Clinic",
            fontSize = 16.sp, // Font size of 16sp
            color = Color.Blue, // Blue color for clickable text
            fontWeight = FontWeight.Medium, // Medium font weight
            modifier = Modifier
                .clickable { openMonashClinic(context) } // Open the clinic website when clicked
                .padding(8.dp) // Add padding around the text
        )

        // Spacer to add vertical space between components
        Spacer(modifier = Modifier.height(32.dp))

        // Display a button to navigate to the LoginActivity
        Button(
            onClick = {
                navController.navigate("login") {
                    popUpTo("welcome") { inclusive = true }
                }  // Start LoginActivity when clicked
            },
            modifier = Modifier
                .fillMaxWidth(0.8f) // Fill 80% of the screen width
                .height(50.dp), // Set the button height to 50dp
            shape = RoundedCornerShape(12.dp) // Rounded corners for the button
        ) {
            // Text inside the button
            Text("Login", fontSize = 18.sp) // Font size of 18sp
        }

        // Spacer to add vertical space between components
        Spacer(modifier = Modifier.height(24.dp))

        // Display the author's name and student ID
        Text(
            text = "Arrtish Suthan (32896786)",
            fontSize = 16.sp, // Font size of 16sp
            fontWeight = FontWeight.Medium, // Medium font weight
            color = Color.Gray // Gray color for the text
        )
    }
}

// Function to open the Monash Nutrition Clinic website
private fun openMonashClinic(context: Context) {
    val url = "https://www.monash.edu/medicine/scs/nutrition/clinics/nutrition" // URL of the clinic
    val intent = Intent(Intent.ACTION_VIEW, url.toUri()) // Create an intent to open the URL in a browser
    context.startActivity(intent) // Start the activity to open the URL
}

/**TODO LIST:
 * FIX UP THE BROKEN QUESTIONNAIRE AND HOOK IT UP (INSERT/RETRIEVAL) FROM THE DB IN IRL TIME
 * FIX THE FOOD INTAKE ENTITY/DAO/REPO/QUESTIONNAIRE VM TO DO THIS
 *
 * FIX UP THE BACKEND FOR THE NUTRICOACH ACTIVITY
 * USE THE VM TO MAKE HTTP CONNECTION TO FRUITYVICE AND CHATGPT API FOR BOTH
 * MAKE A SHOW ALL TIPS TO SAVE ALL PREVIOUS TIPS FROM THE LLM
 *
 * FIX UP THE CLINICIAN LOGIN BACKEND IN THE SETTINGS PART.
 * MAYBE CAN REUSE VM FROM AUTHENTICATION
 *
 * FIX UP THE CLINICIAN PAGE FOR THE AVG SCORE MALE & FEMALE
 * FIX THE HTTP CONNECTION TO SEND DATASET TO LLM FOR 3 KEY DATA PATTERNS
 *
 * FIX LOGIN, SO LOGIN CREDENTIALS ARE RETAINED AFTER USER ONDESTROYS APP
 *
 * GIVE TO DR TAN ON WEDS TO SEE IF MISSING ANYTHING FURTHER
 *
**/