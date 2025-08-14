package com.example.estatusunicda30.data.auth

import com.example.estatusunicda30.domain.repo.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    private val _state = MutableStateFlow(auth.currentUser)
    override val authState: StateFlow<FirebaseUser?> = _state.asStateFlow()

    init {
        auth.addAuthStateListener { _state.value = it.currentUser }
    }

    override suspend fun signIn(email: String, password: String): FirebaseUser =
        auth.signInWithEmailAndPassword(email, password).await().user ?: error("No user")

    override suspend fun signUp(email: String, password: String): FirebaseUser =
        auth.createUserWithEmailAndPassword(email, password).await().user ?: error("No user")

    override suspend fun signInAnonymously(): FirebaseUser =
        auth.signInAnonymously().await().user ?: error("No user")

    override suspend fun signOut() { auth.signOut() }
}