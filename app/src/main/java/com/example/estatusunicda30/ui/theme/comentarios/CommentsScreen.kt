package com.example.estatusunicda30.ui.theme.comentarios

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.estatusunicda30.core.model.Comment

@Composable
fun CommentsScreen(vm: CommentsViewModel = hiltViewModel()) {
    val ui by vm.ui.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Comentarios", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ui.list, key = { it.id }) { c -> CommentItem(c) }
        }

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = ui.input,
            onValueChange = vm::onChangeInput,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Escribe un comentario…") },
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = vm::send,
            enabled = !ui.loading && ui.input.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (ui.loading) "Enviando…" else "Publicar") }

        ui.error?.let {
            Spacer(Modifier.height(6.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun CommentItem(c: Comment) {
    ElevatedCard {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(c.name.ifBlank { "Usuario" }, style = MaterialTheme.typography.labelLarge)
            Text(c.text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}