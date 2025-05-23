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
import com.fit2081.arrtish.id32896786.a1.authentication.LoginPage
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


/**
 * MainActivity
 *
 * The entry point of the app.
 * Sets up the theme, navigation controller, and view models.
 * Initializes user session and data loading.
 */
class MainActivity : ComponentActivity() {

    // Lazy initialization of AppViewModelFactory using the activity context
    private val viewModelFactory by lazy {
        AppViewModelFactory(this)
    }

    // Lazy initialization of MainViewModel with the factory
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
    }

    /**
     * Called when the activity is first created.
     * Sets up the UI content with Compose, enables edge-to-edge, and starts data/session loading.
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // Load theme preference (dark or light) from saved settings
        viewModel.loadThemePreference(this)

        // Load user session information (authentication state)
        AuthManager.loadSession(this)

        // Load and insert data into the database asynchronously
        viewModel.loadAndInsertData(this)

        // Enable edge-to-edge drawing (content under status/navigation bars)
        enableEdgeToEdge()

        // Set Compose content
        setContent {
            // Navigation controller to manage navigation stack
            val navController = rememberNavController()
            // Observe navigation back stack for current route
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            // Define routes where bottom bar should be hidden
            val hideBottomBarRoutes = listOf("welcome", "login", "register", "forgotPassword", "changePassword", "questionnaire")

            // App theme wrapper using user preference (dark or light)
            A1Theme(darkTheme = viewModel.isDarkTheme.value) {
                // Scaffold with conditional bottom bar display
                Scaffold(
                    bottomBar = {
                        if (currentRoute !in hideBottomBarRoutes) {
                            MyBottomAppBar(navController)
                        }
                    }
                ) { innerPadding ->
                    // Main app initialization composable with padding
                    AppInitialisation(
                        Modifier.padding(innerPadding),
                        navController,
                        isDarkTheme = viewModel.isDarkTheme,
                        viewModel,
                        viewModelFactory
                    )
                }
            }
        }
    }

    companion object {
        // Log tag used throughout the app
        val TAG = "FIT2081-A3"
        // Shared preferences name for persistent data
        val PREFS_NAME = "MyPrefs"
    }
}


/**
 * AppInitialisation Composable
 *
 * Determines the start destination of the app based on:
 * - User login status
 * - Whether user has completed the questionnaire
 *
 * Displays a loading indicator while waiting, then sets up NavHost for app navigation.
 */
@Composable
fun AppInitialisation(
    modifier: Modifier,
    navController: NavHostController,
    isDarkTheme: MutableState<Boolean>,
    viewModel: MainViewModel,
    viewModelFactory: AppViewModelFactory
) {
    // Observe userId from AuthManager to track login status
    val userId by AuthManager._userId

    // Observe whether the questionnaire has been answered by user
    val hasAnsweredQuestionnaire by viewModel.hasAnsweredQuestionnaire.observeAsState(initial = false)
    // Observe if the questionnaire check process is complete
    val questionnaireCheckComplete by viewModel.questionnaireCheckComplete.observeAsState(initial = false)

    // Mutable state to hold start destination route once determined
    var startDestination by remember { mutableStateOf<String?>(null) }

    // Side-effect: Update startDestination whenever relevant state changes
    LaunchedEffect(userId, questionnaireCheckComplete, hasAnsweredQuestionnaire) {
        when {
            // No logged in user found, direct to welcome screen
            userId == null || userId == -1 -> {
                startDestination = "welcome"
            }
            // Questionnaire completion check in progress, trigger view model to check
            !questionnaireCheckComplete -> {
                viewModel.checkIfQuestionnaireAnswered(userId!!)
            }
            // Questionnaire check complete: route to home or questionnaire based on response
            questionnaireCheckComplete -> {
                startDestination = if (hasAnsweredQuestionnaire) "home" else "questionnaire"
            }
        }
    }

    // Log userId for debugging
    Log.v(MainActivity.TAG, "userID on login: $userId")

    if (startDestination == null) {
        // Show loading spinner while determining start destination
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        // Set up navigation host with the resolved start destination
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


/**
 * MyBottomAppBar Composable
 *
 * Bottom navigation bar displayed in main app screens (excluding some routes).
 * Provides navigation to Home, Insights, NutriCoach, and Settings pages.
 */
@Composable
fun MyBottomAppBar(navController: NavHostController) {
    // Tracks currently selected tab index
    var selectedItem by remember { mutableStateOf(0) }

    // Get current navigation destination for highlighting selected tab
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // List of bottom bar navigation routes
    val items = listOf(
        "home",
        "insights",
        "nutricoach",
        "settings"
    )

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    // Icon changes depending on route
                    when (item) {
                        "home" -> Icon(Icons.Filled.Home, contentDescription = "Home")
                        "insights" -> Icon(Icons.Filled.Person, contentDescription = "Insights")
                        "nutricoach" -> Icon(Icons.Filled.Info, contentDescription = "NutriCoach")
                        "settings" -> Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                },
                label = { Text(item) },
                selected = currentDestination?.route == item,
                onClick = {
                    // Update selected index and navigate to chosen route
                    selectedItem = index
                    navController.navigate(item)
                }
            )
        }
    }
}

/**
 * WelcomePage Composable
 *
 * The welcome screen shown to new or unauthenticated users.
 * Shows logo, info, disclaimer, links to external nutrition clinic, and buttons to Login/Register.
 */
@Composable
fun WelcomePage(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    // Get context to launch external links
    val context = LocalContext.current
    // Scroll state for vertical scrolling
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App logo image
        Image(
            painter = painterResource(id = R.drawable.logo_for_an_app_called_nutritrack),
            contentDescription = "NutriTrack Logo",
            modifier = Modifier.size(150.dp)
        )

        // App name text
        Text(
            text = "NutriTrack",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Disclaimer text about the app's educational purpose
        Text(
            text = "This app provides general health and nutrition information for educational purposes only. It is not intended as medical advice, diagnosis, or treatment. Always consult a qualified healthcare professional before making any changes to your diet, exercise, or health regimen. Use this app at your own risk. If you’d like to an Accredited Practicing Dietitian (APD), please visit the Monash Nutrition/Dietetics Clinic (discounted rates for students):",
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        // External link to Monash Nutrition Clinic website, opens browser when clicked
        Text(
            text = "https://www.monash.edu/medicine/medicine-and-radiology/departments/clinical-sciences/monash-nutrition-and-dietetics-clinic",
            color = Color.Blue,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable {
                // Open the URL in browser
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = "https://www.monash.edu/medicine/medicine-and-radiology/departments/clinical-sciences/monash-nutrition-and-dietetics-clinic".toUri()
                }
                context.startActivity(intent)
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Button row for Login and Register navigation
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = { navController.navigate("login") }) {
                Text("Login")
            }
            Button(onClick = { navController.navigate("register") }) {
                Text("Register")
            }
        }
    }
}
