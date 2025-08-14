package com.example.estatusunicda30.ui.theme.nav


import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.HowToVote
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.BuildCircle
import androidx.compose.material.icons.outlined.Person

sealed class BottomDest(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    data object Home        : BottomDest("home",        "Inicio",      Icons.Outlined.Home)
    data object Votacion    : BottomDest("votacion",    "Votar",       Icons.Outlined.HowToVote)
    data object Comentarios : BottomDest("comentarios", "Comentarios", Icons.Outlined.ChatBubbleOutline)
    data object Progreso    : BottomDest("progreso",    "Técnico",     Icons.Outlined.BuildCircle)
    data object Perfil      : BottomDest("perfil",      "Perfil",      Icons.Outlined.Person)
    companion object {
        val all = listOf(Home, Votacion, Comentarios, Progreso, Perfil)
    }
}