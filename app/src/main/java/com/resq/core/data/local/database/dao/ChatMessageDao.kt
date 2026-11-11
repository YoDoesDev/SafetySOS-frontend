package com.resq.core.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.resq.core.data.local.database.entities.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {

    // Observe all messages for a specific thread in real-time, sorted oldest to newest (standard chat layout)
    @Query("SELECT * FROM chat_messages WHERE threadId = :threadId ORDER BY timestamp ASC")
    fun getMessagesForThread(threadId: String): Flow<List<ChatMessageEntity>>

    // Insert a single message (e.g., user hits send or incoming socket message arrives)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    // Insert a batch of messages (useful for loading history/syncing)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessageEntity>)

    // Update message status (e.g., PENDING -> SENT or DELIVERED)
    @Query("UPDATE chat_messages SET deliveryStatus = :status WHERE messageId = :messageId")
    suspend fun updateMessageStatus(messageId: String, status: Int)

    // Find all messages that are still stuck in PENDING status (used for offline queue sync when socket reconnects)
    @Query("SELECT * FROM chat_messages WHERE deliveryStatus = 0 ORDER BY timestamp ASC")
    suspend fun getPendingMessages(): List<ChatMessageEntity>

    // Delete a specific message
    @Query("DELETE FROM chat_messages WHERE messageId = :messageId")
    suspend fun deleteMessage(messageId: String)

    // Wipe all messages for a thread
    @Query("DELETE FROM chat_messages WHERE threadId = :threadId")
    suspend fun deleteMessagesForThread(threadId: String)
}
