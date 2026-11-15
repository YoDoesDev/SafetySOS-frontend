package com.resq.features.chatwindow.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.resq.core.data.local.database.dao.ChatRequestDao
import com.resq.core.data.local.database.dao.ChatThreadDao
import com.resq.core.data.local.database.dao.UserDao
import com.resq.core.data.local.database.entities.ChatRequestEntity
import com.resq.core.data.local.database.entities.ChatThreadEntity
import com.resq.core.data.remote.socket.SocketManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

@HiltViewModel
class ChatRequestsViewModel @Inject constructor(
    private val requestDao: ChatRequestDao,
    private val threadDao: ChatThreadDao,
    private val userDao: UserDao,
    private val socketManager: SocketManager
) : ViewModel() {

    val requests: StateFlow<List<ChatRequestEntity>> = flow {
        emit(userDao.getPhone().orEmpty())
    }.flatMapLatest { savedBy ->
        requestDao.observeIncomingPendingRequests(savedBy)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _processingIds = MutableStateFlow<Set<Long>>(emptySet())
    val processingIds: StateFlow<Set<Long>> = _processingIds.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    fun refreshRequests() {
        if (_isRefreshing.value) return
        _isRefreshing.value = true

        val emitted = socketManager.emitWithAck("list_links", JSONObject()) { args ->
            viewModelScope.launch {
                val response = args.firstOrNull().asJsonObject()
                if (response?.optBoolean("success") != true) {
                    _statusMessage.value = response?.optString("reason")
                        ?.takeIf { it.isNotBlank() }
                        ?: "Could not load message requests."
                    _isRefreshing.value = false
                    return@launch
                }

                val savedBy = userDao.getPhone().orEmpty()
                val incomingPending = response.optJSONArray("links").toPendingRequests(savedBy)
                requestDao.clearRequests(savedBy)
                if (incomingPending.isNotEmpty()) requestDao.upsertRequests(incomingPending)
                _isRefreshing.value = false
            }
        }

        if (!emitted) {
            _isRefreshing.value = false
            _statusMessage.value = "Not connected. Reopen the app and try again."
        }
    }

    fun respondToRequest(request: ChatRequestEntity, action: RequestAction) {
        if (request.linkId in _processingIds.value) return
        _processingIds.value = _processingIds.value + request.linkId

        val emitted = socketManager.emitWithAck(
            "link_respond",
            JSONObject()
                .put("linkId", request.linkId)
                .put("action", action.socketValue)
        ) { args ->
            viewModelScope.launch {
                val response = args.firstOrNull().asJsonObject()
                if (response?.optBoolean("success") != true) {
                    _statusMessage.value = response?.optString("reason")
                        ?.takeIf { it.isNotBlank() }
                        ?: "Could not update this request."
                    finishProcessing(request.linkId)
                    return@launch
                }

                if (action == RequestAction.ACCEPT) {
                    val savedBy = userDao.getPhone().orEmpty()
                    createThreadForAcceptedRequest(request, response, savedBy)
                    _statusMessage.value =
                        "Request accepted. You can now message ${request.otherUsername}."
                } else {
                    _statusMessage.value = "Request ignored."
                }

                requestDao.deleteRequest(request.linkId, userDao.getPhone().orEmpty())
                finishProcessing(request.linkId)
            }
        }

        if (!emitted) {
            _statusMessage.value = "Not connected. Reopen the app and try again."
            finishProcessing(request.linkId)
        }
    }

    private suspend fun createThreadForAcceptedRequest(
        request: ChatRequestEntity,
        response: JSONObject,
        savedBy: String
    ) {
        val recipientId = response.optLong("otherUserId", request.otherUserId).toString()
        val existing = threadDao.getThreadByRecipientId(recipientId, savedBy)

        threadDao.upsertThread(
            ChatThreadEntity(
                threadId = existing?.threadId ?: recipientId,
                recipientId = recipientId,
                recipientName = existing?.recipientName ?: request.otherUsername,
                recipientPhoneNumber = existing?.recipientPhoneNumber.orEmpty(),
                lastMessageText = existing?.lastMessageText,
                lastMessageTimestamp = existing?.lastMessageTimestamp ?: 0L,
                lastMessageSenderId = existing?.lastMessageSenderId,
                unreadCount = existing?.unreadCount ?: 0,
                isEmergencyActive = existing?.isEmergencyActive ?: false,
                savedBy = savedBy
            )
        )
    }

    private fun finishProcessing(linkId: Long) {
        _processingIds.value = _processingIds.value - linkId
    }

    private fun Any?.asJsonObject(): JSONObject? = when (this) {
        is JSONObject -> this
        is Map<*, *> -> JSONObject(this)
        is String -> runCatching { JSONObject(this) }.getOrNull()
        else -> null
    }

    private fun JSONArray?.toPendingRequests(savedBy: String): List<ChatRequestEntity> {
        if (this == null) return emptyList()
        val result = mutableListOf<ChatRequestEntity>()

        for (index in 0 until length()) {
            val link = optJSONObject(index) ?: continue
            if (link.optString("status") != "pending" ||
                link.optString("direction") != "incoming"
            ) continue

            val user = link.optJSONObject("user") ?: continue
            val linkId = link.optLong("linkId", -1L)
            val otherUserId = user.optLong("userId", -1L)
            val username = user.optString("username").trim()
            if (linkId < 0L || otherUserId < 0L || username.isBlank()) continue

            result += ChatRequestEntity(
                linkId = linkId,
                otherUserId = otherUserId,
                otherUsername = username,
                status = "pending",
                direction = "incoming",
                savedBy = savedBy
            )
        }

        return result
    }
}

enum class RequestAction(val socketValue: String) {
    ACCEPT("accept"),
    IGNORE("decline")
}