package com.example.estatusunicda30.ui.theme.auth
import com.example.estatusunicda30.core.model.Profile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.estatusunicda30.domain.repo.AuthRepository
import com.example.estatusunicda30.domain.repo.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class SignUpUi(
    val displayName: String = "",
    val email: String = "",
    val password: String = "",
    val confirm: String = "",
    val loading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val auth: AuthRepository,
    private val profiles: ProfileRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(SignUpUi())
    val ui: StateFlow<SignUpUi> = _ui

    fun setDisplayName(v: String) = _ui.update { it.copy(displayName = v) }
    fun setEmail(v: String)       = _ui.update { it.copy(email = v) }
    fun setPassword(v: String)    = _ui.update { it.copy(password = v) }
    fun setConfirm(v: String)     = _ui.update { it.copy(confirm = v) }
    fun clearError()              = _ui.update { it.copy(error = null) }

    fun register(onSuccess: () -> Unit) = viewModelScope.launch {
        val s = ui.value

        when {
            s.displayName.isBlank() || s.email.isBlank() || s.password.isBlank() ->
                return@launch _ui.update { it.copy(error = "Completa todos los campos") }
            s.password.length < 6 ->
                return@launch _ui.update { it.copy(error = "La contraseña debe tener al menos 6 caracteres") }
            s.password != s.confirm ->
                return@launch _ui.update { it.copy(error = "Las contraseñas no coinciden") }
        }

        _ui.update { it.copy(loading = true, error = null) }

        runCatching {

            auth.signUp(s.email.trim(), s.password)


            val user = auth.authState.filterNotNull().first()

            // 3) Guardar/actualizar el perfil (solo uid y displayName)
            profiles.upsert(
                Profile(
                    uid = user.uid,
                    displayName = s.displayName.trim()
                )
            )
        }.onFailure { e ->
            _ui.update { it.copy(error = e.message ?: "Error al registrar", loading = false) }
        }.onSuccess {
            _ui.update { it.copy(loading = false) }
            onSuccess()
        }
    }
}
