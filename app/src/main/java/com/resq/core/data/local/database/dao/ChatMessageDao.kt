package com.resq.core.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.resq.core.data.local.database.entities.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {

    @Query("""
        SELECT * FROM chat_messages
        WHERE threadId = :threadId AND savedBy = :savedBy
        ORDER BY timestamp ASC
    """)
    fun getMessagesForThread(
        threadId: String,
        savedBy: String
    ): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM chat_messages
            WHERE messageId = :messageId AND savedBy = :savedBy
        )
    """)
    suspend fun hasMessage(messageId: String, savedBy: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessageEntity>)

    @Query("""
        UPDATE chat_messages SET deliveryStatus = :status
        WHERE messageId = :messageId AND savedBy = :savedBy
    """)
    suspend fun updateMessageStatus(messageId: String, savedBy: String, status: Int)

    @Query("""
        SELECT * FROM chat_messages
        WHERE savedBy = :savedBy AND deliveryStatus = 0
        ORDER BY timestamp ASC
    """)
    suspend fun getPendingMessages(savedBy: String): List<ChatMessageEntity>

    @Query("""
        SELECT * FROM chat_messages
        WHERE threadId = :threadId AND savedBy = :savedBy AND deliveryStatus = 0
        ORDER BY timestamp ASC
    """)
    suspend fun getPendingMessagesForThread(
        threadId: String,
        savedBy: String
    ): List<ChatMessageEntity>

    @Query("DELETE FROM chat_messages WHERE messageId = :messageId AND savedBy = :savedBy")
    suspend fun deleteMessage(messageId: String, savedBy: String)

    @Query("DELETE FROM chat_messages WHERE threadId = :threadId AND savedBy = :savedBy")
    suspend fun deleteMessagesForThread(threadId: String, savedBy: String)
}