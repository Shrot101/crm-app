package com.akashicsoft.crm.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import com.akashicsoft.crm.data.local.FakeLeadsData
import com.akashicsoft.crm.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class EditLeadViewModel {
    
    private var originalLead: LeadListItem? = null
    
    // Form State
    var salutation = mutableStateOf("Mr.")
    var firstName = mutableStateOf("")
    var lastName = mutableStateOf("")
    
    // Multiple Emails
    var emails = mutableStateListOf<EmailContact>()
    
    // Multiple Phones
    var phones = mutableStateListOf<PhoneContact>()
    var landlines = mutableStateListOf<PhoneContact>()
    
    var organizationName = mutableStateOf("")
    var designation = mutableStateOf("")
    var leadSource = mutableStateOf("Others")
    var status = mutableStateOf("New")
    var assignedOwner = mutableStateOf<AssignedOwner?>(null)
    var notes = mutableStateOf("")

    private val _isSaved = MutableStateFlow(false)
    val isSaved = _isSaved.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    val availableOwners = FakeLeadsData.getMockUsers()
    val countryCodes = FakeLeadsData.getCountryCodes()

    val salutationOptions = listOf("Mr.", "Ms.", "Mrs.", "Dr.", "Prof.")
    val sourceOptions = listOf("Website", "Referral", "LinkedIn", "Cold Call", "Conference", "Partner", "Others")
    val statusOptions = listOf("New", "Qualified", "Contacted", "Proposal Sent", "Negotiation", "Won", "Lost")

    fun loadLead(leadId: String) {
        val lead = FakeLeadsData.leads.value.find { it._id == leadId }
        if (lead != null) {
            originalLead = lead
            salutation.value = lead.salutation ?: "Mr."
            firstName.value = lead.firstName ?: ""
            lastName.value = lead.lastName ?: ""
            
            emails.clear()
            lead.emails?.let { emails.addAll(it) } ?: emails.add(EmailContact("", isPrimary = true))
            
            phones.clear()
            landlines.clear()
            lead.phones?.forEach { phone ->
                if (phone.type == "Landline") landlines.add(phone)
                else phones.add(phone)
            }
            if (phones.isEmpty()) phones.add(PhoneContact("", isPrimary = true, type = "Mobile", countryCode = "+91"))
            
            organizationName.value = lead.organizationName ?: ""
            designation.value = lead.designation ?: ""
            leadSource.value = lead.leadSource ?: "Others"
            
            // Format status for display (REPLACE_UNDERSCORES with space)
            status.value = lead.status?.replace("_", " ")?.lowercase()?.capitalize() ?: "New"
            
            assignedOwner.value = lead.assignedOwner
            notes.value = lead.notes ?: ""
            
            _isSaved.value = false
            _error.value = null
        }
    }

    // Dynamic Field Methods
    fun addEmail() = emails.add(EmailContact(""))
    fun removeEmail(index: Int) { if (emails.size > 1) emails.removeAt(index) }
    fun updateEmail(index: Int, value: String) { emails[index] = emails[index].copy(email = value) }

    fun addPhone() = phones.add(PhoneContact("", type = "Mobile", countryCode = "+91"))
    fun removePhone(index: Int) { if (phones.size > 1) phones.removeAt(index) }
    fun updatePhone(index: Int, value: String) { phones[index] = phones[index].copy(phone = value) }
    fun updatePhoneCountryCode(index: Int, code: String) { phones[index] = phones[index].copy(countryCode = code) }

    fun addLandline() = landlines.add(PhoneContact("", type = "Landline", countryCode = "+91"))
    fun removeLandline(index: Int) = landlines.removeAt(index)
    fun updateLandline(index: Int, value: String) { landlines[index] = landlines[index].copy(phone = value) }
    fun updateLandlineCountryCode(index: Int, code: String) { landlines[index] = landlines[index].copy(countryCode = code) }

    fun saveChanges() {
        val lead = originalLead ?: return
        
        if (firstName.value.isBlank()) {
            _error.value = "First Name is required"
            return
        }

        val updatedLead = lead.copy(
            salutation = salutation.value,
            firstName = firstName.value,
            lastName = lastName.value,
            organizationName = organizationName.value,
            designation = designation.value,
            status = status.value.uppercase().replace(" ", "_"),
            leadSource = leadSource.value,
            emails = emails.toList().filter { it.email.isNotBlank() },
            phones = (phones.toList() + landlines.toList()).filter { it.phone.isNotBlank() },
            assignedOwner = assignedOwner.value,
            notes = notes.value
        )

        FakeLeadsData.updateLead(updatedLead)
        _isSaved.value = true
    }
    
    private fun String.capitalize(): String = this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}
