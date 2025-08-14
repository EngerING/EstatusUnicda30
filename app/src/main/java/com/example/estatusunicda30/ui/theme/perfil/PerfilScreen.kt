package com.example.estatusunicda30.ui.theme.perfil
import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.estatusunicda30.ui.theme.app.AppViewModel

@Composable
fun PerfilScreen(
    appVm: AppViewModel = hiltViewModel(),
    // Si en vez de cerrar la app quieres volver al login,
    // pásame un callback desde AppNav/MainShell y úsalo aquí:
    onSignedOut: (() -> Unit)? = null
) {
    val ui by appVm.ui.collectAsState()
    val name  = (ui.profile?.displayName?.ifBlank { null } ?: "Estudiante")
    val email = ui.user?.email ?: "—"
    val initial = name.trim().take(1).uppercase()

    val context = LocalContext.current
    var showConfirm by remember { mutableStateOf(false) }

    Surface(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar con letra
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = email,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(28.dp))

            // Botón Cerrar sesión
            Button(
                onClick = { showConfirm = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Icon(Icons.Outlined.Logout, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Cerrar sesión")
            }
        }
    }

    // Diálogo de confirmación
    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("¿Cerrar sesión?") },
            text  = { Text("¿Estás seguro de que quieres cerrar sesión?") },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) { Text("Cancelar") }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirm = false
                        appVm.signOut()
                        // Opción A: cerrar la app
                        (context as? Activity)?.let { act ->
                            Toast.makeText(act, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                            act.finishAffinity()
                        }
                        // Opción B (alternativa): volver al login en vez de cerrar app
                        onSignedOut?.invoke()
                    }
                ) { Text("Sí, cerrar") }
            }
        )
    }
}
