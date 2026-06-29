package com.akashicsoft.crm.viewModel

import com.akashicsoft.crm.data.local.FakeLeadsData
import com.akashicsoft.crm.model.LeadListItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LeadDetailsViewModel {
    private val _lead = MutableStateFlow<LeadListItem?>(null)
    val lead = _lead.asStateFlow()

    fun loadLead(leadId: String) {
        val foundLead = FakeLeadsData.leads.value.find { it._id == leadId }
        _lead.value = foundLead
    }

    // Placeholder methods for future deep-linking / Intent implementation
    fun onCallClicked(phoneNumber: String) {
        println("Initiating call to: $phoneNumber")
    }

    fun onEmailClicked(email: String) {
        println("Opening email composer for: $email")
    }

    fun onWhatsAppClicked(phoneNumber: String) {
        println("Opening WhatsApp for: $phoneNumber")
    }
}
