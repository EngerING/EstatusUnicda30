package com.example.estatusunicda30.ui.theme.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.BuildCircle
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.HowToVote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * Dashboard (Home) sin Top/Bottom bar; eso lo provee MainShell.
 * Navega mediante los callbacks recibidos.
 */
@Composable
fun HomeScreen(
    onOpenVotacion: () -> Unit,
    onOpenComentarios: () -> Unit,
    onOpenProgresoTecnico: () -> Unit,

) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FeatureCard(
            title = "Votación",
            subtitle = "Votación sobre el estado de las aulas, baños y aire acondicionado.",
            icon = { Icon(Icons.Outlined.HowToVote, null) },
            onClick = onOpenVotacion
        )
        FeatureCard(
            title = "Comentarios",
            subtitle = "Lea los comentarios públicos sobre las instalaciones de la universidad.",
            icon = { Icon(Icons.Outlined.ChatBubbleOutline, null) },
            onClick = onOpenComentarios
        )
        FeatureCard(
            title = "Progreso del técnico",
            subtitle = "Tareas de mantenimiento y reparaciones actuales.",
            icon = { Icon(Icons.Outlined.BuildCircle, null) },
            onClick = onOpenProgresoTecnico
        )

    }
}

@Composable
private fun FeatureCard(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    CompositionLocalProvider(LocalContentColor provides primary) { icon() }
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
