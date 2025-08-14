package com.example.estatusunicda30.domain.repo
import com.example.estatusunicda30.core.model.VoteAverages
import com.example.estatusunicda30.core.model.VoteRating
import kotlinx.coroutines.flow.Flow

interface VoteRepository {
    fun observeUserRating(surveyId: String, uid: String): Flow<VoteRating?>
    fun observeAverages(surveyId: String): Flow<VoteAverages>
    suspend fun submitRating(surveyId: String, rating: VoteRating)
}