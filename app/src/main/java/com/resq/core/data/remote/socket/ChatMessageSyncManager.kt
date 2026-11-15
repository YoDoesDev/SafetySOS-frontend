package com.resq.core.data.remote.socket

import androidx.room.withTransaction
import com.resq.core.data.local.database.Database
import com.resq.core.data.local.database.dao.ChatMessageDao
import com.resq.core.data.local.database.dao.ChatThreadDao
import com.resq.core.data.local.database.dao.UserDao
import com.resq.core.data.local.database.entities.ChatMessageEntity
import com.resq.core.data.local.database.entities.ChatThreadEntity
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

/** Persists live and queued socket messages before acknowledging delivery to the backend. */
@Singleton
class ChatMessageSyncManager @Inject constructor(
    private val database: Database,
    private val threadDao: ChatThreadDao,
    private val messageDao: ChatMessageDao,
    private val userDao: UserDao,
    private val socketManager: SocketManager
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Volatile
    private var started = false

    private val pendingIdsInFlight = mutableSetOf<String>()

    @Synchronized
    fun start() {
        if (started) return

        socketManager.connect()

        socketManager.on("new_message") { args ->
            val message = args.firstOrNull().asJsonObject() ?: return@on
            scope.launch {
                val savedId = persistIncomingMessage(message) ?: return@launch
                acknowledgeMessages(JSONArray().put(savedId))
            }
        }

        socketManager.on("link_update") { args ->
            val update = args.firstOrNull().asJsonObject() ?: return@on
            if (update.optString("status") == "accepted") {
                scope.launch {
                    markRequestAccepted(update)
                    update.optJSONObject("user")?.optString("userId")?.let {
                        flushPendingForRecipient(it)
                    }
                }
            }
        }

        socketManager.on("connect") {
            syncPendingMessages()
            reconcileAcceptedLinks()
        }

        started = true
        if (socketManager.isConnected()) {
            syncPendingMessages()
            reconcileAcceptedLinks()
        }
    }

    @Synchronized
    fun stop() {
        if (!started) return
        socketManager.off("new_message")
        socketManager.off("link_update")
        socketManager.off("connect")
        started = false
    }

    /** Recheck link status after adding a locally queued draft to a thread. */
    fun onPendingOutboundQueued(recipientId: String) {
        if (!socketManager.isConnected()) return

        socketManager.emitWithAck("list_links", JSONObject()) { args ->
            val response = args.firstOrNull().asJsonObject() ?: return@emitWithAck
            if (!response.optBoolean("success")) return@emitWithAck
            val links = response.optJSONArray("links") ?: return@emitWithAck

            for (index in 0 until links.length()) {
                val link = links.optJSONObject(index) ?: continue
                val userId = link.optJSONObject("user")?.optString("userId") ?: continue

                if (userId == recipientId && link.optString("status") == "accepted") {
                    scope.launch { flushPendingForRecipient(recipientId) }
                    return@emitWithAck
                }
            }
        }
    }

    private fun syncPendingMessages() {
        if (!socketManager.isConnected()) return

        socketManager.emitWithAck("pending_messages", JSONObject()) { args ->
            val response = args.firstOrNull().asJsonObject() ?: return@emitWithAck
            if (!response.optBoolean("success")) return@emitWithAck
            val messages = response.optJSONArray("messages") ?: return@emitWithAck

            scope.launch {
                val savedIds = JSONArray()
                for (index in 0 until messages.length()) {
                    val message = messages.optJSONObject(index) ?: continue
                    persistIncomingMessage(message)?.let(savedIds::put)
                }
                if (savedIds.length() > 0) acknowledgeMessages(savedIds)
            }
        }
    }

    private suspend fun persistIncomingMessage(message: JSONObject): String? {
        val messageId = message.optString("id").takeIf { it.isNotBlank() } ?: return null
        val senderId = message.optLong("from", -1L)
            .takeIf { it >= 0L }
            ?.toString()
            ?: return null
        val senderName = message.optString("fromUsername").ifBlank { "ResQ user" }
        val text = message.optString("text").takeIf { it.isNotBlank() } ?: return null
        val timestamp = message.optLong("ts", System.currentTimeMillis())
        val savedBy = userDao.getPhone().orEmpty()

        database.withTransaction {
            if (messageDao.hasMessage(messageId, savedBy)) return@withTransaction

            val existing = threadDao.getThreadByRecipientId(senderId, savedBy)
            val threadId = existing?.threadId ?: senderId

            threadDao.upsertThread(
                ChatThreadEntity(
                    threadId = threadId,
                    recipientId = senderId,
                    recipientName = existing?.recipientName ?: senderName,
                    recipientPhoneNumber = existing?.recipientPhoneNumber.orEmpty(),
                    lastMessageText = text,
                    lastMessageTimestamp = timestamp,
                    lastMessageSenderId = senderId,
                    unreadCount = (existing?.unreadCount ?: 0) + 1,
                    isEmergencyActive = existing?.isEmergencyActive ?: false,
                    savedBy = savedBy
                )
            )

            messageDao.insertMessage(
                ChatMessageEntity(
                    messageId = messageId,
                    threadId = threadId,
                    senderId = senderId,
                    text = text,
                    timestamp = timestamp,
                    isSentByMe = false,
                    deliveryStatus = 2,
                    savedBy = savedBy
                )
            )
        }

        return messageId
    }

    private fun acknowledgeMessages(ids: JSONArray) {
        socketManager.emit("message_ack", JSONObject().put("ids", ids))
    }

    private suspend fun markRequestAccepted(update: JSONObject) {
        val user = update.optJSONObject("user") ?: return
        val recipientId = user.optString("userId").takeIf { it.isNotBlank() } ?: return
        val username = user.optString("username").ifBlank { "ResQ user" }
        val savedBy = userDao.getPhone().orEmpty()
        val existing = threadDao.getThreadByRecipientId(recipientId, savedBy)

        if (existing != null && existing.lastMessageText == "Chat request sent") {
            threadDao.upsertThread(
                existing.copy(
                    lastMessageText = "Chat request accepted",
                    lastMessageTimestamp = System.currentTimeMillis()
                )
            )
        } else if (existing == null) {
            threadDao.upsertThread(
                ChatThreadEntity(
                    threadId = recipientId,
                    recipientId = recipientId,
                    recipientName = username,
                    recipientPhoneNumber = "",
                    lastMessageText = "Chat request accepted",
                    lastMessageTimestamp = System.currentTimeMillis(),
                    lastMessageSenderId = null,
                    savedBy = savedBy
                )
            )
        }
    }

    private fun reconcileAcceptedLinks() {
        socketManager.emitWithAck("list_links", JSONObject()) { args ->
            val response = args.firstOrNull().asJsonObject() ?: return@emitWithAck
            if (!response.optBoolean("success")) return@emitWithAck
            val links = response.optJSONArray("links") ?: return@emitWithAck

            scope.launch {
                for (index in 0 until links.length()) {
                    val link = links.optJSONObject(index) ?: continue
                    if (link.optString("status") != "accepted") continue

                    val user = link.optJSONObject("user") ?: continue
                    val userId = user.optString("userId").takeIf { it.isNotBlank() } ?: continue

                    markRequestAccepted(JSONObject().put("user", user))
                    flushPendingForRecipient(userId)
                }
            }
        }
    }

    private suspend fun flushPendingForRecipient(recipientId: String) {
        if (!socketManager.isConnected()) return

        val savedBy = userDao.getPhone().orEmpty()
        val thread = threadDao.getThreadByRecipientId(recipientId, savedBy) ?: return
        val pendingMessages = messageDao
            .getPendingMessagesForThread(thread.threadId, savedBy)
            .filter { it.isSentByMe }

        for (message in pendingMessages) {
            val alreadyInFlight = synchronized(pendingIdsInFlight) {
                !pendingIdsInFlight.add(message.messageId)
            }
            if (alreadyInFlight) continue

            val emitted = socketManager.emitWithAck(
                "send_message",
                JSONObject()
                    .put("to", recipientId.toLongOrNull() ?: recipientId)
                    .put("clientId", message.messageId)
                    .put("text", message.text)
            ) { args ->
                scope.launch {
                    val response = args.firstOrNull().asJsonObject()
                    if (response?.optBoolean("success") == true) {
                        messageDao.updateMessageStatus(message.messageId, savedBy, 1)

                        val currentThread =
                            threadDao.getThreadById(thread.threadId, savedBy)

                        if (currentThread != null && (
                                    currentThread.lastMessageText == "Chat request accepted" ||
                                            currentThread.lastMessageTimestamp <= message.timestamp
                                    )
                        ) {
                            threadDao.upsertThread(
                                currentThread.copy(
                                    lastMessageText = message.text,
                                    lastMessageTimestamp = message.timestamp,
                                    lastMessageSenderId = "me"
                                )
                            )
                        }
                    }

                    synchronized(pendingIdsInFlight) {
                        pendingIdsInFlight.remove(message.messageId)
                    }
                }
            }

            if (!emitted) {
                synchronized(pendingIdsInFlight) {
                    pendingIdsInFlight.remove(message.messageId)
                }
                return
            }
        }
    }

    private fun Any?.asJsonObject(): JSONObject? = when (this) {
        is JSONObject -> this
        is Map<*, *> -> JSONObject(this)
        is String -> runCatching { JSONObject(this) }.getOrNull()
        else -> null
    }
}