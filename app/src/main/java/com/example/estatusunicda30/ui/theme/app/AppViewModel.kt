package com.example.estatusunicda30.ui.theme.app
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
class AppViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    private val profileRepo: ProfileRepository
) : ViewModel() {

    enum class ThemeMode { SYSTEM, LIGHT, DARK }

    data class Ui(
        val user: FirebaseUser? = null,
        val profile: Profile? = null,
        val bottomBarVisible: Boolean = false,
        val theme: ThemeMode = ThemeMode.SYSTEM,
        val message: String? = null,
        val loading: Boolean = false
    )

    private val _ui = MutableStateFlow(Ui())
    val ui: StateFlow<Ui> = _ui.asStateFlow()

    init {

        viewModelScope.launch {
            authRepo.authState.collectLatest { u ->
                _ui.update { it.copy(user = u, profile = null) }
                if (u != null) {
                    profileRepo.observe(u.uid).collect { p -> _ui.update { it.copy(profile = p) } }
                }
            }
        }
    }

    fun signOut() = viewModelScope.launch { authRepo.signOut() }
    fun setBottomBarVisible(v: Boolean) = _ui.update { it.copy(bottomBarVisible = v) }
    fun setTheme(mode: ThemeMode) = _ui.update { it.copy(theme = mode) }
    fun show(msg: String) = _ui.update { it.copy(message = msg) }
    fun consumeMessage() = _ui.update { it.copy(message = null) }
}