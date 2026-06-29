package com.akashicsoft.crm.viewModel

import com.akashicsoft.crm.model.DealUiState
import com.akashicsoft.crm.data.local.FakeDealsData
import com.akashicsoft.crm.data.local.DealUIHelpers
import com.akashicsoft.crm.model.DealListItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DealViewModel {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    private val _uiState = MutableStateFlow(DealUiState())
    val uiState = _uiState.asStateFlow()

    private val _currentFilter = MutableStateFlow<Map<String, String>>(emptyMap())
    val currentFilter = _currentFilter.asStateFlow()

    private val _activeFilterCount = MutableStateFlow(0)
    val activeFilterCount = _activeFilterCount.asStateFlow()

    init {
        viewModelScope.launch {
            FakeDealsData.deals.collect { deals ->
                applyFiltering(deals, _currentFilter.value)
            }
        }
    }

    fun loadDeads() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            delay(1000) // Simulate network delay
            applyFiltering(FakeDealsData.deals.value, _currentFilter.value)
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun updateFilter(filter: Map<String, String>) {
        _currentFilter.value = filter
        calculateActiveFilterCount(filter)
        loadDeads()
    }

    private fun calculateActiveFilterCount(filter: Map<String, String>) {
        // Creative Logic: Count meaningful filter entries
        var count = 0
        
        // Filter Type is only counted if it's not the default "Filter"
        if (filter["filterType"] != null && filter["filterType"] != "Filter") count++
        
        // Time Range is only counted if it's not the default "Today"
        if (filter["timeRange"] != null && filter["timeRange"] != "Today") count++
        
        // Frequency is only counted if it's not the default "Daily"
        if (filter["frequency"] != null && filter["frequency"] != "Daily") count++

        // Other fields counted if they have any text
        val stringFields = listOf("title", "owner", "organization", "email", "status", "product", "tag", "department", "agent", "rating", "stage", "assignedTo")
        stringFields.forEach { field ->
            if (!filter[field].isNullOrBlank()) count++
        }

        // Date range counted if they aren't placeholders
        if (filter["closeFrom"] != null && filter["closeFrom"] != "From Date") count++
        if (filter["closeTo"] != null && filter["closeTo"] != "To Date") count++
        
        // Value range
        if (!filter["valueMin"].isNullOrBlank()) count++
        if (!filter["valueMax"].isNullOrBlank()) count++

        _activeFilterCount.value = count
    }

    private fun applyFiltering(allDeals: List<DealListItem>, filter: Map<String, String>) {
        var filteredList = allDeals

        // Filter by Title
        filter["title"]?.let { query ->
            if (query.isNotEmpty()) {
                filteredList = filteredList.filter { it.title.contains(query, ignoreCase = true) }
            }
        }

        // Filter by Owner
        filter["owner"]?.let { query ->
            if (query.isNotEmpty()) {
                filteredList = filteredList.filter { it.owner.name.contains(query, ignoreCase = true) }
            }
        }

        // Filter by Organization
        filter["organization"]?.let { query ->
            if (query.isNotEmpty()) {
                filteredList = filteredList.filter { it.organization?.contains(query, ignoreCase = true) == true }
            }
        }

        // Filter by Status (Display name match)
        filter["status"]?.let { status ->
            if (status.isNotEmpty()) {
                filteredList = filteredList.filter { it.stage?.replace("_", " ")?.equals(status, ignoreCase = true) == true }
            }
        }
        
        // Filter by Stage (Chip match)
        filter["stage"]?.let { stage ->
            if (stage.isNotEmpty()) {
                filteredList = filteredList.filter { it.stage?.replace("_", " ")?.equals(stage, ignoreCase = true) == true }
            }
        }

        // Filter by Rating
        filter["rating"]?.let { rating ->
            if (rating.isNotEmpty()) {
                filteredList = filteredList.filter { it.rating?.equals(rating, ignoreCase = true) == true }
            }
        }

        // Filter by Assigned To
        filter["assignedTo"]?.let { query ->
            if (query.isNotEmpty()) {
                filteredList = filteredList.filter { it.assignedTo?.name?.contains(query, ignoreCase = true) == true }
            }
        }

        // Filter by Product
        filter["product"]?.let { prod ->
            if (prod.isNotEmpty()) {
                filteredList = filteredList.filter { it.product?.contains(prod, ignoreCase = true) == true }
            }
        }

        // Filter by Date Range (Close Date)
        val fromDate = filter["closeFrom"]
        val toDate = filter["closeTo"]
        if (fromDate != null && fromDate != "From Date" && fromDate.isNotEmpty()) {
            filteredList = filteredList.filter { it.closeDate != null && it.closeDate >= fromDate }
        }
        if (toDate != null && toDate != "To Date" && toDate.isNotEmpty()) {
            filteredList = filteredList.filter { it.closeDate != null && it.closeDate <= toDate }
        }

        _uiState.value = _uiState.value.copy(deals = filteredList)
    }

    // Options for Filter Screen
    fun getAvailableAgents(): List<String> = FakeDealsData.deals.value.map { it.owner.name }.distinct()
    fun getAvailableDepartments(): List<String> = listOf("Sales", "Marketing", "Support", "Engineering", "Operations")
    fun getAvailableProducts(): List<String> = FakeDealsData.getMockProducts()
    fun getAvailableStages(): List<String> = DealUIHelpers.getStages()
    fun getAvailableRatings(): List<String> = DealUIHelpers.getRatings()

    fun getCurrentPageInfo(): Triple<Int, Int, Int> {
        val totalDeals = _uiState.value.deals.size
        return Triple(1, 1, totalDeals)
    }

    fun canNavigatePrevious(): Boolean = false
    fun canNavigateNext(): Boolean = false
    fun loadNextPage() {}
    fun loadPreviousPage() {}
    fun loadFirstPage() {}
    fun loadLastPage() {}
}
