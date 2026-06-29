package com.akashicsoft.crm.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.akashicsoft.crm.data.local.FakeDealsData
import com.akashicsoft.crm.data.local.FakeLeadsData
import com.akashicsoft.crm.data.local.DealUIHelpers
import com.akashicsoft.crm.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class EditDealViewModel {
    
    private var originalDeal: DealListItem? = null
    
    // Form State
    var title = mutableStateOf("")
    var organization = mutableStateOf("")
    var contactPerson = mutableStateOf("")
    var assignedTo = mutableStateOf<AssignedOwner?>(null)
    
    var category = mutableStateOf("Customer")
    var product = mutableStateOf("AWS Enterprise")
    
    var stage = mutableStateOf("Opportunity")
    var rating = mutableStateOf("Warm")
    
    var currency = mutableStateOf("$")
    var dealValue = mutableStateOf("")
    
    var closeDate = mutableStateOf("")
    var finalNotes = mutableStateOf("")
    
    var tags = mutableStateListOf<String>()

    private val _isSaved = MutableStateFlow(false)
    val isSaved = _isSaved.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // Options
    val availableOwners = FakeLeadsData.getMockUsers()
    val availableProducts = FakeDealsData.getMockProducts()
    val availableStages = DealUIHelpers.getStages()
    val availableCategories = DealUIHelpers.getCategories()
    val availableRatings = DealUIHelpers.getRatings()
    val currencies = FakeDealsData.getCurrencies()

    fun loadDeal(dealId: String) {
        val deal = FakeDealsData.deals.value.find { it._id == dealId }
        if (deal != null) {
            originalDeal = deal
            title.value = deal.title
            organization.value = deal.organization ?: ""
            contactPerson.value = deal.contactPerson ?: ""
            assignedTo.value = deal.assignedTo
            
            category.value = deal.category ?: "Customer"
            product.value = deal.product ?: "Others"
            
            // Format stage for display
            stage.value = deal.stage?.replace("_", " ")?.lowercase()?.capitalizeEachWord() ?: "Opportunity"
            rating.value = deal.rating ?: "Warm"
            
            currency.value = deal.currency ?: "$"
            dealValue.value = deal.dealValue?.toString() ?: ""
            
            closeDate.value = deal.closeDate ?: ""
            finalNotes.value = deal.finalNotes ?: ""
            
            tags.clear()
            deal.tags?.let { tags.addAll(it) }
            
            _isSaved.value = false
            _error.value = null
        }
    }

    fun addTag(tag: String) {
        if (tag.isNotBlank() && tag !in tags) {
            tags.add(tag)
        }
    }

    fun removeTag(tag: String) {
        tags.remove(tag)
    }

    fun saveChanges() {
        val deal = originalDeal ?: return
        
        if (title.value.isBlank()) {
            _error.value = "Deal Title is required"
            return
        }
        if (organization.value.isBlank()) {
            _error.value = "Organization is required"
            return
        }

        val valDouble = dealValue.value.toDoubleOrNull() ?: 0.0

        val updatedDeal = deal.copy(
            title = title.value,
            organization = organization.value,
            contactPerson = contactPerson.value,
            assignedTo = assignedTo.value,
            category = category.value,
            product = product.value,
            stage = stage.value.uppercase().replace(" ", "_"),
            rating = rating.value,
            currency = currency.value,
            dealValue = valDouble,
            closeDate = closeDate.value,
            tags = tags.toList(),
            finalNotes = finalNotes.value
        )

        FakeDealsData.updateDeal(updatedDeal)
        _isSaved.value = true
    }

    private fun String.capitalizeEachWord(): String {
        return split(" ").joinToString(" ") { it.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase() else char.toString() } }
    }
}
