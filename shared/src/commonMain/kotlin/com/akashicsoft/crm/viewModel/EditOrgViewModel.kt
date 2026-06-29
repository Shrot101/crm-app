package com.akashicsoft.crm.viewModel

import androidx.compose.runtime.mutableStateOf
import com.akashicsoft.crm.data.local.FakeOrgData
import com.akashicsoft.crm.model.Organization
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class EditOrgViewModel {

    private var originalOrg: Organization? = null

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

    // ── Load ──────────────────────────────────────────────────────────────────
    fun loadOrg(orgId: String) {
        val org = FakeOrgData.organizations.value.find { it._id == orgId } ?: return
        originalOrg = org

        organizationName.value  = org.organizationName
        website.value           = org.website
        industry.value          = org.industry
        type.value              = org.type
        leadSource.value        = org.leadSource
        associatedCompany.value = if (org.associatedCompany == "-") "" else org.associatedCompany
        numberOfEmployees.value = if (org.numberOfEmployees == 0) "" else org.numberOfEmployees.toString()
        targetAmount.value      = if (org.targetAmount == 0.0) "" else org.targetAmount.toString()
        address.value           = org.address
        city.value              = org.city
        state.value             = org.state
        assignedTo.value        = org.assignedTo

        _isSaved.value = false
        _error.value   = null
    }

    // ── Save ──────────────────────────────────────────────────────────────────
    fun saveChanges() {
        val org = originalOrg ?: return

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

        val updatedOrg = org.copy(
            organizationName  = organizationName.value.trim(),
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
            assignedTo        = assignedTo.value
        )

        FakeOrgData.updateOrganization(updatedOrg)
        _isSaved.value = true
    }
}
