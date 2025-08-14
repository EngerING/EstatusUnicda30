package com.example.estatusunicda30.core.model
data class VoteAverages(
    val count: Int = 0,
    val classroomsAvg: Double = 0.0,
    val bathroomsAvg: Double = 0.0,
    val acAvg: Double = 0.0
)

data class VoteRating(
    val uid: String = "",
    val classrooms: Int = 3,   // 1..5
    val bathrooms: Int = 3,    // 1..5
    val ac: Int = 3,           // 1..5
    val createdAt: Long = System.currentTimeMillis()
)