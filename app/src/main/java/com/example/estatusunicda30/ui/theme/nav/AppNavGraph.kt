package com.example.estatusunicda30.ui.theme.nav


import MainShell
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.estatusunicda30.ui.theme.app.AppViewModel
import com.example.estatusunicda30.ui.theme.auth.LoginScreen
import com.example.estatusunicda30.ui.theme.auth.SignUpScreen



sealed class AuthDest(val route: String) {
    data object Login    : AuthDest("auth/login")
    data object Register : AuthDest("auth/register")
}

// Ruta para el shell
private const val SHELL_ROUTE = "shell"

@Composable
fun AppNavGraph(
    nav: NavHostController,
    onOpenEstadisticaOutOfBottom: (() -> Unit)? = null
) {
    val appVm: AppViewModel = hiltViewModel()
    val ui by appVm.ui.collectAsState()

    // Redirige automáticamente cuando cambia la sesión (login / logout)
    LaunchedEffect(ui.user) {
        if (ui.user == null) {
            nav.navigate(AuthDest.Login.route) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        } else {
            nav.navigate(SHELL_ROUTE) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = nav,
        startDestination = if (ui.user == null) AuthDest.Login.route else SHELL_ROUTE
    ) {

        composable(AuthDest.Login.route) {
            LoginScreen(
                onGoToRegister = { nav.navigate(AuthDest.Register.route) },
                onLoggedIn = {
                    nav.navigate(SHELL_ROUTE) {
                        popUpTo(AuthDest.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onForgotPassword = { email ->
                    // opcional: navegar a "forgot" o disparar reset
                    // vm.sendReset(email)
                }
            )
        }

        composable(AuthDest.Register.route) {
            SignUpScreen(
                onBack = { nav.popBackStack() },
                onRegistered = {
                    nav.navigate(SHELL_ROUTE) {
                        popUpTo(AuthDest.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(SHELL_ROUTE) {
            MainShell(
                onOpenEstadisticaOutOfBottom = onOpenEstadisticaOutOfBottom,
                onOpenEstadistica = { nav.navigate("estadistica") }
            )
        }
        /* =================== RUTAS FUERA DEL BOTTOM (si las expones aquí) =================== */
        // Nota: ya tienes "config" y "estadistica" dentro del NavHost interno del MainShell.
        // Si prefieres que vivan fuera, podrías moverlas aquí. Tal como lo tienes, déjalo en el shell.
    }
}
