package com.resq.features.chatwindow.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.resq.core.data.local.database.dao.ChatMessageDao
import com.resq.core.data.local.database.dao.ChatThreadDao
import com.resq.core.data.local.database.dao.UserDao
import com.resq.core.data.local.database.entities.ChatMessageEntity
import com.resq.core.data.local.database.entities.ChatThreadEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ChatWindowViewModel @Inject constructor(
    private val threadDao: ChatThreadDao,
    private val messageDao: ChatMessageDao,
    private val userDao: UserDao
) : ViewModel() {

    private val _thread = MutableStateFlow<ChatThreadEntity?>(null)
    val thread: StateFlow<ChatThreadEntity?> = _thread.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessageEntity>>(emptyList())
    val messages: StateFlow<List<ChatMessageEntity>> = _messages.asStateFlow()

    private var observeJob: Job? = null
    private var activeThreadId: String? = null

    fun openThread(threadId: String) {
        if (activeThreadId == threadId && observeJob?.isActive == true) return
        activeThreadId = threadId
        observeJob?.cancel()
        _thread.value = null
        _messages.value = emptyList()
        observeJob = viewModelScope.launch {
            val savedBy = userDao.getPhone().orEmpty()
            _thread.value = threadDao.getThreadById(threadId, savedBy)
            threadDao.clearUnreadCount(threadId, savedBy)
            messageDao.getMessagesForThread(threadId, savedBy).collect { storedMessages ->
                _messages.value = storedMessages
            }
        }
    }

}
