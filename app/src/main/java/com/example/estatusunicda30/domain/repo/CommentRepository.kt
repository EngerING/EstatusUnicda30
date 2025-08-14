package com.example.estatusunicda30.domain.repo

import com.example.estatusunicda30.core.model.Comment
import kotlinx.coroutines.flow.Flow

interface CommentRepository {
    fun observePublic(): Flow<List<Comment>>
    suspend fun addPublic(text: String)
}