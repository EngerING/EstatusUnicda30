package com.example.estatusunicda30.data.auth


import kotlinx.coroutines.flow.Flow

/** Modo de tema que usará la app. */
enum class ThemeMode {
    SYSTEM,  // Sigue el tema del sistema (recomendado)
    LIGHT,   // Siempre claro
    DARK     // Siempre oscuro
}

/**
 * Abstracción de preferencias de la app.
 * La implementación concreta (DataStore) ya la conectamos con Hilt como SettingsRepositoryImpl.
 */
interface SettingsRepository {

    /** Flujo con el modo de tema actual (se emite en tiempo real). */
    val themeMode: Flow<ThemeMode>

    /** Persiste el modo de tema. */
    suspend fun setTheme(mode: ThemeMode)
}