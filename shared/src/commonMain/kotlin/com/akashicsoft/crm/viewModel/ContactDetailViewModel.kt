package com.akashicsoft.crm.viewModel

import com.akashicsoft.crm.data.local.FakeContactsData
import com.akashicsoft.crm.model.ContactDetailUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ContactDetailViewModel {
    private val viewModelScope = CoroutineScope(Dispatchers.Main)

    private val _uiState = MutableStateFlow(ContactDetailUiState())
    val uiState = _uiState.asStateFlow()

    fun loadContact(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            // Simulate delay
            delay(300)
            
            val contact = FakeContactsData.contacts.value.find { it.id == id }
            
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                contact = contact,
                error = if (contact == null) "Contact not found" else null
            )
        }
    }

    fun toggleFavorite(id: String) {
        val currentContact = _uiState.value.contact ?: return
        if (currentContact.id == id) {
            val updated = currentContact.copy(isFavorite = !currentContact.isFavorite)
            FakeContactsData.updateContact(updated)
            _uiState.value = _uiState.value.copy(contact = updated)
        }
    }

    fun deleteContact(id: String, onDeleted: () -> Unit) {
        FakeContactsData.deleteContact(id)
        onDeleted()
    }
}
