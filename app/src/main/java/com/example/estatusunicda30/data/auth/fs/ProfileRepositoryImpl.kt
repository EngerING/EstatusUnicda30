package com.example.estatusunicda30.data.auth.fs

import com.example.estatusunicda30.core.model.Profile
import com.example.estatusunicda30.domain.repo.ProfileRepository
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
) : ProfileRepository {

    private fun doc(uid: String) = db.collection("profiles").document(uid)

    override fun observe(uid: String): Flow<Profile?> = callbackFlow {
        val reg = doc(uid).addSnapshotListener { s, _ ->
            trySend(s?.toObject(Profile::class.java))
        }
        awaitClose { reg.remove() }
    }

    override suspend fun upsert(profile: Profile) {
        doc(profile.uid).set(profile).await()
    }
}