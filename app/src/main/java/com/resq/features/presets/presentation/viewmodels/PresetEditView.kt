package com.resq.features.presets.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.resq.core.data.local.database.dao.UserDao
import com.resq.core.data.local.database.entities.ContactsEntity
import com.resq.core.data.local.database.entities.PresetEntity
import com.resq.features.presets.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PresetEditView @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPresetsUseCase: GetPresetsUseCase,
    private val savePresetUseCase: SavePresetUseCase, // Used for upsert/update depending on DAO
    private val getContactsUseCase: GetContactsUseCase,
    private val userDao: UserDao
) : ViewModel() {

    private val presetId: Long = savedStateHandle.get<String>("presetId")?.toLongOrNull() ?: 0L

    var title by mutableStateOf("")
        private set

    var message by mutableStateOf("")
        private set

    var isLocationEnabled by mutableStateOf(true)
        private set

    var notifyAllContacts by mutableStateOf(true)
        private set

    var selectedContacts by mutableStateOf<List<String>>(emptyList())
        private set

    var presetError by mutableStateOf<String?>(null)
        private set

    var showContactDialog by mutableStateOf(false)
        private set

    private var originalPreset: PresetEntity? = null

    init {
        loadPresetData()
    }

    val allContacts: StateFlow<List<ContactsEntity>> = flow {
        val userPhone = userDao.getPhone() ?: ""
        emit(userPhone)
    }.flatMapLatest { userPhone ->
        if (userPhone.isNotEmpty()) getContactsUseCase(savedBy = userPhone) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun loadPresetData() {
        viewModelScope.launch {
            val userPhone = userDao.getPhone() ?: return@launch
            getPresetsUseCase(savedBy = userPhone).collect { presets ->
                val target = presets.find { it.id == presetId }
                if (target != null) {
                    originalPreset = target
                    title = target.title
                    message = target.message
                    isLocationEnabled = target.isLocationEnabled
                    // Check if contacts match all available or are custom
                    selectedContacts = target.selectedContacts
                    // If target contacts list is not empty, we can check how notifyAll was meant
                    notifyAllContacts = target.selectedContacts.isEmpty() 
                }
            }
        }
    }

    fun onTitleChange(newTitle: String) {
        title = newTitle
        if (presetError != null) presetError = null
    }

    fun onMessageChange(newMessage: String) {
        message = newMessage
        if (presetError != null) presetError = null
    }

    fun onLocationChanged(enabled: Boolean) {
        isLocationEnabled = enabled
    }

    fun onNotifyAllChanged(notifyAll: Boolean) {
        notifyAllContacts = notifyAll
        if (presetError != null) presetError = null
    }

    fun onSelectContactsClick() {
        showContactDialog = true
    }

    fun dismissContactDialog() {
        showContactDialog = false
    }

    fun onContactsSelected(contacts: List<String>) {
        selectedContacts = contacts
        showContactDialog = false
        if (presetError != null) presetError = null
    }

    fun updatePreset(onSuccess: () -> Unit) {
        val trimmedTitle = title.trim()
        val trimmedMessage = message.trim()

        if (trimmedTitle.isEmpty()) {
            presetError = "Preset title cannot be empty."
            return
        }
        if (trimmedTitle.length > 40) {
            presetError = "Title should not exceed 40 characters."
            return
        }
        if (trimmedMessage.isEmpty()) {
            presetError = "Emergency message cannot be empty."
            return
        }
        if (trimmedMessage.length < 5) {
            presetError = "Emergency message must be at least 5 characters long."
            return
        }
        if (!notifyAllContacts && selectedContacts.isEmpty()) {
            presetError = "Please select at least one trusted contact."
            return
        }

        presetError = null

        viewModelScope.launch {
            val userPhone = userDao.getPhone() ?: ""
            if (userPhone.isBlank()) {
                presetError = "User phone number not found."
                return@launch
            }

            val targetContacts = if (notifyAllContacts) {
                allContacts.value.map { it.phoneNumber }
            } else {
                selectedContacts
            }

            val updatedPreset = PresetEntity(
                id = presetId,
                title = trimmedTitle,
                message = trimmedMessage,
                isLocationEnabled = isLocationEnabled,
                selectedContacts = targetContacts,
                isDefault = originalPreset?.isDefault ?: false,
                savedBy = userPhone
            )

            savePresetUseCase(updatedPreset)
            onSuccess()
        }
    }
}
