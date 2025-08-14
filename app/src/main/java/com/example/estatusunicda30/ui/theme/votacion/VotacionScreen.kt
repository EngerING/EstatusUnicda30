package com.example.estatusunicda30.ui.theme.votacion

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.estatusunicda30.core.model.notify.AppNotifier

import kotlin.math.round

@Composable
fun VotacionScreen(
    vm: VotacionViewModel = hiltViewModel()
) {
    val ui  by vm.ui.collectAsState()
    val avg by vm.averages.collectAsState()

    val context = LocalContext.current
    val snackHost = remember { SnackbarHostState() }

    // Pide permiso en Android 13+
    AskNotificationsPermission()

    // Feedback al enviar: Snackbar + notificación del sistema
    LaunchedEffect(ui.submitted) {
        if (ui.submitted) {
            snackHost.showSnackbar("✅ Voto enviado correctamente")
            AppNotifier.sendVoteSuccess(context)
            vm.consumeSubmitted()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackHost) }
    ) { inner ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Votación",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Evalúa el estado de las instalaciones:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                AveragesCard(
                    count = avg.count,
                    classrooms = avg.classroomsAvg,
                    bathrooms = avg.bathroomsAvg,
                    ac = avg.acAvg
                )
            }

            // Sliders
            item {
                VoteSliderCard(
                    title = "Aulas",
                    value = ui.classrooms,
                    onValueChange = vm::onClassrooms
                )
            }
            item {
                VoteSliderCard(
                    title = "Baños",
                    value = ui.bathrooms,
                    onValueChange = vm::onBathrooms
                )
            }
            item {
                VoteSliderCard(
                    title = "Aire acondicionado",
                    value = ui.ac,
                    onValueChange = vm::onAc
                )
            }

            // Botón enviar + error
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = vm::submit,
                        enabled = !ui.loading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (ui.loading) "Enviando…" else "Enviar voto")
                    }
                    ui.error?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun AveragesCard(
    count: Int,
    classrooms: Double,
    bathrooms: Double,
    ac: Double
) {
    ElevatedCard {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Promedios", style = MaterialTheme.typography.titleMedium)
            Text(
                "Votos: $count",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            AvgRow("Aulas", classrooms)
            AvgRow("Baños", bathrooms)
            AvgRow("Aire acondicionado", ac)
        }
    }
}

@Composable
private fun AvgRow(label: String, value: Double) {
    val shown = if (value.isNaN()) 0.0 else value
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label)
        Text(String.format("%.1f/5", round(shown * 10) / 10))
    }
}

@Composable
private fun VoteSliderCard(
    title: String,
    value: Int,
    onValueChange: (Float) -> Unit
) {
    ElevatedCard {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Slider(
                value = value.toFloat(),
                onValueChange = onValueChange,
                valueRange = 1f..5f,
                steps = 3 // 1..5 → puntos intermedios
            )
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("1")
                Text(value.toString(), fontWeight = FontWeight.Bold)
                Text("5")
            }
        }
    }
}

/** Pide el permiso de notificaciones en Android 13+. */
@Composable
private fun AskNotificationsPermission() {
    if (Build.VERSION.SDK_INT >= 33) {
        val context = LocalContext.current
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { /* no-op */ }
        )
        LaunchedEffect(Unit) {
            val granted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            if (!granted) launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}