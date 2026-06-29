package com.akashicsoft.crm.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import com.akashicsoft.crm.data.local.FakeLeadsData
import com.akashicsoft.crm.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CreateLeadViewModel {
    
    // Form State
    var salutation = mutableStateOf("Mr.")
    var firstName = mutableStateOf("")
    var lastName = mutableStateOf("")
    
    // Multiple Emails
    var emails = mutableStateListOf(EmailContact("", isPrimary = true))
    
    // Multiple Phones (Mobile/Landline handled via dynamic list)
    var phones = mutableStateListOf(PhoneContact("", isPrimary = true, type = "Mobile", countryCode = "+91"))
    
    // Dedicated Landline (Optional)
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

    fun resetForm() {
        salutation.value = "Mr."
        firstName.value = ""
        lastName.value = ""
        emails.clear()
        emails.add(EmailContact("", isPrimary = true))
        phones.clear()
        phones.add(PhoneContact("", isPrimary = true, type = "Mobile", countryCode = "+91"))
        landlines.clear()
        organizationName.value = ""
        designation.value = ""
        leadSource.value = "Others"
        status.value = "New"
        assignedOwner.value = null
        notes.value = ""
        _isSaved.value = false
        _error.value = null
    }

    fun addEmail() {
        emails.add(EmailContact(""))
    }

    fun removeEmail(index: Int) {
        if (emails.size > 1) emails.removeAt(index)
    }

    fun updateEmail(index: Int, value: String) {
        emails[index] = emails[index].copy(email = value)
    }

    fun addPhone() {
        phones.add(PhoneContact("", type = "Mobile", countryCode = "+91"))
    }

    fun removePhone(index: Int) {
        if (phones.size > 1) phones.removeAt(index)
    }

    fun updatePhone(index: Int, value: String) {
        phones[index] = phones[index].copy(phone = value)
    }

    fun updatePhoneCountryCode(index: Int, code: String) {
        phones[index] = phones[index].copy(countryCode = code)
    }
    
    // Landline Methods
    fun addLandline() {
        landlines.add(PhoneContact("", type = "Landline", countryCode = "+91"))
    }
    
    fun removeLandline(index: Int) {
        landlines.removeAt(index)
    }
    
    fun updateLandline(index: Int, value: String) {
        landlines[index] = landlines[index].copy(phone = value)
    }
    
    fun updateLandlineCountryCode(index: Int, code: String) {
        landlines[index] = landlines[index].copy(countryCode = code)
    }

    fun saveLead() {
        if (firstName.value.isBlank()) {
            _error.value = "First Name is required"
            return
        }
        if (emails.any { it.isPrimary && it.email.isBlank() }) {
             _error.value = "Primary Email is required"
             return
        }
        if (phones.any { it.isPrimary && it.phone.isBlank() }) {
            _error.value = "Primary Mobile is required"
            return
        }

        val allPhones = phones.toList() + landlines.toList()

        val newLead = LeadListItem(
            _id = (FakeLeadsData.leads.value.size + 1).toString(),
            salutation = salutation.value,
            firstName = firstName.value,
            lastName = lastName.value,
            organizationName = organizationName.value,
            designation = designation.value,
            status = status.value.uppercase().replace(" ", "_"),
            leadSource = leadSource.value,
            leadNumber = "LD-${(FakeLeadsData.leads.value.size + 1).toString().padStart(3, '0')}",
            createdAt = "2024-03-21T00:00:00Z",
            emails = emails.toList().filter { it.email.isNotBlank() },
            phones = allPhones.filter { it.phone.isNotBlank() },
            assignedOwner = assignedOwner.value,
            notes = notes.value
        )

        FakeLeadsData.addLead(newLead)
        _isSaved.value = true
    }
}
