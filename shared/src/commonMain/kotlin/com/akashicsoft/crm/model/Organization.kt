package com.akashicsoft.crm.model

data class Organization(
    val _id: String,
    val organizationName: String,
    val orgNo: String,
    val website: String,
    val industry: String,
    val type: String,
    val leadSource: String,
    val associatedCompany: String,
    val numberOfEmployees: Int,
    val targetAmount: Double,
    val address: String,
    val city: String,
    val state: String,
    val assignedTo: String = "Unassigned",
    val createdAt: String? = null
)

data class OrgUiState(
    val isLoading: Boolean = false,
    val organizations: List<Organization> = emptyList()
)

data class CreateOrgUiState(
    val isSaved: Boolean = false,
    val error: String? = null
)

