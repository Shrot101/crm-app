package com.akashicsoft.crm.model

import kotlinx.serialization.Serializable

@Serializable
data class DealListItem(
    val _id: String,
    val title: String,
    val dealNumber: String,
    val owner: AssignedOwner,
    val organization: String? = null,
    val rating: String? = null,
    val assignedTo: AssignedOwner? = null,
    val stage: String? = null,
    val product: String? = null,
    val tags: List<String>? = null,
    val dealValue: Double? = null,
    val closeDate: String? = null,
    val category: String? = null,
    val contactPerson: String? = null,
    val currency: String? = "$",
    val finalNotes: String? = null
)

data class DealUiState(
    val isLoading: Boolean = false,
    val deals: List<DealListItem> = emptyList(),
    val error: String? = null
)
