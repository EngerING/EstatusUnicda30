package com.example.estatusunicda30.ui.theme.progreso

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.estatusunicda30.core.model.WorkItem

@Composable
fun ProgresoScreen(vm: ProgresoViewModel = hiltViewModel()) {
    val ui by vm.ui.collectAsState()
    val list by vm.visible.collectAsState()

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Progreso del técnico", style = MaterialTheme.typography.headlineMedium)

            FilterChipsRow(selected = ui.filter, onSelect = vm::setFilter)

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(list, key = { it.id }) { item ->
                    WorkCard(
                        item = item,
                        onTake = { vm.takeTask(it) },
                        onRelease = { vm.releaseTask(it) },
                        onChangeStatus = vm::changeStatus
                    )
                }
            }

            ui.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        }

        // FAB: nueva tarea
        FloatingActionButton(
            onClick = vm::openNewDialog,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) { Icon(Icons.Default.Check, contentDescription = "Nueva tarea") }

        if (ui.showNewDialog) {
            NewTaskDialog(
                title = ui.newTitle,
                desc = ui.newDesc,
                assignToMe = ui.assignToMe,
                onTitle = vm::onNewTitle,
                onDesc = vm::onNewDesc,
                onAssignToMe = vm::onAssignToMe,
                onDismiss = vm::closeNewDialog,
                onCreate = vm::createTask
            )
        }
    }
}

@Composable
private fun FilterChipsRow(
    selected: ProgresoViewModel.Filter,
    onSelect: (ProgresoViewModel.Filter) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Chip("Todos", selected == ProgresoViewModel.Filter.ALL) { onSelect(ProgresoViewModel.Filter.ALL) }
        Chip("Mías",  selected == ProgresoViewModel.Filter.MINE) { onSelect(ProgresoViewModel.Filter.MINE) }
        Chip("Abiertas", selected == ProgresoViewModel.Filter.OPEN) { onSelect(ProgresoViewModel.Filter.OPEN) }
        Chip("En curso", selected == ProgresoViewModel.Filter.IN_PROGRESS) { onSelect(ProgresoViewModel.Filter.IN_PROGRESS) }
        Chip("Hechas", selected == ProgresoViewModel.Filter.DONE) { onSelect(ProgresoViewModel.Filter.DONE) }
    }
}

@Composable private fun Chip(text: String, selected: Boolean, onClick: () -> Unit) {
    AssistChip(onClick = onClick, label = { Text(text) }, leadingIcon = if (selected) { { Icon(Icons.Default.Check, null) } } else null)
}

@Composable
private fun WorkCard(
    item: WorkItem,
    onTake: (String) -> Unit,
    onRelease: (String) -> Unit,
    onChangeStatus: (String, String) -> Unit
) {
    ElevatedCard {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(item.title, style = MaterialTheme.typography.titleMedium)
            if (item.description.isNotBlank()) {
                Text(item.description, style = MaterialTheme.typography.bodyMedium)
            }
            Text(
                "Asignado a: ${item.assigneeName ?: "Sin asignar"}",
                style = MaterialTheme.typography.bodySmall
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Text("Estado: ${item.status}", style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.weight(1f))
                // Botón tomar/liberar
                if (item.assigneeUid == null) {
                    OutlinedButton(onClick = { onTake(item.id) }) { Text("Tomar") }
                } else {
                    OutlinedButton(onClick = { onRelease(item.id) }) { Text("Liberar") }
                }
                // Cambiar estado
                StatusMenu(current = item.status) { new -> onChangeStatus(item.id, new) }
            }
        }
    }
}

@Composable
private fun StatusMenu(current: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    OutlinedButton(onClick = { expanded = true }) { Text("Cambiar estado") }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        listOf("open" to "Abierto", "in_progress" to "En curso", "done" to "Hecho").forEach { (value, label) ->
            DropdownMenuItem(text = { Text(label) }, onClick = { expanded = false; onSelect(value) })
        }
    }
}

@Composable
private fun NewTaskDialog(
    title: String,
    desc: String,
    assignToMe: Boolean,
    onTitle: (String) -> Unit,
    onDesc: (String) -> Unit,
    onAssignToMe: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    onCreate: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva tarea") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = onTitle, label = { Text("Título") }, singleLine = true)
                OutlinedTextField(value = desc, onValueChange = onDesc, label = { Text("Descripción (opcional)") })
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = assignToMe, onCheckedChange = onAssignToMe)
                    Text("Asignármela")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onCreate, enabled = title.isNotBlank()) { Text("Crear") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}