package com.example.estatusunicda30.ui.theme.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.estatusunicda30.R
import com.example.estatusunicda30.ui.theme.auth.AuthViewModel

import androidx.compose.runtime.setValue
@Composable
fun LoginScreen(
    onGoToRegister: () -> Unit,
    onLoggedIn: () -> Unit,
    onForgotPassword: (String) -> Unit = {},
    vm: AuthViewModel = hiltViewModel()
) {
    val ui by vm.ui.collectAsState()
    val user by vm.user.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    // Si el usuario ya está autenticado, navega
    LaunchedEffect(user) { if (user != null) onLoggedIn() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo (asegúrate de tener R.drawable.unicda_logo, o cambia el id)
            Image(
                painter = painterResource(id = R.drawable.unicda_logo),
                contentDescription = "UNICDA",
                modifier = Modifier
                    .height(72.dp)
                    .padding(top = 8.dp)
            )

            Text(
                "Estado Unicda",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )

            // Correo
            Surface(
                shape = RoundedCornerShape(14.dp),
                tonalElevation = 2.dp,
                shadowElevation = 6.dp
            ) {
                OutlinedTextField(
                    value = ui.email,
                    onValueChange = vm::onEmail,
                    leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                    label = { Text("Correo electrónico") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 56.dp)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(14.dp)
                )
            }

            // Contraseña
            Surface(
                shape = RoundedCornerShape(14.dp),
                tonalElevation = 2.dp,
                shadowElevation = 6.dp
            ) {
                OutlinedTextField(
                    value = ui.password,
                    onValueChange = vm::onPassword,
                    leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                    label = { Text("Contraseña") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        TextButton(onClick = { passwordVisible = !passwordVisible }) {
                            Text(if (passwordVisible) "Ocultar" else "Mostrar")
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { vm.signIn() }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 56.dp)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(14.dp)
                )
            }

            // Error (si hay)
            ui.error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Botón Acceso
            Button(
                onClick = { vm.signIn() },
                enabled = !ui.loading,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                if (ui.loading) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text(if (ui.loading) "Accediendo…" else "Acceso")
            }

            // ¿Olvidaste tu contraseña?
            TextButton(onClick = { onForgotPassword(ui.email) }) {
                Text("¿Has olvidado tu contraseña?")
            }

            Divider(thickness = 0.5.dp, modifier = Modifier.padding(top = 4.dp, bottom = 2.dp))

            // Crear cuenta
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("¿No tienes cuenta?")
                Spacer(Modifier.width(6.dp))
                TextButton(onClick = onGoToRegister) {
                    Text("Crear cuenta")
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}
