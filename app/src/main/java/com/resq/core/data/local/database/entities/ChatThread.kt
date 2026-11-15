package com.resq.core.data.local.database.entities

import androidx.room.Entity

@Entity(
    tableName = "chat_threads",
    primaryKeys = ["threadId", "savedBy"]
)
data class ChatThreadEntity(
    val threadId: String,
    val recipientId: String,
    val recipientName: String,
    val recipientPhoneNumber: String,
    val lastMessageText: String?,
    val lastMessageTimestamp: Long,
    val lastMessageSenderId: String?,
    val unreadCount: Int = 0,
    val isEmergencyActive: Boolean = false,
    val savedBy: String
)