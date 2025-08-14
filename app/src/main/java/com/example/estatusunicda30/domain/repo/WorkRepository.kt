package com.example.estatusunicda30.domain.repo

import com.example.estatusunicda30.core.model.WorkItem
import kotlinx.coroutines.flow.Flow

interface WorkRepository {
    fun observeAll(): Flow<List<WorkItem>>
    fun observeMine(uid: String): Flow<List<WorkItem>>

    suspend fun create(
        title: String,
        description: String,
        creatorUid: String,
        creatorName: String?,
        assigneeUid: String? = null,
        assigneeName: String? = null
    )

    suspend fun assign(taskId: String, assigneeUid: String?, assigneeName: String?)
    suspend fun updateStatus(taskId: String, status: String)
}
