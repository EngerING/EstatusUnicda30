package com.example.estatusunicda30.ui.theme.votacion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.estatusunicda30.core.model.VoteAverages
import com.example.estatusunicda30.core.model.VoteRating
import com.example.estatusunicda30.domain.repo.AuthRepository
import com.example.estatusunicda30.domain.repo.VoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val SURVEY_ID = "current"

@HiltViewModel
class VotacionViewModel @Inject constructor(
    private val votes: VoteRepository,
    private val auth: AuthRepository
) : ViewModel() {

    data class Ui(
        val classrooms: Int = 3,
        val bathrooms: Int = 3,
        val ac: Int = 3,
        val loading: Boolean = false,
        val error: String? = null,
        val submitted: Boolean = false
    )

    private val _ui = MutableStateFlow(Ui())
    val ui: StateFlow<Ui> = _ui.asStateFlow()

    /** Promedios globales (live). */
    val averages: StateFlow<VoteAverages> =
        votes.observeAverages(SURVEY_ID)
            .stateIn(viewModelScope, SharingStarted.Eagerly, VoteAverages())

    init {
        // Prefill de sliders si el usuario ya tiene un voto guardado.
        viewModelScope.launch {
            auth.authState.collectLatest { user ->
                if (user == null) return@collectLatest
                votes.observeUserRating(SURVEY_ID, user.uid).collectLatest { r ->
                    if (r != null) {
                        _ui.update {
                            it.copy(
                                classrooms = r.classrooms,
                                bathrooms = r.bathrooms,
                                ac = r.ac
                            )
                        }
                    }
                }
            }
        }
    }

    fun onClassrooms(v: Float) = _ui.update { it.copy(classrooms = v.toInt()) }
    fun onBathrooms(v: Float)  = _ui.update { it.copy(bathrooms  = v.toInt()) }
    fun onAc(v: Float)         = _ui.update { it.copy(ac         = v.toInt()) }

    fun submit() = viewModelScope.launch {
        val u = auth.authState.firstOrNull() ?: run {
            _ui.update { it.copy(error = "Debes iniciar sesión para votar") }
            return@launch
        }

        _ui.update { it.copy(loading = true, error = null) }

        val rating = VoteRating(
            uid = u.uid,
            classrooms = _ui.value.classrooms,
            bathrooms  = _ui.value.bathrooms,
            ac         = _ui.value.ac,
            createdAt  = System.currentTimeMillis()
        )

        runCatching { votes.submitRating(SURVEY_ID, rating) }
            .onSuccess { _ui.update { it.copy(submitted = true) } }
            .onFailure { e -> _ui.update { it.copy(error = e.message ?: "Error") } }

        _ui.update { it.copy(loading = false) }
    }

    fun consumeSubmitted() = _ui.update { it.copy(submitted = false) }
}
