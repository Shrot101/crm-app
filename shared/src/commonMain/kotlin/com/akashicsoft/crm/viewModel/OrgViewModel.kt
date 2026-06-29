package com.akashicsoft.crm.viewModel

import com.akashicsoft.crm.data.local.FakeOrgData
import com.akashicsoft.crm.model.OrgUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrgViewModel {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    private val _uiState = MutableStateFlow(OrgUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            FakeOrgData.organizations.collect { orgs ->
                _uiState.value = _uiState.value.copy(organizations = orgs)
            }
        }
    }

    fun loadOrganizations() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            delay(1000) // Simulate network delay
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun getCurrentPageInfo(): Triple<Int, Int, Int> {
        val total = _uiState.value.organizations.size
        return Triple(1, 1, total)
    }

    fun canNavigatePrevious(): Boolean = false
    fun canNavigateNext(): Boolean = false
    fun loadNextPage() {}
    fun loadPreviousPage() {}
    fun loadFirstPage() {}
    fun loadLastPage() {}
    
    fun getAvailableAgents(): List<String> = listOf("Alice Smith", "Bob Johnson", "Charlie Davis")
}
