package com.example.estatusunicda30.core.model

data class WorkItem(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val status: String = "open",          // open | in_progress | done
    val assigneeUid: String? = null,
    val assigneeName: String? = null,
    val createdBy: String = "",
    val createdByName: String? = null,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)
