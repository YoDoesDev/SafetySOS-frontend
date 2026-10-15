package com.resq.features.contacts.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.resq.core.data.local.database.entities.ContactsEntity
import com.resq.features.contacts.domain.repositories.AddContactRepository
import com.resq.features.contacts.domain.usecase.AddContactResult
import com.resq.features.contacts.domain.usecase.AddContactUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsView @Inject constructor(
    private val repository: AddContactRepository,
    private val addContactUseCase: AddContactUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isAddDialogVisible = MutableStateFlow(false)
    val isAddDialogVisible: StateFlow<Boolean> = _isAddDialogVisible.asStateFlow()

    private val _dialogError = MutableStateFlow("")
    val dialogError: StateFlow<String> = _dialogError.asStateFlow()

    val contacts: StateFlow<List<ContactsEntity>> = combine(
        repository.getAllContacts(),
        _searchQuery
    ) { list, query ->
        if (query.isBlank()) list
        else list.filter {
            it.savedName.contains(query, ignoreCase = true) ||
            it.phoneNumber.contains(query) ||
            (it.username?.contains(query, ignoreCase = true) == true)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun showAddDialog() {
        _dialogError.value = ""
        _isAddDialogVisible.value = true
    }

    fun dismissAddDialog() {
        _isAddDialogVisible.value = false
        _dialogError.value = ""
    }

    fun onAddContactSubmitted(savedName: String, rawPhone: String, countryCode: String, username: String?) {
    val trimmedName = savedName.trim()
    var cleanedPhone = rawPhone.trim()

    // 1. Validate saved name
    if (trimmedName.isBlank()) {
        _dialogError.value = "Please enter a saved name."
        return
    }

    // 2. Validate raw phone before stripping zeros or adding country code
    if (cleanedPhone.isBlank()) {
        _dialogError.value = "Please enter a valid phone number."
        return
    }

    // 3. Strip leading zeros
    cleanedPhone = cleanedPhone.trimStart('0')

    // 4. Re-check if it became blank after stripping zeros (e.g., user typed "000")
    if (cleanedPhone.isBlank()) {
        _dialogError.value = "Please enter a valid phone number."
        return
    }

    // 5. Length validation check (adjust minimum threshold as needed)
    if (cleanedPhone.length < 7) {
        _dialogError.value = "Phone number is too short."
        return
    }

    // 6. Combine country code and cleaned phone
    val fullPhoneNumber = countryCode + cleanedPhone

    viewModelScope.launch {
        _dialogError.value = ""

        when (val result = addContactUseCase(trimmedName, fullPhoneNumber, username)) {
            is AddContactResult.Success -> {
                dismissAddDialog()
            }
            is AddContactResult.DuplicateError -> {
                _dialogError.value = result.message
            }
            is AddContactResult.GenericError -> {
                _dialogError.value = result.message
            }
        }
    }
}



    fun deleteContact(contact: ContactsEntity) {
        viewModelScope.launch {
            repository.deleteContact(contact)
        }
    }
}
