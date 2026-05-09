package com.example.sensixpert

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.activity.compose.BackHandler

import com.example.sensixpert.service.OneSignalHelper
import com.example.sensixpert.service.NotificationHelper
import com.example.sensixpert.ui.screens.*
import com.example.sensixpert.ui.theme.SensixpertTheme
import com.example.sensixpert.viewmodel.AuthState
import com.example.sensixpert.viewmodel.AuthViewModel
import com.example.sensixpert.viewmodel.BoosterViewModel
import com.example.sensixpert.viewmodel.SubscriptionViewModel


enum class AppScreen {
    LOGIN, REGISTER,
    BOOSTER, SENSITIVITY_GUNS, SENSITIVITY_DEVICE,
    PRIVACY_POLICY, TERMS_CONDITIONS,
    SUBSCRIPTION
}

class MainActivity : ComponentActivity() {

    private val boosterViewModel: BoosterViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val subscriptionViewModel: SubscriptionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()



        // Create notification channel
        NotificationHelper.createNotificationChannel(this)

        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1001
            )
        }

        // Handle disconnect intent from notification
        handleIntent(intent)

        setContent {
            SensixpertTheme {
                // ── Internet check ──
                var hasInternet by remember { mutableStateOf(isNetworkAvailable(this@MainActivity)) }

                if (!hasInternet) {
                    NoInternetScreen(
                        onRetry = {
                            hasInternet = isNetworkAvailable(this@MainActivity)
                        }
                    )
                } else {
                    // ── Auth gate ──
                    val authState = authViewModel.authState

                    // Start subscription observer when logged in
                    LaunchedEffect(authState) {
                        if (authState is AuthState.LoggedIn) {
                            authViewModel.getUserId()?.let { uid ->
                                subscriptionViewModel.startObserving(uid)
                            }
                            // Login to OneSignal for push notifications
                            OneSignalHelper.loginUser()
                        }
                    }

                    when (authState) {
                        is AuthState.Loading -> {
                            // Brief loading — show nothing or splash
                        }
                        is AuthState.LoggedOut -> {
                            // Not logged in → Login/Register flow
                            AuthFlow(authViewModel = authViewModel)
                        }
                        is AuthState.LoggedIn -> {
                            // Logged in → Main app
                            MainAppFlow(
                                boosterViewModel = boosterViewModel,
                                authViewModel = authViewModel,
                                subscriptionViewModel = subscriptionViewModel
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == NotificationHelper.ACTION_DISCONNECT) {
            boosterViewModel.disconnectFromNotification(this)
        }
    }

    companion object {
        fun isNetworkAvailable(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = cm.activeNetwork ?: return false
            val capabilities = cm.getNetworkCapabilities(network) ?: return false
            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }
    }
}

// ═══════════════════════════════════════════════════════════
// AUTH FLOW (Login ↔ Register)
// ═══════════════════════════════════════════════════════════
@Composable
private fun AuthFlow(authViewModel: AuthViewModel) {
    var currentScreen by remember { mutableStateOf(AppScreen.LOGIN) }

    // Handle system back button in Auth flow
    BackHandler(enabled = currentScreen == AppScreen.REGISTER) {
        currentScreen = AppScreen.LOGIN
    }

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            slideInHorizontally(tween(250)) { if (targetState == AppScreen.REGISTER) it else -it } +
                    fadeIn(tween(250)) togetherWith
                    slideOutHorizontally(tween(250)) { if (targetState == AppScreen.REGISTER) -it else it } +
                    fadeOut(tween(250))
        },
        label = "auth_nav"
    ) { screen ->
        when (screen) {
            AppScreen.LOGIN -> LoginScreen(
                authViewModel = authViewModel,
                onNavigateToRegister = { currentScreen = AppScreen.REGISTER }
            )
            AppScreen.REGISTER -> RegisterScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = { currentScreen = AppScreen.LOGIN }
            )
            else -> {}
        }
    }
}

// ═══════════════════════════════════════════════════════════
// MAIN APP FLOW (after login)
// ═══════════════════════════════════════════════════════════
@Composable
private fun MainAppFlow(
    boosterViewModel: BoosterViewModel,
    authViewModel: AuthViewModel,
    subscriptionViewModel: SubscriptionViewModel
) {
    var currentScreen by remember { mutableStateOf(AppScreen.BOOSTER) }

    // Handle system back button — go back to Booster from any sub-screen
    BackHandler(enabled = currentScreen != AppScreen.BOOSTER) {
        currentScreen = AppScreen.BOOSTER
    }

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            if (targetState != AppScreen.BOOSTER) {
                slideInHorizontally(tween(250)) { it } + fadeIn(tween(250)) togetherWith
                        slideOutHorizontally(tween(250)) { -it } + fadeOut(tween(250))
            } else {
                slideInHorizontally(tween(250)) { -it } + fadeIn(tween(250)) togetherWith
                        slideOutHorizontally(tween(250)) { it } + fadeOut(tween(250))
            }
        },
        label = "screen_nav"
    ) { screen ->
        when (screen) {
            AppScreen.BOOSTER -> MainBoosterScreen(
                viewModel = boosterViewModel,
                authViewModel = authViewModel,
                subscriptionViewModel = subscriptionViewModel,
                modifier = Modifier.fillMaxSize(),
                onNavigateToSensitivity = {
                    currentScreen = AppScreen.SENSITIVITY_GUNS
                },
                onNavigateToDeviceSensi = {
                    currentScreen = AppScreen.SENSITIVITY_DEVICE
                },
                onNavigateToPrivacyPolicy = {
                    currentScreen = AppScreen.PRIVACY_POLICY
                },
                onNavigateToTerms = {
                    currentScreen = AppScreen.TERMS_CONDITIONS
                },
                onNavigateToSubscription = {
                    currentScreen = AppScreen.SUBSCRIPTION
                },
            )
            AppScreen.SENSITIVITY_GUNS -> SensitivityScreen(
                modifier = Modifier.fillMaxSize(),
                autoDetect = false,
                isSubscribed = subscriptionViewModel.isSubscribed,
                onNavigateToSubscription = {
                    currentScreen = AppScreen.SUBSCRIPTION
                },
                onNavigateBack = {
                    currentScreen = AppScreen.BOOSTER
                }
            )
            AppScreen.SENSITIVITY_DEVICE -> DeviceSensiScreen(
                modifier = Modifier.fillMaxSize(),
                isSubscribed = subscriptionViewModel.isSubscribed,
                onNavigateToSubscription = {
                    currentScreen = AppScreen.SUBSCRIPTION
                },
                onNavigateBack = {
                    currentScreen = AppScreen.BOOSTER
                }
            )
            AppScreen.PRIVACY_POLICY -> PrivacyPolicyScreen(
                modifier = Modifier.fillMaxSize(),
                onNavigateBack = {
                    currentScreen = AppScreen.BOOSTER
                }
            )
            AppScreen.TERMS_CONDITIONS -> TermsConditionsScreen(
                modifier = Modifier.fillMaxSize(),
                onNavigateBack = {
                    currentScreen = AppScreen.BOOSTER
                }
            )
            AppScreen.SUBSCRIPTION -> SubscriptionScreen(
                subscriptionViewModel = subscriptionViewModel,
                userId = authViewModel.getUserId(),
                onNavigateBack = {
                    currentScreen = AppScreen.BOOSTER
                }
            )
            else -> {}
        }
    }
}