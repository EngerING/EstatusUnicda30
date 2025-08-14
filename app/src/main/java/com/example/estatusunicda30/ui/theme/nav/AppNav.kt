package com.example.estatusunicda30.ui.theme.nav


import MainShell
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.estatusunicda30.ui.theme.auth.LoginScreen
import com.example.estatusunicda30.ui.theme.auth.SignUpScreen

@Composable
fun AppNav() {
    val root = rememberNavController()

    NavHost(
        navController = root,
        startDestination = "auth/login"
    ) {
        /* ============ LOGIN ============ */
        composable(route = "auth/login") {
            LoginScreen(
                onGoToRegister = { root.navigate("auth/register") },
                onLoggedIn = {
                    root.navigate("main") {
                        popUpTo("auth/login") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onForgotPassword = { email ->

                }
            )
        }

        /* ============ REGISTER ============ */
        composable(route = "auth/register") {
            SignUpScreen(
                onBack = { root.popBackStack() },
                onRegistered = {
                    root.navigate("main") {
                        popUpTo("auth/login") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        /* ============ APP MAIN (SHELL) ============ */
        composable(route = "main") {
            MainShell(
                onOpenEstadisticaOutOfBottom = null,
                onOpenEstadistica = { root.navigate("estadistica") }
            )
        }


    }
}