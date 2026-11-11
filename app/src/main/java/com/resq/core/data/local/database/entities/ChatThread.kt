package com.resq.core.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_threads")
data class ChatThreadEntity(
    @PrimaryKey
    val threadId: String,
    val recipientId: String,
    val recipientName: String,
    val recipientPhoneNumber: String,
    val lastMessageText: String?,
    val lastMessageTimestamp: Long,
    val lastMessageSenderId: String?,
    val unreadCount: Int = 0,
    val isEmergencyActive: Boolean = false
)
