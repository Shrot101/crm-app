package com.akashicsoft.crm.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.akashicsoft.crm.data.local.FakeContactsData
import com.akashicsoft.crm.model.Contact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class EditContactViewModel {

    private var originalContact: Contact? = null

    // ── Form State ────────────────────────────────────────────────────────────
    var salutation   = mutableStateOf("Mr.")
    var name         = mutableStateOf("")
    var emails       = mutableStateListOf("")                   // [0] = primary email
    var phones       = mutableStateListOf(ContactPhoneEntry())  // [0] = primary mobile
    var landlines    = mutableStateListOf<ContactPhoneEntry>()  // optional
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

    // ── Load ──────────────────────────────────────────────────────────────────
    fun loadContact(id: String) {
        val contact = FakeContactsData.contacts.value.find { it.id == id } ?: return
        originalContact = contact

        salutation.value   = contact.salutation ?: "Mr."
        name.value         = contact.name
        organization.value = contact.organization
        designation.value  = contact.designation
        department.value   = contact.department ?: ""
        source.value       = contact.source
        isFavorite.value   = contact.isFavorite

        // ── Emails ────────────────────────────────────────────────────────────
        emails.clear()
        val allEmails = contact.emails.ifEmpty { listOf(contact.email) }
        emails.addAll(allEmails.ifEmpty { listOf("") })

        // ── Phones ────────────────────────────────────────────────────────────
        phones.clear()
        val allPhones = contact.phones.ifEmpty { listOf(contact.mobileNumber) }
        phones.addAll(allPhones.map { parsePhoneEntry(it) }.ifEmpty { listOf(ContactPhoneEntry()) })

        // ── Landlines ─────────────────────────────────────────────────────────
        landlines.clear()
        val allLandlines = contact.landlineNumbers.ifEmpty { listOfNotNull(contact.landlineNumber) }
        landlines.addAll(allLandlines.map { parsePhoneEntry(it) })

        _isSaved.value = false
        _error.value   = null
    }

    /** Splits a stored phone string (e.g. "+91 9876543210") into a [ContactPhoneEntry]. */
    private fun parsePhoneEntry(raw: String): ContactPhoneEntry {
        val trimmed = raw.trim()
        val code = FakeContactsData.getCountryCodes()
            .map { it.second }
            .sortedByDescending { it.length }
            .firstOrNull { trimmed.startsWith(it) }
        return if (code != null) {
            ContactPhoneEntry(countryCode = code, number = trimmed.removePrefix(code).trim())
        } else {
            ContactPhoneEntry(countryCode = "+91", number = trimmed)
        }
    }

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

    // ── Save ──────────────────────────────────────────────────────────────────
    fun saveChanges() {
        val current = originalContact ?: return

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

        val updated = current.copy(
            salutation      = salutation.value,
            name            = name.value.trim(),
            designation     = designation.value.trim(),
            organization    = organization.value.trim(),
            department      = department.value.trim().takeIf { it.isNotBlank() },
            source          = source.value,
            isFavorite      = isFavorite.value,
            email           = allEmails.firstOrNull() ?: current.email,
            mobileNumber    = allPhones.firstOrNull() ?: current.mobileNumber,
            landlineNumber  = allLandlines.firstOrNull(),
            emails          = allEmails,
            phones          = allPhones,
            landlineNumbers = allLandlines
        )

        FakeContactsData.updateContact(updated)
        _isSaved.value = true
    }
}
