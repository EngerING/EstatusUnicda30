package com.example.estatusunicda30.ui.theme.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.estatusunicda30.core.model.Profile
import com.example.estatusunicda30.domain.repo.AuthRepository
import com.example.estatusunicda30.domain.repo.ProfileRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository,
    private val profiles: ProfileRepository
) : ViewModel() {

    data class UiState(
        val email: String = "",
        val password: String = "",
        val loading: Boolean = false,
        val error: String? = null
    )

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    val user: StateFlow<FirebaseUser?> =
        repo.authState.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun onEmail(v: String) = _ui.update { it.copy(email = v) }
    fun onPassword(v: String) = _ui.update { it.copy(password = v) }

    fun signIn() = guarded { repo.signIn(ui.value.email.trim(), ui.value.password) }
    fun signInAnon() = guarded { repo.signInAnonymously() }
    fun signOut() = guarded { repo.signOut() }


    fun signUp() = guarded {
        val email = ui.value.email.trim()
        val pass = ui.value.password
        repo.signUp(email, pass)

        // Espera al user autenticado desde el Flow si aún no llegó
        val u: FirebaseUser = user.value ?: repo.authState.filterNotNull().first()

        val display = email.substringBefore('@').ifBlank { "Estudiante" }
        profiles.upsert(Profile(
            uid = u.uid, displayName = display,
            photoUrl = TODO(),
        ))
    }

    private inline fun guarded(crossinline block: suspend () -> Unit) =
        viewModelScope.launch {
            _ui.update { it.copy(loading = true, error = null) }
            runCatching { block() }
                .onFailure { e -> _ui.update { it.copy(error = e.message ?: "Error") } }
            _ui.update { it.copy(loading = false) }
        }
}