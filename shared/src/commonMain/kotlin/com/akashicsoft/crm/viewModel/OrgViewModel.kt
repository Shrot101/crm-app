package com.akashicsoft.crm.viewModel

import com.akashicsoft.crm.data.local.FakeOrgData
import com.akashicsoft.crm.model.OrgUiState
import com.akashicsoft.crm.model.Organization
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class OrgViewModel {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    private val _currentFilter = MutableStateFlow<Map<String, String>>(emptyMap())
    val currentFilter: StateFlow<Map<String, String>> = _currentFilter.asStateFlow()

    val activeFilterCount: StateFlow<Int> = _currentFilter.map { filters ->
        calculateCount(filters)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _uiState = MutableStateFlow(OrgUiState())
    val uiState: StateFlow<OrgUiState> = combine(
        FakeOrgData.organizations,
        _currentFilter
    ) { organizations, filters ->
        val filtered = organizations.filter { matchesFilter(it, filters) }
        OrgUiState(organizations = filtered)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), OrgUiState())

    init {
        loadOrganizations()
    }

    fun updateFilter(filter: Map<String, String>) {
        _currentFilter.value = filter
    }

    private fun matchesFilter(org: Organization, filters: Map<String, String>): Boolean {
        if (filters.isEmpty()) return true
        return filters.all { (key, value) ->
            if (value.isBlank() || value == "Select" || value == "From Date" || value == "To Date") return@all true
            when (key) {
                "organizationName" -> org.organizationName.contains(value, ignoreCase = true)
                "website" -> org.website.contains(value, ignoreCase = true)
                "industry" -> org.industry.equals(value, ignoreCase = true)
                "type" -> org.type.equals(value, ignoreCase = true)
                "leadSource" -> org.leadSource.equals(value, ignoreCase = true)
                "associatedCompany" -> org.associatedCompany.contains(value, ignoreCase = true)
                "city" -> org.city.contains(value, ignoreCase = true)
                "state" -> org.state.contains(value, ignoreCase = true)
                "assignedTo" -> org.assignedTo.equals(value, ignoreCase = true)
                "empMin" -> org.numberOfEmployees >= (value.toIntOrNull() ?: 0)
                "empMax" -> org.numberOfEmployees <= (value.toIntOrNull() ?: Int.MAX_VALUE)
                "targetMin" -> org.targetAmount >= (value.toDoubleOrNull() ?: 0.0)
                "targetMax" -> org.targetAmount <= (value.toDoubleOrNull() ?: Double.MAX_VALUE)
                "createdFrom" -> {
                    try {
                        val fromDate = value
                        org.createdAt?.substringBefore("T")?.let { it >= fromDate } ?: true
                    } catch (e: Exception) { true }
                }
                "createdTo" -> {
                    try {
                        val toDate = value
                        org.createdAt?.substringBefore("T")?.let { it <= toDate } ?: true
                    } catch (e: Exception) { true }
                }
                else -> true
            }
        }
    }

    private fun calculateCount(filters: Map<String, String>): Int {
        val ignoreValues = listOf("", "Select", "From Date", "To Date")
        return filters.values.count { it !in ignoreValues }
    }

    fun loadOrganizations() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            delay(1000)
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun getCurrentPageInfo(): Triple<Int, Int, Int> {
        val total = uiState.value.organizations.size
        return Triple(1, 1, total)
    }

    fun canNavigatePrevious(): Boolean = false
    fun canNavigateNext(): Boolean = false
    fun loadNextPage() {}
    fun loadPreviousPage() {}
    fun loadFirstPage() {}
    fun loadLastPage() {}
    
    fun getAvailableAgents(): List<String> = listOf("Alice Smith", "Bob Johnson", "Charlie Davis")
    fun getIndustryOptions() = listOf("Technology", "Manufacturing", "Artificial Intelligence", "Healthcare", "Finance", "Retail", "Education", "Real Estate", "Others")
    fun getTypeOptions() = listOf("Customer", "Partner", "Prospect", "Vendor", "Competitor")
    fun getLeadSourceOptions() = listOf("Web", "Referral", "Conference", "Cold Call", "LinkedIn", "Direct", "Others")

    fun getOrgNameSuggestions() = FakeOrgData.organizations.value.map { it.organizationName }.distinct()
    fun getCitySuggestions() = FakeOrgData.organizations.value.map { it.city }.distinct()
    fun getStateSuggestions() = FakeOrgData.organizations.value.map { it.state }.distinct()
    fun getAssociatedCompanySuggestions() = FakeOrgData.organizations.value.map { it.associatedCompany }.distinct().filter { it != "-" }
}
