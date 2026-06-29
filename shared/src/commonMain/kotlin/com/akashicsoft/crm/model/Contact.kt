package com.akashicsoft.crm.model

import kotlinx.serialization.Serializable

@Serializable
data class Contact(
    val id: String,
    val name: String,
    val salutation: String? = null,
    val designation: String,
    val organization: String,
    val email: String,
    val mobileNumber: String,
    val landlineNumber: String? = null,
    val department: String? = null,
    val contactNo: String,
    val source: String,
    val isFavorite: Boolean = false,
    val createdAt: String? = null,
    val lastActivity: String? = null,
    // Extended list fields — populated by CreateContact; existing screens use the scalar fields above
    val emails: List<String> = emptyList(),
    val phones: List<String> = emptyList(),
    val landlineNumbers: List<String> = emptyList()
) {
    val initials: String
        get() = name.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first().uppercase() }
            .joinToString("")
}

data class ContactUiState(
    val isLoading: Boolean = false,
    val contacts: List<Contact> = emptyList(),
    val groupedContacts: Map<Char, List<Contact>> = emptyMap(),
    val totalContacts: Int = 0,
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val error: String? = null
)

data class ContactDetailUiState(
    val isLoading: Boolean = false,
    val contact: Contact? = null,
    val error: String? = null
)
