package com.example.estatusunicda30.domain.repo
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val authState: Flow<FirebaseUser?>
    suspend fun signIn(email: String, password: String): FirebaseUser
    suspend fun signUp(email: String, password: String): FirebaseUser
    suspend fun signInAnonymously(): FirebaseUser
    suspend fun signOut()

}