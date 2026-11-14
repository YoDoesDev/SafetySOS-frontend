package com.resq.features.chatwindow.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.resq.core.data.local.database.dao.ChatThreadDao
import com.resq.core.data.local.database.entities.ChatThreadEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class ChatListView @Inject constructor(
    threadDao: ChatThreadDao
) : ViewModel() {

    private val query = MutableStateFlow("")
    val searchQuery: StateFlow<String> = query.asStateFlow()

    val threads: StateFlow<List<ChatThreadEntity>> = combine(
        threadDao.getAllThreads(), query
    ) { allThreads, search ->
        if (search.isBlank()) allThreads
        else allThreads.filter {
            it.recipientName.contains(search, ignoreCase = true) ||
                    (it.lastMessageText?.contains(search, ignoreCase = true) == true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onSearchChanged(value: String) {
        query.value = value
    }
}
