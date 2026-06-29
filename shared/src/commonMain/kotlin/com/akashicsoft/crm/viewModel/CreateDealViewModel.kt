package com.akashicsoft.crm.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.akashicsoft.crm.data.local.FakeDealsData
import com.akashicsoft.crm.data.local.FakeLeadsData
import com.akashicsoft.crm.data.local.DealUIHelpers
import com.akashicsoft.crm.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CreateDealViewModel {
    
    // Form State
    var title = mutableStateOf("")
    var organization = mutableStateOf("")
    var contactPerson = mutableStateOf("")
    var assignedOwner = mutableStateOf<AssignedOwner?>(null)
    
    var category = mutableStateOf("Customer")
    var product = mutableStateOf("AWS Enterprise")
    
    var stage = mutableStateOf("Opportunity")
    var rating = mutableStateOf("Warm")
    
    var currency = mutableStateOf("$")
    var dealValue = mutableStateOf("")
    
    var closeDate = mutableStateOf("2024-12-31")
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

    fun resetForm() {
        title.value = ""
        organization.value = ""
        contactPerson.value = ""
        assignedOwner.value = null
        category.value = "Customer"
        product.value = "AWS Enterprise"
        stage.value = "Opportunity"
        rating.value = "Warm"
        currency.value = "$"
        dealValue.value = ""
        closeDate.value = "2024-12-31"
        finalNotes.value = ""
        tags.clear()
        _isSaved.value = false
        _error.value = null
    }

    fun addTag(tag: String) {
        if (tag.isNotBlank() && tag !in tags) {
            tags.add(tag)
        }
    }

    fun removeTag(tag: String) {
        tags.remove(tag)
    }

    fun saveDeal() {
        if (title.value.isBlank()) {
            _error.value = "Deal Title is required"
            return
        }
        if (organization.value.isBlank()) {
            _error.value = "Organization is required"
            return
        }

        val valDouble = dealValue.value.toDoubleOrNull() ?: 0.0

        val newDeal = DealListItem(
            _id = "d${FakeDealsData.deals.value.size + 1}",
            title = title.value,
            dealNumber = "DL-${(FakeDealsData.deals.value.size + 1001)}",
            owner = AssignedOwner("owner1", "Alice Smith"), // Current user mock
            organization = organization.value,
            rating = rating.value,
            assignedTo = assignedOwner.value,
            stage = stage.value.uppercase().replace(" ", "_"),
            product = product.value,
            tags = tags.toList(),
            dealValue = valDouble,
            closeDate = closeDate.value,
            category = category.value,
            contactPerson = contactPerson.value,
            currency = currency.value,
            finalNotes = finalNotes.value
        )

        FakeDealsData.addDeal(newDeal)
        _isSaved.value = true
    }
}
