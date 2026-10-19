package com.example.model

data class AuditEntry(
    val id: String,
    val action: String,
    val actorName: String,
    val actorRole: UserRole,
    val targetEntity: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)
