package com.fit2081.arrtish.id32896786.a1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.edit
import com.fit2081.arrtish.id32896786.a1.ui.theme.A1Theme
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.compose.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.fit2081.arrtish.id32896786.a1.authentication.AuthManager
import com.fit2081.arrtish.id32896786.a1.authentication.AuthManager.getStudentId
import com.fit2081.arrtish.id32896786.a1.authentication.ChangePasswordPage
import com.fit2081.arrtish.id32896786.a1.authentication.LoginPage
import com.fit2081.arrtish.id32896786.a1.authentication.RegisterPage
import com.fit2081.arrtish.id32896786.a1.clinician.ClinicianLogin
import com.fit2081.arrtish.id32896786.a1.clinician.ClinicianPage
import com.fit2081.arrtish.id32896786.a1.home.HomePage
import com.fit2081.arrtish.id32896786.a1.insights.InsightsPage
import com.fit2081.arrtish.id32896786.a1.nutricoach.NutriCoachPage
import com.fit2081.arrtish.id32896786.a1.questionnaire.QuestionnairePage
import com.fit2081.arrtish.id32896786.a1.settings.SettingsPage


class MainActivity : ComponentActivity() {

    // Create MainViewModel using ViewModelProvider
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        AuthManager.loadSession(this)

        viewModel.loadAndInsertData(this)

        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val hideBottomBarRoutes = listOf("welcome", "login", "register", "changePassword")

            A1Theme {
                Scaffold(
                    bottomBar = {
                        if (currentRoute !in hideBottomBarRoutes) {
                            MyBottomAppBar(navController)
                        }
                    }
                ) { innerPadding ->
                    AppInitialisation(Modifier.padding(innerPadding), navController)
                }
            }
        }
    }

    companion object {
        val TAG = "FIT2081-A3"
    }
}


@Composable
fun AppInitialisation(modifier: Modifier, navController: NavHostController) {
    val context = LocalContext.current
    val userId by AuthManager._userId

    Log.v(MainActivity.TAG, "userID on login: $userId")

    val startDestination = if (userId != null && userId != -1) {
        "home"
    } else {
        "welcome"
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("welcome") {
            WelcomePage(modifier, navController)
        }
        composable("login") {
            LoginPage(modifier, navController)
        }
        composable("register") {
            RegisterPage(modifier, navController)
        }
        composable("changePassword") {
            ChangePasswordPage(modifier, navController)
        }
        composable("home") {
            HomePage(userId ?: -1, modifier, navController)
        }
        composable("questionnaire") {
            QuestionnairePage(userId ?: -1, navController)
        }
        composable("insights") {
            InsightsPage(userId ?: -1, modifier, navController)
        }
        composable("nutricoach") {
            NutriCoachPage(userId ?: -1, modifier)
        }
        composable("settings") {
            SettingsPage(userId ?: -1, modifier, navController)
        }
        composable("clinician login") {
            ClinicianLogin(navController)
        }
        composable("clinician") {
            ClinicianPage(userId ?: -1, modifier, navController)
        }
    }
}

// Composable function for creating the bottom navigation bar.
@Composable
fun MyBottomAppBar(navController: NavHostController) {
    // State to track the currently selected item in the bottom navigation bar.
    var selectedItem by remember { mutableStateOf(0) }

    // List of navigation items: "home", "reports", "settings".
    val items = listOf(
        "home",
        "insights",
        "nutricoach",
        "settings"
    )

    // NavigationBar composable to define the bottom navigation bar.
    NavigationBar {
        // Iterate through each item in the 'items' list along with its index.
        items.forEachIndexed { index, item ->
            // NavigationBarItem for each item in the list.
            NavigationBarItem(
                // Define the icon based on the item's name.
                icon = {
                    when (item) {
                        // If the item is "home", show the Home icon.
                        "home" -> Icon(Icons.Filled.Home, contentDescription = "Home")

                        "insights" -> Icon(Icons.Filled.Person, contentDescription = "Insights")

                        "nutricoach" -> Icon(Icons.Filled.Info, contentDescription = "NutriCoach")

                        "settings" -> Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                },
                // Display the item's name as the label.
                label = { Text(item) },
                // Determine if this item is currently selected.
                selected = selectedItem == index,
                // Actions to perform when this item is clicked.
                onClick = {
                    // Update the selectedItem state to the current index.
                    selectedItem = index
                    // Navigate to the corresponding screen based on the item's name.
                    navController.navigate(item)
                }
            )
        }
    }
}

// Preview function for the WelcomePage Composable
//@Preview(showBackground = true)
@Composable
fun WelcomePage(
    modifier: Modifier = Modifier,
    navController: NavController,
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

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate("register") {
                    popUpTo("welcome") { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.8f) // Fill 80% of the screen width
                .height(50.dp), // Set the button height to 50dp
        ) {
            Text("New to this app? Register here")
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
 *
 * FIX UP THE BACKEND FOR THE NUTRICOACH ACTIVITY
 * USE THE VM TO MAKE HTTP CONNECTION TO CHATGPT API - NEED TO TEST WITH PROPER KEY
 * MAKE A SHOW ALL TIPS TO SAVE ALL PREVIOUS TIPS FROM THE LLM
 *
 * FIX THE HTTP CONNECTION TO SEND DATASET TO LLM FOR 3 KEY DATA PATTERNS - CLINICIAN PART
 *
 * GIVE TO DR TAN ON WEDS TO SEE IF MISSING ANYTHING FURTHER - this friday
 *
**/

/** TO TEST
 *
 * FIX UP THE CLINICIAN LOGIN BACKEND IN THE SETTINGS PART.
 *
**/

/** DONE
 *
 * FIX QUESTIONNAIRE TO USE CORRECT TIMESTAMP
 *
 * FIX UP THE BROKEN QUESTIONNAIRE AND HOOK IT UP (INSERT/RETRIEVAL) FROM THE DB IN IRL TIME
 * FIX THE FOOD INTAKE ENTITY/DAO/REPO/QUESTIONNAIRE VM TO DO THIS
 *
 * USE THE VM TO MAKE HTTP CONNECTION TO FRUITYVICE
 *
 * REMOVE THE REMAINING LAUNCHED EFFECTS
 *
 * FIX LOGIN, SO LOGIN CREDENTIALS ARE RETAINED AFTER USER ONDESTROYS APP
 *
 * FIX DB/REPO/DAO TO USE LIVEDATA INSTEAD OF FLOW
 *
 * FIX UP THE CLINICIAN PAGE FOR THE AVG SCORE MALE & FEMALE
 *
 *
 **/