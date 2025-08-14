package com.example.estatusunicda30.domain.repo


import com.example.estatusunicda30.core.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun observe(uid: String): Flow<Profile?>
    suspend fun upsert(profile: Profile)
}