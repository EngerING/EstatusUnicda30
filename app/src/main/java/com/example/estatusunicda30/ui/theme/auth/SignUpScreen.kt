package com.example.estatusunicda30.ui.theme.auth
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onBack: () -> Unit,
    onRegistered: () -> Unit,
    vm: AuthViewModel = hiltViewModel()
) {
    val ui by vm.ui.collectAsState()
    val user by vm.user.collectAsState()

    var passVisible by remember { mutableStateOf(false) }
    var confirm by remember { mutableStateOf("") }
    var confirmVisible by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }

    // si se registró y ya hay user -> navega
    LaunchedEffect(user) { if (user != null) onRegistered() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Crear cuenta") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // correo
            OutlinedTextField(
                value = ui.email,
                onValueChange = { localError = null; vm.onEmail(it) },
                label = { Text("Correo electrónico") },
                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            // contraseña
            OutlinedTextField(
                value = ui.password,
                onValueChange = { localError = null; vm.onPassword(it) },
                label = { Text("Contraseña (mín. 6)") },
                leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                trailingIcon = {
                    TextButton(onClick = { passVisible = !passVisible }) {
                        Text(if (passVisible) "Ocultar" else "Mostrar")
                    }
                },
                singleLine = true,
                visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            // confirmar contraseña
            OutlinedTextField(
                value = confirm,
                onValueChange = { localError = null; confirm = it },
                label = { Text("Confirmar contraseña") },
                leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                trailingIcon = {
                    TextButton(onClick = { confirmVisible = !confirmVisible }) {
                        Text(if (confirmVisible) "Ocultar" else "Mostrar")
                    }
                },
                singleLine = true,
                visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        submitSignUp(
                            email = ui.email,
                            password = ui.password,
                            confirm = confirm,
                            setLocalError = { localError = it },
                            onOk = vm::signUp
                        )
                    }
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            // errores
            val errorMsg = localError ?: ui.error
            if (errorMsg != null) {
                Text(errorMsg, color = MaterialTheme.colorScheme.error)
            }

            // botón crear cuenta
            Button(
                onClick = {
                    submitSignUp(
                        email = ui.email,
                        password = ui.password,
                        confirm = confirm,
                        setLocalError = { localError = it },
                        onOk = vm::signUp
                    )
                },
                enabled = !ui.loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (ui.loading) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text(if (ui.loading) "Creando cuenta…" else "Crear cuenta")
            }
        }
    }
}

private fun submitSignUp(
    email: String,
    password: String,
    confirm: String,
    setLocalError: (String?) -> Unit,
    onOk: () -> Unit
) {
    when {
        email.isBlank() || password.isBlank() || confirm.isBlank() ->
            setLocalError("Completa todos los campos")
        password.length < 6 ->
            setLocalError("La contraseña debe tener al menos 6 caracteres")
        password != confirm ->
            setLocalError("Las contraseñas no coinciden")
        else -> {
            setLocalError(null)
            onOk()
        }
    }
}

