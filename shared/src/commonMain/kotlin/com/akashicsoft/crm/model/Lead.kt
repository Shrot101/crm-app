package com.akashicsoft.crm.model

import kotlinx.serialization.Serializable

@Serializable
data class LeadListItem(
    val _id: String,
    val salutation: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val organizationName: String? = null,
    val designation: String? = null,
    val status: String? = null,
    val leadSource: String? = null,
    val leadNumber: String? = null,
    val createdAt: String? = null,
    val emails: List<EmailContact>? = null,
    val phones: List<PhoneContact>? = null,
    val assignedOwner: AssignedOwner? = null,
    val notes: String? = null
) {
    fun getFullName(): String = listOfNotNull(salutation, firstName, lastName).joinToString(" ").ifBlank { "Untitled Lead" }
    fun getPrimaryEmail(): String = emails?.firstOrNull { it.isPrimary }?.email ?: emails?.firstOrNull()?.email ?: "No Email"
    fun getPrimaryPhone(): String = phones?.firstOrNull { it.isPrimary }?.phone ?: phones?.firstOrNull()?.phone ?: "No Phone"
    fun getOwnerName(): String = assignedOwner?.name ?: "Unassigned"
}

@Serializable
data class EmailContact(
    val email: String,
    val type: String? = null,
    val isPrimary: Boolean = false
)

@Serializable
data class PhoneContact(
    val phone: String,
    val type: String? = null,
    val isPrimary: Boolean = false,
    val countryCode: String? = "+91"
)

@Serializable
data class AssignedOwner(
    val _id: String,
    val name: String
)

data class LeadUiState(
    val isLoading: Boolean = false,
    val leads: List<LeadListItem> = emptyList(),
    val search: String = ""
)
