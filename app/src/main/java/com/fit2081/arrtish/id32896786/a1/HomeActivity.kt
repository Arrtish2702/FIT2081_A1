package com.fit2081.arrtish.id32896786.a1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fit2081.arrtish.id32896786.a1.CsvExports.getUserDetailsAndSave
import com.fit2081.arrtish.id32896786.a1.ui.theme.A1Theme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.json.JSONObject

// Home Activity for the app home page
class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Enable edge-to-edge display (no status bar)

//        // Get user ID passed from the previous activity, default to "default_user" if not available
        val userId = intent.getStringExtra("user_id") ?: "default_user"
//        Log.v("FIT2081-HomeActivity", "User ID: $userId")
//
//        // Get shared preferences specific to the user
//        val sharedPreferences = UserSharedPreferences.getPreferences(this, userId)
//        // Check if the user data is updated in the shared preferences
//        val isUpdated = sharedPreferences.getBoolean("updated", false)
//
//        Log.v("FIT2081-HomeActivity", "SharedPreferences: $sharedPreferences | Updated: $isUpdated")
//
//        // If data is not updated, fetch user details and save it
//        if (!isUpdated) {
//            Log.v("FIT2081-HomeActivity", "Adding data to SharedPreferences for user $userId")
//            getUserDetailsAndSave(this, userId)
//        } else {
//            Log.v("FIT2081-HomeActivity", "Using existing SharedPreferences for user $userId")
//        }
//
//        Log.v("FIT2081-HomeActivity", "SharedPreferences: $sharedPreferences")

        // Set up the UI content for the Home page
        setContent {
//            HideSystemBars() // Hide system bars for a full-screen experience
            A1Theme { // Apply the app's theme
                val navController: NavHostController = rememberNavController()
                Scaffold(
                    topBar = {
                        MyTopAppBar(navController)
                    },
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        MyBottomAppBar(navController)
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier.padding(innerPadding)
                    ){
                        MyNavHost(navController, userId)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(navController: NavHostController) {
    // Track the current route
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route ?: "Home"

    // State to manage the menu's visibility
    var expanded by remember { mutableStateOf(false) }

    // Get context from LocalContext
    val context = LocalContext.current

    TopAppBar(
        title = { Text(currentRoute.replaceFirstChar { it.uppercase() }) }, // Display the route name as title
        actions = {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(Icons.Filled.MoreVert, contentDescription = "More options")
            }

            // Dropdown menu for the log out option
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    onClick = {
                        val intent = Intent(context, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        context.startActivity(intent)
                        expanded = false
                    },
                    text = { Text("Log out") }
                )
            }
        }
    )
}


// MyNavHost Composable function for navigation within the app
@Composable
fun MyNavHost(navController: NavHostController, userId: String?) {
    // NavHost composable to define the navigation graph
    val modifier = Modifier
    NavHost(
        // Use the provided NavHostController
        navController = navController,
        // Set the starting destination to "dice"
        startDestination = "home"
    ) {
        // Define the composable for the "dice" route
        composable("home") {
            HomePage(userId, modifier)
        }

        composable("insights") {
            InsightsPage(userId.toString())
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

@Composable
fun HomePage(userId: String?, modifier: Modifier = Modifier, viewModel: HomeViewModel = viewModel()) {
    val context = LocalContext.current // Get the current context

    // Retrieve the user's insights data (food quality score)
    val detailsJson = ""
//    val userDetails = JSONObject(detailsJson)

    // Extract food quality score from the JSON (default to 0.0 if not found)
//    val foodQualityScore = userDetails.optDouble("qualityScore", 0.0).toFloat()
    val foodQualityScore = 80

    var showDialog by remember { mutableStateOf(false) } // State to control dialog visibility

//    Log.v("FIT2081-HomeActivity", "Food Quality Score: $foodQualityScore")

//    // Check if the user has completed the questionnaire
//    checkForQuestionnaire(context, userId) { hasAnswered ->
//        if (!hasAnswered) {
//            showDialog = true // Show dialog if questionnaire has not been answered
//        }
//    }

//    // Display dialog prompting the user to complete the questionnaire if needed
//    if (showDialog) {
//        AlertDialog(
//            onDismissRequest = { /* Prevent dismiss */ },
//            title = { Text("Complete the Questionnaire") },
//            text = { Text("You need to complete the questionnaire to continue using the app.") },
//            confirmButton = {
//                Button(onClick = {
//                    showDialog = false // Close dialog
//                    onRouteToQuestionnaire(context, userId) // Navigate to questionnaire
//                }) {
//                    Text("Complete Now")
//                }
//            },
//            dismissButton = {
//                Button(onClick = { Authentication.logout(context) }) {
//                    Text(text = "Log Out") // Log out button
//                }
//            }
//        )
//    }

    // Box layout to hold all UI elements
    Box(
        modifier = Modifier
            .fillMaxSize() // Fill the screen
            .padding(16.dp), // Add padding
        contentAlignment = Alignment.TopCenter // Align content to the top center
    ) {
        // Column to arrange elements vertically
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp), // Space between elements
            modifier = Modifier.fillMaxSize()
        ) {
            // Display image based on food quality score
            Image(
                painter = painterResource(
                    id = when {
                        foodQualityScore.toInt() >= 80 -> R.drawable.high_score_picture_removebg_preview
                        foodQualityScore.toInt() >= 40 -> R.drawable.medium_score_picture_removebg_preview
                        foodQualityScore.toInt() >= 0 -> R.drawable.low_score_picture_removebg_preview
                        else -> 0 // Default image if no match
                    }
                ),
                contentDescription = "Food Quality Score", // Image description
                modifier = Modifier
                    .size(275.dp) // Set image size
                    .align(Alignment.CenterHorizontally) // Center align the image
            )

            // Display greeting with the user's ID
            Text(
                text = "Hello, ${userId ?: "Guest"}!", // If userId is null, show "Guest"
                fontSize = 24.sp, // Font size for greeting
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center // Center align the text
            )

            // Display food quality score
            Text(
                text = "Your Food Quality Score: $foodQualityScore",
                fontSize = 20.sp, // Font size for score display
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            // Provide detailed explanation about the food quality score
            Text(
                text = "Your Food Quality Score provides a snapshot of how well your eating patterns align with established food guidelines, helping you identify both strengths and opportunities for improvement in your diet.\n" +
                        "This personalized measurement considers various food groups, including vegetables, fruits, whole grains, and proteins, to give you practical insights for making healthier food choices.\n",
                fontSize = 12.sp, // Font size for explanation
                textAlign = TextAlign.Center
            )

            // Button to edit questionnaire responses
            Button(onClick = {
//                onRouteToQuestionnaire(context, userId) // Navigate to questionnaire
            }) {
                Text(text = "Edit Responses")
            }

//            // Button to view insights
//            Button(onClick = { navController.navigate("insights") }) {
//                Text(text = "View Insights")
//            }

//            // Button to log out
//            Button(onClick = { viewModel.logout(context) }) {
//                Text(text = "Log Out")
//            }
        }
    }
}
//
//// Function to check if the user has answered the questionnaire
//fun checkForQuestionnaire(context: Context, userId: String?, callback: (Boolean) -> Unit) {
//    if (userId == null) {
//        callback(false) // Return false if no user ID is available
//        return
//    }
//
//    // Retrieve shared preferences for the specific user
//    val sharedPreferences = UserSharedPreferences.getPreferences(context, userId)
//    val answeredQuestionnaire = sharedPreferences.getBoolean("answered", false) // Check if questionnaire is answered
//    Log.v("FIT2081-Questionnaire", answeredQuestionnaire.toString())
//    callback(answeredQuestionnaire) // Return the result to the callback
//}
//
//// Function to navigate to the questionnaire activity
//fun onRouteToQuestionnaire(context: Context, userId: String?) {
//    val intent = Intent(context, QuestionnaireActivity::class.java).apply {
//        putExtra("user_id", userId) // Pass user ID to the next activity
//    }
//    context.startActivity(intent) // Start the activity
//}
//
//// Function to navigate to the insights activity
//fun onRouteToInsights(context: Context, userId: String?) {
//    val intent = Intent(context, InsightsActivity::class.java).apply {
//        putExtra("user_id", userId) // Pass user ID to the next activity
//    }
//    context.startActivity(intent) // Start the activity
//}
//
//// Composable function to hide system bars
//@Composable
//internal fun HideSystemBars() {
//    val systemUiController = rememberSystemUiController() // Get system UI controller
//    systemUiController.isSystemBarsVisible = false // Hide the system bars
//}
//
//// Composable function to show system bars
//@Composable
//internal fun ShowSystemBars() {
//    val systemUiController = rememberSystemUiController() // Get system UI controller
//    systemUiController.isSystemBarsVisible = true // Show the system bars
//}
