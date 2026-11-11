package com.resq.core.data.local.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chat_messages",
    foreignKeys = [
        ForeignKey(
            entity = ChatThreadEntity::class,
            parentColumns = ["threadId"],
            childColumns = ["threadId"],
            onDelete = ForeignKey.CASCADE // Deleting a thread wipes its messages automatically
        )
    ],
    indices = [
        Index(value = ["threadId"]), // Fast lookup index for queries by thread
        Index(value = ["timestamp"]) // Optional index for chronological sorting
    ]
)
data class ChatMessageEntity(
    @PrimaryKey
    val messageId: String,          // Unique UUID or server-generated ID
    val threadId: String,           // Foreign key matching ChatThreadEntity.threadId
    val senderId: String,           // User ID of who sent the message
    val text: String,               // Message content
    val timestamp: Long,            // Unix epoch time
    val isSentByMe: Boolean,        // True if local user sent it, false if received
    val deliveryStatus: Int         // e.g., 0 = PENDING, 1 = SENT, 2 = DELIVERED, 3 = READ
)
