package com.example.estatusunicda30.ui.theme


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary            = BrandPrimary,
    onPrimary          = BrandOnPrimary,
    primaryContainer   = BrandPrimaryContainer,
    onPrimaryContainer = BrandOnPrimaryContainer,
    secondary          = BrandSecondary,
    onSecondary        = BrandOnSecondary,
    background         = BrandBackground,
    surface            = BrandSurface,
    surfaceVariant     = BrandSurfaceVariant,
    outline            = BrandOutline,
    error              = BrandError,
    onError            = BrandOnError
)

private val DarkColors = darkColorScheme(
    primary            = BrandPrimaryDark,
    onPrimary          = BrandOnPrimary,
    primaryContainer   = BrandPrimaryContainerDark,
    onPrimaryContainer = BrandOnPrimary,
    secondary          = BrandSecondary,
    onSecondary        = BrandOnSecondary,
    background         = BrandBackgroundDark,
    surface            = BrandSurfaceDark,
    surfaceVariant     = BrandSurfaceVariantDark,
    outline            = BrandOutlineDark,
    error              = BrandError,
    onError            = BrandOnError
)

@Composable
fun EstatusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        shapes = Shapes(
            small = RoundedCornerShape(12.dp),
            medium = RoundedCornerShape(18.dp), // cards como en la FT
            large = RoundedCornerShape(22.dp)
        ),
        content = content
    )
}