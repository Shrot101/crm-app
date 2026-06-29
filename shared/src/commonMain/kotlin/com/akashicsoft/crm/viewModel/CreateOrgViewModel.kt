package com.akashicsoft.crm.viewModel

import androidx.compose.runtime.mutableStateOf
import com.akashicsoft.crm.data.local.FakeOrgData
import com.akashicsoft.crm.model.Organization
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CreateOrgViewModel {

    // ── Form State ────────────────────────────────────────────────────────────
    var organizationName  = mutableStateOf("")
    var website           = mutableStateOf("")
    var industry          = mutableStateOf("Technology")
    var type              = mutableStateOf("Customer")
    var leadSource        = mutableStateOf("Web")
    var associatedCompany = mutableStateOf("")
    var numberOfEmployees = mutableStateOf("")
    var targetAmount      = mutableStateOf("")
    var address           = mutableStateOf("")
    var city              = mutableStateOf("")
    var state             = mutableStateOf("")
    var assignedTo        = mutableStateOf("Unassigned")

    // ── Streams ───────────────────────────────────────────────────────────────
    private val _isSaved = MutableStateFlow(false)
    val isSaved = _isSaved.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // ── Static Options ────────────────────────────────────────────────────────
    val industryOptions = listOf(
        "Technology", "Manufacturing", "Artificial Intelligence",
        "Healthcare", "Finance", "Retail", "Education", "Real Estate", "Others"
    )
    val typeOptions = listOf("Customer", "Partner", "Prospect", "Vendor", "Competitor")
    val leadSourceOptions = listOf("Web", "Referral", "Conference", "Cold Call", "LinkedIn", "Direct", "Others")
    val agentOptions = listOf("Unassigned", "Alice Smith", "Bob Johnson", "Charlie Davis")

    // ── Reset ─────────────────────────────────────────────────────────────────
    fun resetForm() {
        organizationName.value  = ""
        website.value           = ""
        industry.value          = "Technology"
        type.value              = "Customer"
        leadSource.value        = "Web"
        associatedCompany.value = ""
        numberOfEmployees.value = ""
        targetAmount.value      = ""
        address.value           = ""
        city.value              = ""
        state.value             = ""
        assignedTo.value        = "Unassigned"
        _isSaved.value          = false
        _error.value            = null
    }

    // ── Save ──────────────────────────────────────────────────────────────────
    fun saveOrg() {
        if (organizationName.value.isBlank()) {
            _error.value = "Organization Name is required"
            return
        }

        val empCount = if (numberOfEmployees.value.isBlank()) {
            0
        } else {
            val parsed = numberOfEmployees.value.toIntOrNull()
            if (parsed == null || parsed < 0) {
                _error.value = "Number of Employees must be a non-negative integer"
                return
            }
            parsed
        }

        val targetAmt = if (targetAmount.value.isBlank()) {
            0.0
        } else {
            val parsed = targetAmount.value.toDoubleOrNull()
            if (parsed == null || parsed < 0.0) {
                _error.value = "Target Amount must be a non-negative number"
                return
            }
            parsed
        }

        val size = FakeOrgData.organizations.value.size
        val newOrg = Organization(
            _id               = (size + 1).toString(),
            organizationName  = organizationName.value.trim(),
            orgNo             = "ORG-00${size + 1}",
            website           = website.value.trim(),
            industry          = industry.value,
            type              = type.value,
            leadSource        = leadSource.value,
            associatedCompany = associatedCompany.value.trim().ifBlank { "-" },
            numberOfEmployees = empCount,
            targetAmount      = targetAmt,
            address           = address.value.trim(),
            city              = city.value.trim(),
            state             = state.value.trim(),
            assignedTo        = assignedTo.value,
            createdAt         = "2026-06-29T17:05:00Z"
        )

        FakeOrgData.addOrganization(newOrg)
        _isSaved.value = true
    }
}
