import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BuildCircle
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.HowToVote
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.estatusunicda30.ui.theme.app.AppViewModel
import com.example.estatusunicda30.ui.theme.comentarios.CommentsScreen
import com.example.estatusunicda30.ui.theme.home.HomeScreen
import com.example.estatusunicda30.ui.theme.nav.BottomDest
import com.example.estatusunicda30.ui.theme.progreso.ProgresoScreen
import com.example.estatusunicda30.ui.theme.votacion.VotacionScreen
import com.example.estatusunicda30.ui.theme.perfil.PerfilScreen
import com.example.estatusunicda30.ui.theme.config.SettingsScreen
@Composable
fun MainShell(
    appVm: AppViewModel = hiltViewModel(),
    onOpenEstadisticaOutOfBottom: (() -> Unit)? = null,
    onOpenEstadistica: () -> Unit
) {
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val current = backStack?.destination

    // Rutas del bottom (desde BottomDest)
    val bottomRoutes = remember { BottomDest.all.map { it.route }.toSet() }

    // Back: si estás en cualquier tab del bottom (menos Home) vuelve a Home
    BackHandler(enabled = current?.route in bottomRoutes && current?.route != BottomDest.Home.route) {
        nav.navigate(BottomDest.Home.route) {
            popUpTo(nav.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        topBar = { AppTopBar(appVm, onOpenConfig = { nav.navigate("config") }) }, // <- botón engranaje
        bottomBar = {
            NavigationBar {
                BottomDest.all.forEach { dest ->
                    val selected = current?.hierarchy?.any { it.route == dest.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            nav.navigate(dest.route) {
                                popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(dest.icon, contentDescription = dest.label) },
                        label = { Text(dest.label, fontSize = 11.sp) }
                    )
                }
            }
        }
    ) { inner ->
        NavHost(
            navController = nav,
            startDestination = BottomDest.Home.route,
            modifier = Modifier.padding(inner)
        ) {
            composable(BottomDest.Home.route) {
                HomeScreen(
                    onOpenVotacion        = { nav.navigate(BottomDest.Votacion.route) },
                    onOpenComentarios     = { nav.navigate(BottomDest.Comentarios.route) },
                    onOpenProgresoTecnico = { nav.navigate(BottomDest.Progreso.route) },

                )
            }

            // Un solo destino por route
            composable(BottomDest.Votacion.route)     { VotacionScreen() }
            composable(BottomDest.Comentarios.route)  { CommentsScreen() }
            composable(BottomDest.Progreso.route)     { ProgresoScreen() }
            composable(BottomDest.Perfil.route) { PerfilScreen() }
            composable("config") {
                SettingsScreen(onBack = { nav.popBackStack() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppTopBar(
    appVm: AppViewModel,
    onOpenConfig: () -> Unit
) {
    val ui by appVm.ui.collectAsState()
    TopAppBar(
        title = {
            Text(
                text = "¡Bienvenido, ${ui.profile?.displayName ?: "estudiante"}!",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = {
            // Botón Configuración (engranaje)
            IconButton(onClick = onOpenConfig) {
                Icon(Icons.Outlined.Settings, contentDescription = "Configuración")
            }

            // Avatar con inicial
            val initials = remember(ui.profile?.displayName) {
                (ui.profile?.displayName ?: "E").trim().take(1).uppercase()
            }
            Box(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    initials,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}

@Composable
private fun PlaceholderScreen(title: String) {
    Surface { Text(text = title, modifier = Modifier.padding(24.dp)) }
}

