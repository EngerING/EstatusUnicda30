package com.example.estatusunicda30.data.auth

import com.example.estatusunicda30.core.model.Comment
import com.example.estatusunicda30.domain.repo.CommentRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

@Singleton
class CommentRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : CommentRepository {

    private val col get() = db.collection("comments")

    override fun observePublic(): Flow<List<Comment>> = callbackFlow {
        val reg = col.orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(100)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    close(err); return@addSnapshotListener
                }
                val list = snap?.documents?.map { it.toComment() } ?: emptyList()
                trySend(list)
            }
        awaitClose { reg.remove() }
    }

    override suspend fun addPublic(text: String) {
        val u = auth.currentUser ?: error("Debes iniciar sesión")
        val data = mapOf(
            "uid" to u.uid,
            "name" to (u.displayName ?: "Usuario"),
            "text" to text.trim(),
            "createdAt" to System.currentTimeMillis() // simple y consistente
        )
        col.document().set(data).await()
    }
}

private fun DocumentSnapshot.toComment(): Comment = Comment(
    id = id,
    uid = getString("uid") ?: "",
    name = getString("name") ?: "",
    text = getString("text") ?: "",
    createdAt = getLong("createdAt") ?: 0L
)