package com.akashicsoft.crm.viewModel

import com.akashicsoft.crm.model.LeadUiState
import com.akashicsoft.crm.data.local.FakeLeadsData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LeadViewModel {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    private val _uiState = MutableStateFlow(LeadUiState())
    val uiState = _uiState.asStateFlow()

    private val _currentFilter = MutableStateFlow<Map<String, String>>(emptyMap())
    val currentFilter = _currentFilter.asStateFlow()

    init {
        viewModelScope.launch {
            FakeLeadsData.leads.collect { leads ->
                _uiState.value = _uiState.value.copy(leads = leads)
            }
        }
    }

    fun loadLeads() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            delay(1500) // Simulate network delay
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun updateFilter(filter: Map<String, String>) {
        _currentFilter.value = filter
        // In a real app, you would fetch filtered data from API here
        loadLeads()
    }

    fun getAvailableAgents(): List<String> = FakeLeadsData.getMockUsers().map { it.name }
    fun getAvailableDepartments(): List<String> = FakeLeadsData.getMockDepartments()

    fun getCurrentPageInfo(): Triple<Int, Int, Int> {
        val totalLeads = _uiState.value.leads.size
        return Triple(1, 1, totalLeads)
    }

    fun canNavigatePrevious(): Boolean = false
    fun canNavigateNext(): Boolean = false
    fun loadNextPage() {}
    fun loadPreviousPage() {}
    fun loadFirstPage() {}
    fun loadLastPage() {}
}
