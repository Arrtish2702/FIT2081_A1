package com.fit2081.arrtish.id32896786.a1

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.fit2081.arrtish.id32896786.a1.ui.theme.A1Theme
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.compose.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.fit2081.arrtish.id32896786.a1.authentication.AuthManager
import com.fit2081.arrtish.id32896786.a1.authentication.login.LoginPage
import com.fit2081.arrtish.id32896786.a1.authentication.RegisterPage
import com.fit2081.arrtish.id32896786.a1.authentication.passwordmanager.ChangePasswordPage
import com.fit2081.arrtish.id32896786.a1.authentication.passwordmanager.ForgotPasswordPage
import com.fit2081.arrtish.id32896786.a1.internalpages.clinician.ClinicianLogin
import com.fit2081.arrtish.id32896786.a1.internalpages.clinician.ClinicianPage
import com.fit2081.arrtish.id32896786.a1.internalpages.home.HomePage
import com.fit2081.arrtish.id32896786.a1.internalpages.insights.InsightsPage
import com.fit2081.arrtish.id32896786.a1.internalpages.nutricoach.NutriCoachPage
import com.fit2081.arrtish.id32896786.a1.internalpages.questionnaire.QuestionnairePage
import com.fit2081.arrtish.id32896786.a1.internalpages.settings.SettingsPage


class MainActivity : ComponentActivity() {

    private val viewModelFactory by lazy {
        AppViewModelFactory(this)  // pass Activity as Context
    }

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        viewModel.loadThemePreference(this)

        AuthManager.loadSession(this)

        viewModel.loadAndInsertData(this)

        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val hideBottomBarRoutes = listOf("welcome", "login", "register", "forgotPassword", "questionnaire")

            A1Theme(darkTheme = viewModel.isDarkTheme.value) {
                Scaffold(
                    bottomBar = {
                        if (currentRoute !in hideBottomBarRoutes) {
                            MyBottomAppBar(navController)
                        }
                    }
                ) { innerPadding ->
                    AppInitialisation(Modifier.padding(innerPadding), navController, isDarkTheme = viewModel.isDarkTheme, viewModel, viewModelFactory)
                }
            }
        }
    }

    companion object {
        val TAG = "FIT2081-A3"
        val PREFS_NAME = "MyPrefs"
    }
}


@Composable
fun AppInitialisation(
    modifier: Modifier,
    navController: NavHostController,
    isDarkTheme: MutableState<Boolean>,
    viewModel: MainViewModel,
    viewModelFactory: AppViewModelFactory
) {
    val userId by AuthManager._userId

    val hasAnsweredQuestionnaire by viewModel.hasAnsweredQuestionnaire.observeAsState(initial = false)
    val questionnaireCheckComplete by viewModel.questionnaireCheckComplete.observeAsState(initial = false)
    var startDestination by remember { mutableStateOf<String?>(null) }

    // Trigger check and only update startDestination after completion
    LaunchedEffect(userId, questionnaireCheckComplete) {
        if (userId != null && userId != -1 && !questionnaireCheckComplete) {
            viewModel.checkIfQuestionnaireAnswered(userId!!)
        } else if (userId == null || userId == -1) {
            startDestination = "welcome"
        } else if (questionnaireCheckComplete) {
            startDestination = if (!hasAnsweredQuestionnaire) "questionnaire" else "home"
        }
    }
    Log.v(MainActivity.TAG, "userID on login: $userId")
    // 🌀 Show a loading indicator until startDestination is ready
    if (startDestination == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        NavHost(navController = navController, startDestination = startDestination!!) {
            composable("welcome") {
                WelcomePage(modifier, navController)
            }
            composable("login") {
                LoginPage(modifier, navController, viewModelFactory)
            }
            composable("register") {
                RegisterPage(modifier, navController, viewModelFactory)
            }
            composable("forgotPassword") {
                ForgotPasswordPage(modifier, navController)
            }
            composable("changePassword") {
                ChangePasswordPage(modifier, navController)
            }
            composable("home") {
                HomePage(userId ?: -1, modifier, navController, viewModelFactory)
            }
            composable("questionnaire") {
                QuestionnairePage(userId ?: -1, navController, viewModelFactory)
            }
            composable("insights") {
                InsightsPage(userId ?: -1, modifier, navController, viewModelFactory)
            }
            composable("nutricoach") {
                NutriCoachPage(userId ?: -1, modifier, viewModelFactory)
            }
            composable("settings") {
                SettingsPage(
                    userId ?: -1,
                    modifier,
                    navController,
                    isDarkTheme = isDarkTheme,
                    viewModelFactory
                )
            }
            composable("clinician login") {
                ClinicianLogin(navController, viewModelFactory)
            }
            composable("clinician") {
                ClinicianPage(userId ?: -1, modifier, navController, viewModelFactory)
            }
        }
    }
}

