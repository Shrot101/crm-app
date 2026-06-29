package com.akashicsoft.crm.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.akashicsoft.crm.data.local.FakeContactsData
import com.akashicsoft.crm.model.Contact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Holds one phone/landline entry (country code + number) for the Create Contact form. */
data class ContactPhoneEntry(
    val countryCode: String = "+91",
    val number: String = ""
)

class CreateContactViewModel {

    // ── Form State ────────────────────────────────────────────────────────────
    var salutation   = mutableStateOf("Mr.")
    var name         = mutableStateOf("")
    var emails       = mutableStateListOf("")                   // [0] = primary email
    var phones       = mutableStateListOf(ContactPhoneEntry())  // [0] = primary mobile
    var landlines    = mutableStateListOf<ContactPhoneEntry>()  // optional, starts empty
    var organization = mutableStateOf("")
    var designation  = mutableStateOf("")
    var department   = mutableStateOf("")
    var source       = mutableStateOf("Website")
    var isFavorite   = mutableStateOf(false)

    // ── Streams ───────────────────────────────────────────────────────────────
    private val _isSaved = MutableStateFlow(false)
    val isSaved = _isSaved.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // ── Static Options ────────────────────────────────────────────────────────
    val salutationOptions = listOf("Mr.", "Ms.", "Mrs.", "Dr.", "Prof.")
    val sourceOptions = listOf(
        "Website", "Referral", "LinkedIn", "Cold Call",
        "Conference", "Direct", "Others"
    )
    val countryCodes = FakeContactsData.getCountryCodes()

    // ── Email Actions ─────────────────────────────────────────────────────────
    fun addEmail()                              { emails.add("") }
    fun removeEmail(index: Int)                 { if (emails.size > 1) emails.removeAt(index) }
    fun updateEmail(index: Int, value: String)  { emails[index] = value }

    // ── Phone Actions ─────────────────────────────────────────────────────────
    fun addPhone()                                   { phones.add(ContactPhoneEntry()) }
    fun removePhone(index: Int)                      { if (phones.size > 1) phones.removeAt(index) }
    fun updatePhone(index: Int, number: String)      { phones[index] = phones[index].copy(number = number) }
    fun updatePhoneCode(index: Int, code: String)    { phones[index] = phones[index].copy(countryCode = code) }

    // ── Landline Actions ──────────────────────────────────────────────────────
    fun addLandline()                                   { landlines.add(ContactPhoneEntry()) }
    fun removeLandline(index: Int)                      { landlines.removeAt(index) }
    fun updateLandline(index: Int, number: String)      { landlines[index] = landlines[index].copy(number = number) }
    fun updateLandlineCode(index: Int, code: String)    { landlines[index] = landlines[index].copy(countryCode = code) }

    // ── Reset ─────────────────────────────────────────────────────────────────
    fun resetForm() {
        salutation.value   = "Mr."
        name.value         = ""
        emails.clear();   emails.add("")
        phones.clear();   phones.add(ContactPhoneEntry())
        landlines.clear()
        organization.value = ""
        designation.value  = ""
        department.value   = ""
        source.value       = "Website"
        isFavorite.value   = false
        _isSaved.value     = false
        _error.value       = null
    }

    // ── Save ──────────────────────────────────────────────────────────────────
    fun saveContact() {
        if (name.value.isBlank()) {
            _error.value = "Full Name is required"; return
        }
        if (emails.firstOrNull()?.isBlank() != false) {
            _error.value = "Email Address is required"; return
        }
        if (phones.firstOrNull()?.number?.isBlank() != false) {
            _error.value = "Mobile Number is required"; return
        }
        if (organization.value.isBlank()) {
            _error.value = "Organization is required"; return
        }
        if (designation.value.isBlank()) {
            _error.value = "Designation is required"; return
        }

        val allEmails    = emails.filter { it.isNotBlank() }
        val allPhones    = phones.filter { it.number.isNotBlank() }
                              .map { "${it.countryCode} ${it.number}".trim() }
        val allLandlines = landlines.filter { it.number.isNotBlank() }
                              .map { "${it.countryCode} ${it.number}".trim() }

        val size = FakeContactsData.contacts.value.size
        val newContact = Contact(
            id             = "u${size + 1}",
            name           = name.value.trim(),
            salutation     = salutation.value,
            designation    = designation.value.trim(),
            organization   = organization.value.trim(),
            email          = allEmails.firstOrNull() ?: "",
            mobileNumber   = allPhones.firstOrNull() ?: "",
            landlineNumber = allLandlines.firstOrNull(),
            department     = department.value.trim().takeIf { it.isNotBlank() },
            contactNo      = "CT-${size + 1001}",
            source         = source.value,
            isFavorite     = isFavorite.value,
            createdAt      = "2024-03-21T00:00:00Z",
            lastActivity   = null,
            emails         = allEmails,
            phones         = allPhones,
            landlineNumbers = allLandlines
        )

        FakeContactsData.addContact(newContact)
        _isSaved.value = true
    }
}
