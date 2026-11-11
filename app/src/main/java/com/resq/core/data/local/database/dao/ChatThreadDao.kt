package com.resq.core.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.resq.core.data.local.database.entities.ChatThreadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatThreadDao {

    // Observe all chat threads, automatically sorted by newest activity first
    @Query("SELECT * FROM chat_threads ORDER BY lastMessageTimestamp DESC")
    fun getAllThreads(): Flow<List<ChatThreadEntity>>

    // Get a single thread by its ID (useful for checking if a thread already exists)
    @Query("SELECT * FROM chat_threads WHERE threadId = :threadId")
    suspend fun getThreadById(threadId: String): ChatThreadEntity?

    // Insert or replace a thread (e.g., when starting a new chat or updating metadata)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertThread(thread: ChatThreadEntity)

    // Quick update for when a new message arrives and we just need to update the preview snippet
    @Query("""
        UPDATE chat_threads 
        SET lastMessageText = :lastMessageText, 
            lastMessageTimestamp = :timestamp, 
            lastMessageSenderId = :senderId,
            unreadCount = unreadCount + :incrementUnread 
        WHERE threadId = :threadId
    """)
    suspend fun updateLastMessage(
        threadId: String,
        lastMessageText: String,
        timestamp: Long,
        senderId: String,
        incrementUnread: Int
    )

    // Reset unread counter to 0 when the user opens the chat window
    @Query("UPDATE chat_threads SET unreadCount = 0 WHERE threadId = :threadId")
    suspend fun clearUnreadCount(threadId: String)

    // Delete a specific chat thread
    @Query("DELETE FROM chat_threads WHERE threadId = :threadId")
    suspend fun deleteThread(threadId: String)
}
