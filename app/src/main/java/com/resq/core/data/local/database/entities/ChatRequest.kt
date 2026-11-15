package com.resq.core.data.local.database.entities

import androidx.room.Entity

/** Local cache of link requests returned by the backend's list_links event. */
@Entity(
    tableName = "chat_requests",
    primaryKeys = ["linkId", "savedBy"]
)
data class ChatRequestEntity(
    val linkId: Long,
    val otherUserId: Long,
    val otherUsername: String,
    val status: String,
    val direction: String,
    val cachedAt: Long = System.currentTimeMillis(),
    val savedBy: String
)