package com.akashicsoft.crm.activity.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akashicsoft.crm.activity.data.FakeActivityData
import com.akashicsoft.crm.activity.model.*
import kotlinx.coroutines.flow.*

class ActivityFilterViewModel : ViewModel() {

    private val _currentFilter = MutableStateFlow<Map<String, String>>(emptyMap())
    val currentFilter: StateFlow<Map<String, String>> = _currentFilter.asStateFlow()

    val activeFilterCount: StateFlow<Int> = _currentFilter.map { filters ->
        calculateCount(filters)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private fun calculateCount(filters: Map<String, String>): Int {
        var count = 0
        val ignoreValues = listOf("", "Select Type", "Select Source", "Select Priority", "Select Reminder", "From Date", "To Date")
        filters.forEach { (key, value) ->
            if (value !in ignoreValues) {
                count++
            }
        }
        return count
    }

    fun updateFilter(filter: Map<String, String>) {
        _currentFilter.value = filter
    }

    fun resetFilter() {
        _currentFilter.value = emptyMap()
    }

    // Helper methods for Filter Screen options
    fun getAvailableTypes() = ActivityType.values().map { it.name.lowercase().replaceFirstChar { c -> c.uppercase() } }
    fun getAvailableSources() = ActivitySource.values().map { it.name.lowercase().replaceFirstChar { c -> c.uppercase() } }
    fun getAvailablePriorities() = ActivityPriority.values().map { it.name.lowercase().replaceFirstChar { c -> c.uppercase() } }
    fun getReminderOptions() = listOf("None", "5 minutes before", "15 minutes before", "30 minutes before", "1 hour before", "1 day before")

    // Suggestions from fake data
    fun getTitleSuggestions() = FakeActivityData.activities.value.map { it.title }.distinct()
    fun getContactSuggestions() = FakeActivityData.activities.value.mapNotNull { it.contact?.name }.distinct()
    fun getDealSuggestions() = FakeActivityData.activities.value.mapNotNull { it.deal?.title }.distinct()
    fun getAssignedToSuggestions() = FakeActivityData.activities.value.mapNotNull { it.assignedTo?.name }.distinct()
    fun getProductSuggestions() = FakeActivityData.activities.value.mapNotNull { it.product }.distinct()
    fun getOrganizationSuggestions() = FakeActivityData.activities.value.mapNotNull { it.organization }.distinct()
    fun getLocationSuggestions() = FakeActivityData.activities.value.mapNotNull { it.location }.distinct()
}
