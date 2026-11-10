package com.resq.features.presets.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.resq.core.data.local.database.dao.UserDao
import com.resq.core.data.local.database.entities.ContactsEntity
import com.resq.core.data.local.database.entities.PresetEntity
import com.resq.features.presets.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PresetsView @Inject constructor(
    private val getContactsUseCase: GetContactsUseCase,
    private val getPresetsUseCase: GetPresetsUseCase,
    private val savePresetUseCase: SavePresetUseCase,
    private val deletePresetUseCase: DeletePresetUseCase,
    private val setDefaultUseCase: SetDefaultUseCase,
    private val userDao: UserDao
) : ViewModel() {

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

    // Error message state
    var presetError by mutableStateOf<String?>(null)
        private set

    // Dialog Visibility States
    var showContactDialog by mutableStateOf(false)
        private set

    var viewingPresetMessage by mutableStateOf<String?>(null)
        private set

    // Reactive list of contacts for the dialog
    val allContacts: StateFlow<List<ContactsEntity>> = flow {
        val userPhone = userDao.getPhone() ?: ""
        emit(userPhone)
    }.flatMapLatest { userPhone ->
        if (userPhone.isNotEmpty()) {
            getContactsUseCase(savedBy = userPhone)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    // Reactive list of presets for the screen
    val allPresets: StateFlow<List<PresetEntity>> = flow {
        val userPhone = userDao.getPhone() ?: ""
        emit(userPhone)
    }.flatMapLatest { userPhone ->
        if (userPhone.isNotEmpty()) {
            getPresetsUseCase(savedBy = userPhone)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

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

    // Full message dialog handlers
    fun openMessageDetails(fullMessage: String) {
        viewingPresetMessage = fullMessage
    }

    fun dismissMessageDetails() {
        viewingPresetMessage = null
    }
    
    /*
    // Button action implementations
    fun onEditPresetClicked(presetId: String) {
        // TODO: Implement edit flow logic
    }
    */
    
    fun onSetDefaultClicked(presetId: Long) {
    viewModelScope.launch {
        val userPhone = userDao.getPhone() ?: return@launch
        setDefaultUseCase(presetId, userPhone) // or repository.setDefault(presetId, userPhone)
    }
}


    fun onDeletePresetClicked(presetId: Long) {
        viewModelScope.launch {
            val userPhone = userDao.getPhone() ?: return@launch
            deletePresetUseCase(presetId = presetId, savedBy = userPhone)
        }
    }

    fun savePreset() {
        val trimmedTitle = title.trim()
        val trimmedMessage = message.trim()

        // 1. Title Validation
        if (trimmedTitle.isEmpty()) {
            presetError = "Preset title cannot be empty."
            return
        }
        if (trimmedTitle.length > 40) {
            presetError = "Title should not exceed 40 characters."
            return
        }

        // 2. Message Validation
        if (trimmedMessage.isEmpty()) {
            presetError = "Emergency message cannot be empty."
            return
        }
        if (trimmedMessage.length < 5) {
            presetError = "Emergency message must be at least 5 characters long."
            return
        }

        // 3. Contact Selection Validation
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

            // Extract all contact phone numbers when notifyAllContacts is true
            val targetContacts = if (notifyAllContacts) {
                allContacts.value.map { it.phoneNumber }
            } else {
                selectedContacts
            }

            val newPreset = PresetEntity(
                id = 0, // Auto-generated by Room
                title = trimmedTitle,
                message = trimmedMessage,
                isLocationEnabled = isLocationEnabled,
                selectedContacts = targetContacts,
                isDefault = false,
                savedBy = userPhone
            )

            savePresetUseCase(newPreset)

            // Reset inputs after saving successfully
            title = ""
            message = ""
            selectedContacts = emptyList()
            notifyAllContacts = true
            isLocationEnabled = true
        }
    }
}
