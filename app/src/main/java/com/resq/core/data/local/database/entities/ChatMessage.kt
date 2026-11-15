package com.resq.core.data.local.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "chat_messages",
    primaryKeys = ["messageId", "savedBy"],
    foreignKeys = [
        ForeignKey(
            entity = ChatThreadEntity::class,
            parentColumns = ["threadId", "savedBy"],
            childColumns = ["threadId", "savedBy"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["threadId", "savedBy"]),
        Index(value = ["savedBy", "timestamp"])
    ]
)
data class ChatMessageEntity(
    val messageId: String,
    val threadId: String,
    val senderId: String,
    val text: String,
    val timestamp: Long,
    val isSentByMe: Boolean,
    val deliveryStatus: Int,
    val savedBy: String
)