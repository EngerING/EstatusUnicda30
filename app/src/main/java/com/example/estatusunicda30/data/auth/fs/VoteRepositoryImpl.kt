package com.example.estatusunicda30.data.auth.fs

import com.example.estatusunicda30.core.model.VoteAverages
import com.example.estatusunicda30.core.model.VoteRating
import com.example.estatusunicda30.domain.repo.VoteRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoteRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
) : VoteRepository {

    private fun ratingsRef(surveyId: String) =
        db.collection("surveys").document(surveyId).collection("ratings")

    override fun observeUserRating(surveyId: String, uid: String): Flow<VoteRating?> = callbackFlow {
        val reg = ratingsRef(surveyId).document(uid).addSnapshotListener { snap, err ->
            if (err != null) { close(err); return@addSnapshotListener }
            val d = snap?.data
            if (d == null) trySend(null) else trySend(
                VoteRating(
                    uid = snap.id,
                    classrooms = (d["classrooms"] as? Number)?.toInt() ?: 3,
                    bathrooms = (d["bathrooms"] as? Number)?.toInt() ?: 3,
                    ac = (d["ac"] as? Number)?.toInt() ?: 3,
                    createdAt = (d["createdAt"] as? Number)?.toLong() ?: 0L
                )
            )
        }
        awaitClose { reg.remove() }
    }

    override fun observeAverages(surveyId: String): Flow<VoteAverages> = callbackFlow {
        val reg = ratingsRef(surveyId).addSnapshotListener { snap, err ->
            if (err != null) { close(err); return@addSnapshotListener }
            val docs = snap?.documents ?: emptyList()
            val count = docs.size
            if (count == 0) { trySend(VoteAverages()); return@addSnapshotListener }
            var c = 0.0; var b = 0.0; var a = 0.0
            for (d in docs) {
                c += (d.get("classrooms") as? Number)?.toDouble() ?: 0.0
                b += (d.get("bathrooms")  as? Number)?.toDouble() ?: 0.0
                a += (d.get("ac")         as? Number)?.toDouble() ?: 0.0
            }
            trySend(
                VoteAverages(
                    count = count,
                    classroomsAvg = c / count,
                    bathroomsAvg = b / count,
                    acAvg = a / count
                )
            )
        }
        awaitClose { reg.remove() }
    }

    override suspend fun submitRating(surveyId: String, rating: VoteRating) {
        val doc = ratingsRef(surveyId).document(rating.uid)
        val data = mapOf(
            "classrooms" to rating.classrooms,
            "bathrooms"  to rating.bathrooms,
            "ac"         to rating.ac,
            "createdAt"  to rating.createdAt
        )
        doc.set(data).await()
    }
}