// Composable function for creating the bottom navigation bar.
@Composable
fun MyBottomAppBar(navController: NavHostController) {
    // State to track the currently selected item in the bottom navigation bar.
    var selectedItem by remember { mutableStateOf(0) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination


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
                selected = currentDestination?.route == item,
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
    val scrollState = rememberScrollState()

    // Column layout to arrange components vertically
    Column(
        modifier = modifier
            .fillMaxSize() // Fill the entire screen
            .verticalScroll(scrollState)
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
            shape = RoundedCornerShape(12.dp)
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
 *
 * DO CHECK RUN OF ALL REQUIREMENTS FOR THE ASSIGNMENT
 *
 * DO DOCUMENT LIST OF HD REQUIREMENTS
 *  - put in pw hashing for user pws - 1
 *  - put in change pw page for users whom logged out and forgot their pw - 0.5
 *      - can add one inside of the app in settings page - 0.5 - MUST DO
 *  - light/dark theme with rmb state - 0.3
 *  - unique password requirements - 0.5
 *  - stylised nutriCoach prompts to encourage/congratulate users with high/low scores - 1
 *  - stylised clinician prompts to give analysis/encourage more users based on the dataset for the insights - 1
 *
**/

/** TO TEST
 *
 * DO SYSTEM WIDE APP TEST
 *
**/

/** DONE
 *
 * FORGOT PASSWORD - NEED TO USE SECURITY QUESTION OR NOT
 * CHANGE PASSWORD - IF THE USER HAS CHANGED PW, ROUTE TO CHANGE PW ON AFTER LOGIN
 *
 * ADD UNIQUE PASSWORD IDENTIFIER CHECKER FOR PASSWD
 *
 * ENHANCE THE PROMPTS OF CLINICIAN AND NUTRICOACH GEN AI
 *
 * UPDATE THE NUTRICOACH GPT QUERY WITH ADDITIONAL PATIENT AND FOODINTAKE DATA
 *
 * DO THE NONSENSE FRUIT SCORE FOR FRUITYVICE
 *
 * FIX ROUTING FOR LOGIN AND QUESTIONNAIRE TO HAVE THE NAV CONTROLLER IN THE UI
 *
 * FIX THE HTTP CONNECTION TO SEND DATASET TO LLM FOR 3 KEY DATA PATTERNS - CLINICIAN PART
 *
 * TEST FRUITYVICEAPI AGAIN
 *
 * FIX UP THE CLINICIAN LOGIN BACKEND IN THE SETTINGS PART.
 *
 * FIX THE INSERT CHECK FOR QUESTIONNAIRE
 *
 * FIX SCREEN ROTATION ISSUES
 *
 * FIX DARKTHEME TO HOLD ON APP DESTROY
 *
 * FIX QUESTIONNAIRE NOT HOLDING STATE FOR SCREEN ROTATE
 *
 * CHANGE SHOW ALL TIPS TO USE A MODAL POP UP FOR STATELIFECYCLE - UI CHANGE
 *
 * HASHED THE PASSWORD FOR THE USERS
 *
 * IMPLEMENTED SAVED NUTRICOACH TIPS INTO A DB SYSTEM PER THE USER
 *
 * FIX UP THE BACKEND FOR THE NUTRICOACH ACTIVITY
 * USE THE VM TO MAKE HTTP CONNECTION TO CHATGPT API - NEED TO TEST WITH PROPER KEY
 * MAKE A SHOW ALL TIPS TO SAVE ALL PREVIOUS TIPS FROM THE LLM
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
 **/