package com.example.estatusunicda30.data.auth


import com.example.estatusunicda30.core.model.WorkItem
import com.example.estatusunicda30.domain.repo.WorkRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
) : WorkRepository {

    private val col get() = db.collection("work_items")

    override fun observeAll(): Flow<List<WorkItem>> = callbackFlow {
        val reg = col.orderBy("updatedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) { trySend(emptyList()); return@addSnapshotListener }
                trySend(snap?.documents?.map { it.toWorkItem() } ?: emptyList())
            }
        awaitClose { reg.remove() }
    }

    override fun observeMine(uid: String): Flow<List<WorkItem>> = callbackFlow {
        val reg = col.whereEqualTo("assigneeUid", uid)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) { trySend(emptyList()); return@addSnapshotListener }
                trySend(snap?.documents?.map { it.toWorkItem() } ?: emptyList())
            }
        awaitClose { reg.remove() }
    }

    override suspend fun create(
        title: String,
        description: String,
        creatorUid: String,
        creatorName: String?,
        assigneeUid: String?,
        assigneeName: String?
    ) {
        val now = System.currentTimeMillis()
        val data = mapOf(
            "title" to title,
            "description" to description,
            "status" to "open",
            "assigneeUid" to assigneeUid,
            "assigneeName" to assigneeName,
            "createdBy" to creatorUid,
            "createdByName" to creatorName,
            "createdAt" to now,
            "updatedAt" to now
        )
        col.document().set(data).await()
    }

    override suspend fun assign(taskId: String, assigneeUid: String?, assigneeName: String?) {
        col.document(taskId).update(
            mapOf(
                "assigneeUid" to assigneeUid,
                "assigneeName" to assigneeName,
                "updatedAt" to System.currentTimeMillis()
            )
        ).await()
    }

    override suspend fun updateStatus(taskId: String, status: String) {
        col.document(taskId).update(
            mapOf(
                "status" to status,
                "updatedAt" to System.currentTimeMillis()
            )
        ).await()
    }
}

private fun DocumentSnapshot.toWorkItem(): WorkItem = WorkItem(
    id = id,
    title = getString("title") ?: "",
    description = getString("description") ?: "",
    status = getString("status") ?: "open",
    assigneeUid = getString("assigneeUid"),
    assigneeName = getString("assigneeName"),
    createdBy = getString("createdBy") ?: "",
    createdByName = getString("createdByName"),
    createdAt = getLong("createdAt") ?: 0L,
    updatedAt = getLong("updatedAt") ?: 0L
)