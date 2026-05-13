package com.hastakala.shop.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hastakala.shop.ui.components.BottomNav
import com.hastakala.shop.ui.components.TopBar
import com.hastakala.shop.ui.navigation.Routes
import com.hastakala.shop.ui.screens.*
import com.hastakala.shop.ui.theme.HastaKalaTheme
import com.hastakala.shop.util.UserPrefs
import com.hastakala.shop.viewmodel.MainViewModel

@Composable
fun HastaKalaApp() {
    val ctx = LocalContext.current
    var isDark by remember { mutableStateOf(UserPrefs.isDarkMode(ctx)) }
    
    val nav = rememberNavController()
    val vm: MainViewModel = viewModel()
    val backstack by nav.currentBackStackEntryAsState()
    val current = backstack?.destination?.route ?: Routes.LOGIN
    val authRoutes = listOf(Routes.LOGIN, Routes.REGISTER, Routes.FORGOT_PASSWORD)
    val showChrome = current !in authRoutes

    val start = if (UserPrefs.isLoggedIn(ctx)) Routes.DASHBOARD else Routes.LOGIN

    HastaKalaTheme(darkTheme = isDark) {
        Scaffold(
            topBar = {
                if (showChrome) {
                    TopBar(
                        isDark = isDark,
                        onToggleTheme = {
                            isDark = !isDark
                            UserPrefs.setDarkMode(ctx, isDark)
                        },
                        onLogout = {
                            UserPrefs.logout(ctx)
                            nav.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                        }
                    )
                }
            },
            bottomBar = {
                if (showChrome) {
                    BottomNav(current) { route ->
                        if (route != current) nav.navigate(route) {
                            popUpTo(Routes.DASHBOARD) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            },
        ) { padding ->
            NavHost(
                navController = nav,
                startDestination = start,
                modifier = Modifier.padding(padding),
                enterTransition = { fadeIn(tween(220)) + slideInHorizontally(tween(260)) { it / 8 } },
                exitTransition = { fadeOut(tween(180)) },
                popEnterTransition = { fadeIn(tween(220)) },
                popExitTransition = { fadeOut(tween(180)) },
            ) {
                composable(Routes.LOGIN) {
                    LoginScreen(
                        onSuccess = {
                            nav.navigate(Routes.DASHBOARD) { popUpTo(Routes.LOGIN) { inclusive = true } }
                        },
                        onRegisterClick = { nav.navigate(Routes.REGISTER) },
                        onForgotPasswordClick = { nav.navigate(Routes.FORGOT_PASSWORD) }
                    )
                }
                composable(Routes.REGISTER) {
                    RegisterScreen(
                        onBack = { nav.popBackStack() },
                        onRegisterSuccess = {
                            nav.navigate(Routes.DASHBOARD) { popUpTo(Routes.LOGIN) { inclusive = true } }
                        }
                    )
                }
                composable(Routes.FORGOT_PASSWORD) {
                    ForgotPasswordScreen(onBack = { nav.popBackStack() })
                }
                composable(Routes.DASHBOARD) {
                    DashboardScreen(vm,
                        onAddSale = { nav.navigate(Routes.ADD_SALE) },
                        onAnalytics = { nav.navigate(Routes.ANALYTICS) },
                        onHistory = { nav.navigate(Routes.HISTORY) })
                }
                composable(Routes.ADD_SALE) { QuickBillScreen(vm) }
                composable(Routes.HISTORY) { HistoryScreen(vm) }
                composable(Routes.ANALYTICS) { AnalyticsScreen(vm) }
                composable(Routes.STOCK) { StockScreen(vm) }
            }
        }
    }
}
