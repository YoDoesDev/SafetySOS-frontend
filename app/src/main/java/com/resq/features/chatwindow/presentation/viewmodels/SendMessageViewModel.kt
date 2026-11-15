package com.resq.features.chatwindow.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.resq.core.data.local.database.Database
import com.resq.core.data.local.database.dao.ChatMessageDao
import com.resq.core.data.local.database.dao.ChatThreadDao
import com.resq.core.data.local.database.dao.UserDao
import com.resq.core.data.local.database.entities.ChatMessageEntity
import com.resq.core.data.local.database.entities.ChatThreadEntity
import com.resq.core.data.local.database.entities.ContactsEntity
import com.resq.core.data.remote.socket.ChatMessageSyncManager
import com.resq.core.data.remote.socket.SocketManager
import com.resq.features.contacts.domain.repositories.AddContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

@HiltViewModel
class SendMessageViewModel @Inject constructor(
    contactRepository: AddContactRepository,
    private val database: Database,
    private val threadDao: ChatThreadDao,
    private val messageDao: ChatMessageDao,
    private val userDao: UserDao,
    private val socketManager: SocketManager,
    private val chatMessageSyncManager: ChatMessageSyncManager
) : ViewModel() {

    val contacts: StateFlow<List<ContactsEntity>> = contactRepository.getAllContacts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _existingThread = MutableStateFlow<ChatThreadEntity?>(null)
    val existingThread: StateFlow<ChatThreadEntity?> = _existingThread.asStateFlow()

    fun loadExistingThread(threadId: String) {
        viewModelScope.launch {
            val savedBy = userDao.getPhone().orEmpty()
            _existingThread.value = threadDao.getThreadById(threadId, savedBy)
        }
    }

    fun clearStatus() {
        _statusMessage.value = null
    }

    fun sendDraft(contact: ContactsEntity?, text: String) {
        val message = text.trim()
        if (contact == null) {
            _statusMessage.value = "Choose a contact first."
            return
        }
        if (message.isBlank()) {
            _statusMessage.value = "Write a message first."
            return
        }

        val username = contact.username?.trim().orEmpty()
        if (!contact.existsOnResQ || username.isBlank()) {
            _statusMessage.value = "This contact needs a ResQ username to chat."
            return
        }
        if (_isSending.value) return

        _isSending.value = true
        _statusMessage.value = null

        val requestSent = socketManager.emitWithAck("list_links", JSONObject()) { args ->
            viewModelScope.launch {
                val response = args.firstOrNull().asJsonObject()
                if (response?.optBoolean("success") != true) {
                    finishWithError(
                        response?.optString("reason") ?: "Could not load chat connections."
                    )
                    return@launch
                }

                val link = response.optJSONArray("links").findLink(username)
                if (link == null) {
                    requestLink(contact, username, message)
                    return@launch
                }

                val user = link.optJSONObject("user")
                val recipientId = user?.optString("userId").orEmpty()

                when (link.optString("status")) {
                    "accepted" -> sendToRecipient(
                        recipientId = recipientId,
                        username = username,
                        contact = contact,
                        text = message
                    )

                    "pending" -> {
                        if (link.optString("direction") == "incoming") {
                            requestLink(contact, username, message)
                        } else {
                            queueDraftUntilAccepted(recipientId, contact, message)
                        }
                    }

                    else -> finishWithError("This chat connection is unavailable.")
                }
            }
        }

        if (!requestSent) finishWithError("Not connected. Reopen the app and try again.")
    }

    fun sendToExistingThread(threadId: String, text: String) {
        val message = text.trim()
        if (message.isBlank()) {
            _statusMessage.value = "Write a message first."
            return
        }
        if (_isSending.value) return

        viewModelScope.launch {
            val savedBy = userDao.getPhone().orEmpty()
            val thread = threadDao.getThreadById(threadId, savedBy)
            if (thread == null) {
                _statusMessage.value = "This conversation is no longer available."
                return@launch
            }

            _isSending.value = true
            _statusMessage.value = null
            sendToRecipient(
                recipientId = thread.recipientId,
                username = thread.recipientName,
                contact = null,
                text = message,
                existingThread = thread
            )
        }
    }

    private fun requestLink(contact: ContactsEntity, username: String, draft: String) {
        val emitted = socketManager.emitWithAck(
            "link_request",
            JSONObject().put("username", username)
        ) { args ->
            viewModelScope.launch {
                val response = args.firstOrNull().asJsonObject()
                if (response?.optBoolean("success") != true) {
                    val reason = response?.optString("reason").orEmpty()
                    if (reason.contains("already linked", ignoreCase = true)) {
                        loadLinksThenSend(contact, username, draft)
                    } else {
                        finishWithError(reason.ifBlank { "Could not send chat request." })
                    }
                    return@launch
                }

                val recipientId = response.optString("otherUserId")
                if (response.optString("status") == "accepted") {
                    sendToRecipient(recipientId, username, contact, draft)
                } else {
                    queueDraftUntilAccepted(recipientId, contact, draft)
                }
            }
        }

        if (!emitted) finishWithError("Not connected. Reopen the app and try again.")
    }

    private fun loadLinksThenSend(
        contact: ContactsEntity,
        username: String,
        draft: String
    ) {
        val emitted = socketManager.emitWithAck("list_links", JSONObject()) { args ->
            viewModelScope.launch {
                val response = args.firstOrNull().asJsonObject()
                val link = response?.optJSONArray("links").findLink(username)
                val recipientId = link?.optJSONObject("user")?.optString("userId").orEmpty()

                if (link?.optString("status") == "accepted") {
                    sendToRecipient(recipientId, username, contact, draft)
                } else if (
                    link != null &&
                    link.optString("status") == "pending" &&
                    link.optString("direction") == "outgoing"
                ) {
                    queueDraftUntilAccepted(recipientId, contact, draft)
                } else {
                    finishWithError("The chat request is still pending.")
                }
            }
        }

        if (!emitted) finishWithError("Not connected. Reopen the app and try again.")
    }

    private fun sendToRecipient(
        recipientId: String,
        username: String,
        contact: ContactsEntity?,
        text: String,
        existingThread: ChatThreadEntity? = null
    ) {
        if (recipientId.isBlank() || recipientId == "null") {
            finishWithError("Could not identify this ResQ user.")
            return
        }

        val clientId = UUID.randomUUID().toString()

        viewModelScope.launch {
            try {
                val savedBy = userDao.getPhone().orEmpty()
                val old = existingThread ?: threadDao.getThreadByRecipientId(recipientId, savedBy)
                val now = System.currentTimeMillis()
                val threadId = old?.threadId ?: recipientId

                database.withTransaction {
                    threadDao.upsertThread(
                        ChatThreadEntity(
                            threadId = threadId,
                            recipientId = recipientId,
                            recipientName = old?.recipientName ?: contact?.savedName ?: username,
                            recipientPhoneNumber = old?.recipientPhoneNumber
                                ?: contact?.phoneNumber.orEmpty(),
                            lastMessageText = text,
                            lastMessageTimestamp = now,
                            lastMessageSenderId = "me",
                            unreadCount = old?.unreadCount ?: 0,
                            isEmergencyActive = old?.isEmergencyActive ?: false,
                            savedBy = savedBy
                        )
                    )
                    messageDao.insertMessage(
                        ChatMessageEntity(
                            messageId = clientId,
                            threadId = threadId,
                            senderId = "me",
                            text = text,
                            timestamp = now,
                            isSentByMe = true,
                            deliveryStatus = 0,
                            savedBy = savedBy
                        )
                    )
                }

                sendStoredMessage(recipientId, threadId, clientId, text, savedBy)
            } catch (error: Exception) {
                finishWithError(error.message ?: "Could not save this message locally.")
            }
        }
    }

    private fun sendStoredMessage(
        recipientId: String,
        threadId: String,
        clientId: String,
        text: String,
        savedBy: String
    ) {
        val emitted = socketManager.emitWithAck(
            "send_message",
            JSONObject()
                .put("to", recipientId.toLongOrNull() ?: recipientId)
                .put("clientId", clientId)
                .put("text", text)
        ) { args ->
            viewModelScope.launch {
                val response = args.firstOrNull().asJsonObject()
                if (response?.optBoolean("success") == true) {
                    messageDao.updateMessageStatus(clientId, savedBy, 1)
                    finishWithMessage("Message sent.")
                } else {
                    finishWithError(
                        response?.optString("reason")
                            ?: "Message is saved locally and will retry when the connection is ready."
                    )
                    chatMessageSyncManager.onPendingOutboundQueued(recipientId)
                }
            }
        }

        if (!emitted) {
            chatMessageSyncManager.onPendingOutboundQueued(recipientId)
            finishWithMessage("Message saved. It will send when the connection is ready.")
        }
    }

    private fun queueDraftUntilAccepted(
        recipientId: String,
        contact: ContactsEntity,
        draft: String
    ) {
        if (recipientId.isBlank() || recipientId == "null") {
            finishWithError("Could not identify this ResQ user.")
            return
        }

        viewModelScope.launch {
            try {
                val savedBy = userDao.getPhone().orEmpty()
                val existing = threadDao.getThreadByRecipientId(recipientId, savedBy)
                val threadId = existing?.threadId ?: recipientId
                val now = System.currentTimeMillis()
                val clientId = UUID.randomUUID().toString()

                database.withTransaction {
                    threadDao.upsertThread(
                        ChatThreadEntity(
                            threadId = threadId,
                            recipientId = recipientId,
                            recipientName = existing?.recipientName ?: contact.savedName,
                            recipientPhoneNumber = existing?.recipientPhoneNumber
                                ?: contact.phoneNumber,
                            lastMessageText = "Chat request sent",
                            lastMessageTimestamp = now,
                            lastMessageSenderId = existing?.lastMessageSenderId,
                            unreadCount = existing?.unreadCount ?: 0,
                            isEmergencyActive = existing?.isEmergencyActive ?: false,
                            savedBy = savedBy
                        )
                    )
                    messageDao.insertMessage(
                        ChatMessageEntity(
                            messageId = clientId,
                            threadId = threadId,
                            senderId = "me",
                            text = draft,
                            timestamp = now,
                            isSentByMe = true,
                            deliveryStatus = 0,
                            savedBy = savedBy
                        )
                    )
                }

                chatMessageSyncManager.onPendingOutboundQueued(recipientId)
                finishWithMessage("Chat request sent. Your message will send after they accept.")
            } catch (error: Exception) {
                finishWithError(error.message ?: "Could not save your draft.")
            }
        }
    }

    private fun finishWithError(message: String) {
        _statusMessage.value = message
        _isSending.value = false
    }

    private fun finishWithMessage(message: String) {
        _statusMessage.value = message
        _isSending.value = false
    }

    private fun Any?.asJsonObject(): JSONObject? = when (this) {
        is JSONObject -> this
        is Map<*, *> -> JSONObject(this)
        is String -> runCatching { JSONObject(this) }.getOrNull()
        else -> null
    }

    private fun JSONArray?.findLink(username: String): JSONObject? {
        if (this == null) return null
        for (index in 0 until length()) {
            val link = optJSONObject(index) ?: continue
            if (link.optJSONObject("user")
                    ?.optString("username")
                    ?.equals(username, ignoreCase = true) == true
            ) {
                return link
            }
        }
        return null
    }
}