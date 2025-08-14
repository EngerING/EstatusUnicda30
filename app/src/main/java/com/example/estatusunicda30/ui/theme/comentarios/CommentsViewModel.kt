package com.example.estatusunicda30.ui.theme.comentarios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.estatusunicda30.core.model.Comment
import com.example.estatusunicda30.domain.repo.CommentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class CommentsViewModel @Inject constructor(
    private val repo: CommentRepository
) : ViewModel() {

    data class Ui(
        val list: List<Comment> = emptyList(),
        val input: String = "",
        val loading: Boolean = false,
        val error: String? = null
    )

    private val _ui = MutableStateFlow(Ui())
    val ui: StateFlow<Ui> = _ui.asStateFlow()

    init {
        viewModelScope.launch {
            repo.observePublic().collect { items ->
                _ui.update { it.copy(list = items) }
            }
        }
    }

    fun onChangeInput(t: String) = _ui.update { it.copy(input = t) }

    fun send() = viewModelScope.launch {
        val text = _ui.value.input.trim()
        if (text.isEmpty()) return@launch
        _ui.update { it.copy(loading = true, error = null) }
        runCatching { repo.addPublic(text) }
            .onSuccess { _ui.update { it.copy(input = "") } }
            .onFailure { e -> _ui.update { it.copy(error = e.message) } }
        _ui.update { it.copy(loading = false) }
    }
}