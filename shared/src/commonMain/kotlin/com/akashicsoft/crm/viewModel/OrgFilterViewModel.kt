package com.akashicsoft.crm.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akashicsoft.crm.data.local.FakeOrgData
import kotlinx.coroutines.flow.*

class OrgFilterViewModel : ViewModel() {

    private val _currentFilter = MutableStateFlow<Map<String, String>>(emptyMap())
    val currentFilter: StateFlow<Map<String, String>> = _currentFilter.asStateFlow()

    val activeFilterCount: StateFlow<Int> = _currentFilter.map { filters ->
        calculateCount(filters)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private fun calculateCount(filters: Map<String, String>): Int {
        val ignoreValues = listOf("", "Select", "From Date", "To Date")
        return filters.values.count { it !in ignoreValues }
    }

    fun updateFilter(filter: Map<String, String>) {
        _currentFilter.value = filter
    }

    fun resetFilter() {
        _currentFilter.value = emptyMap()
    }

    // Helper methods for Filter Screen options
    fun getIndustryOptions() = listOf("Technology", "Manufacturing", "Artificial Intelligence", "Healthcare", "Finance", "Retail", "Education", "Real Estate", "Others")
    fun getTypeOptions() = listOf("Customer", "Partner", "Prospect", "Vendor", "Competitor")
    fun getLeadSourceOptions() = listOf("Web", "Referral", "Conference", "Cold Call", "LinkedIn", "Direct", "Others")
    fun getAgentOptions() = listOf("Alice Smith", "Bob Johnson", "Charlie Davis")

    // Suggestions from fake data
    fun getOrgNameSuggestions() = FakeOrgData.organizations.value.map { it.organizationName }.distinct()
    fun getCitySuggestions() = FakeOrgData.organizations.value.map { it.city }.distinct()
    fun getStateSuggestions() = FakeOrgData.organizations.value.map { it.state }.distinct()
    fun getAssociatedCompanySuggestions() = FakeOrgData.organizations.value.map { it.associatedCompany }.distinct().filter { it != "-" }
}
