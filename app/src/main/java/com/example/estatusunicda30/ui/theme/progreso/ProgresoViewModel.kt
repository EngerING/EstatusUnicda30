package com.example.estatusunicda30.ui.theme.progreso

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.estatusunicda30.core.model.WorkItem
import com.example.estatusunicda30.domain.repo.AuthRepository
import com.example.estatusunicda30.domain.repo.WorkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class ProgresoViewModel @Inject constructor(
    private val work: WorkRepository,
    private val auth: AuthRepository
) : ViewModel() {

    enum class Filter { ALL, MINE, OPEN, IN_PROGRESS, DONE }

    data class Ui(
        val items: List<WorkItem> = emptyList(),
        val filter: Filter = Filter.ALL,
        val loading: Boolean = false,
        val error: String? = null,
        // Dialog "nueva tarea"
        val showNewDialog: Boolean = false,
        val newTitle: String = "",
        val newDesc: String = "",
        val assignToMe: Boolean = true
    )

    private val _ui = MutableStateFlow(Ui())
    val ui: StateFlow<Ui> = _ui.asStateFlow()

    private val me = auth.authState.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    init {
        // Escucha global de tareas
        viewModelScope.launch {
            work.observeAll()
                .catch { e -> _ui.update { it.copy(error = e.message) } }
                .collect { list -> _ui.update { it.copy(items = list, error = null) } }
        }
    }

    fun setFilter(f: Filter) = _ui.update { it.copy(filter = f) }
    fun openNewDialog() = _ui.update { it.copy(showNewDialog = true, newTitle = "", newDesc = "", assignToMe = true) }
    fun closeNewDialog() = _ui.update { it.copy(showNewDialog = false) }
    fun onNewTitle(t: String) = _ui.update { it.copy(newTitle = t) }
    fun onNewDesc(t: String)  = _ui.update { it.copy(newDesc = t) }
    fun onAssignToMe(b: Boolean) = _ui.update { it.copy(assignToMe = b) }

    fun createTask() = viewModelScope.launch {
        val u = me.value ?: return@launch
        val title = _ui.value.newTitle.trim()
        if (title.isBlank()) return@launch

        _ui.update { it.copy(loading = true) }
        val assUid = if (_ui.value.assignToMe) u.uid else null
        val assName = if (_ui.value.assignToMe) (u.displayName ?: u.email ?: "Usuario") else null

        runCatching {
            work.create(
                title = title,
                description = _ui.value.newDesc.trim(),
                creatorUid = u.uid,
                creatorName = u.displayName ?: u.email,
                assigneeUid = assUid,
                assigneeName = assName
            )
        }.onFailure { e -> _ui.update { it.copy(error = e.message) } }

        _ui.update { it.copy(loading = false, showNewDialog = false, newTitle = "", newDesc = "") }
    }

    fun takeTask(id: String) = viewModelScope.launch {
        val u = me.value ?: return@launch
        work.assign(id, u.uid, u.displayName ?: u.email ?: "Usuario")
    }

    fun releaseTask(id: String) = viewModelScope.launch {
        work.assign(id, null, null)
    }

    fun changeStatus(id: String, status: String) = viewModelScope.launch {
        work.updateStatus(id, status)
    }

    /** Lista visible según filtro */
    val visible: StateFlow<List<WorkItem>> = combine(ui, me) { ui, meUser ->
        when (ui.filter) {
            Filter.ALL -> ui.items
            Filter.MINE -> ui.items.filter { it.assigneeUid == meUser?.uid }
            Filter.OPEN -> ui.items.filter { it.status == "open" }
            Filter.IN_PROGRESS -> ui.items.filter { it.status == "in_progress" }
            Filter.DONE -> ui.items.filter { it.status == "done" }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
}