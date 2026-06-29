package com.akashicsoft.crm.viewModel

import com.akashicsoft.crm.data.local.FakeContactsData
import com.akashicsoft.crm.model.ContactUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ContactViewModel {
    private val viewModelScope = CoroutineScope(Dispatchers.Main)

    private val _uiState = MutableStateFlow(ContactUiState())
    val uiState = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // ── Filter State ──────────────────────────────────────────────────────────
    private val _currentFilter = MutableStateFlow<Map<String, String>>(emptyMap())
    val currentFilter: StateFlow<Map<String, String>> = _currentFilter.asStateFlow()

    val activeFilterCount: StateFlow<Int> = _currentFilter.map { filter ->
        val textKeys = listOf(
            "name", "email", "phone", "organization",
            "designation", "department", "salutation", "source"
        )
        var count = textKeys.count { key -> !filter[key].isNullOrBlank() }
        if (filter["isFavorite"] == "true") count++
        if (!filter["createdFrom"].isNullOrBlank()) count++
        if (!filter["createdTo"].isNullOrBlank()) count++
        count
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    init {
        viewModelScope.launch {
            FakeContactsData.contacts.collect {
                loadContacts()
            }
        }
    }

    fun loadContacts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            // Simulate network delay
            delay(500)

            val query = _searchQuery.value
            val f = _currentFilter.value

            val filteredContacts = FakeContactsData.contacts.value.filter { contact ->
                // Search query (global search bar)
                val matchesQuery = query.isBlank() ||
                    contact.name.contains(query, ignoreCase = true) ||
                    contact.organization.contains(query, ignoreCase = true) ||
                    contact.designation.contains(query, ignoreCase = true)

                // Filter: name
                val matchesName = f["name"].isNullOrBlank() ||
                    contact.name.contains(f["name"]!!, ignoreCase = true)

                // Filter: salutation
                val matchesSalutation = f["salutation"].isNullOrBlank() ||
                    contact.salutation.equals(f["salutation"], ignoreCase = true)

                // Filter: email
                val matchesEmail = f["email"].isNullOrBlank() ||
                    contact.email.contains(f["email"]!!, ignoreCase = true)

                // Filter: phone (mobile or landline)
                val matchesPhone = f["phone"].isNullOrBlank() ||
                    contact.mobileNumber.contains(f["phone"]!!, ignoreCase = true) ||
                    (contact.landlineNumber?.contains(f["phone"]!!, ignoreCase = true) == true)

                // Filter: organization
                val matchesOrg = f["organization"].isNullOrBlank() ||
                    contact.organization.contains(f["organization"]!!, ignoreCase = true)

                // Filter: designation
                val matchesDesignation = f["designation"].isNullOrBlank() ||
                    contact.designation.contains(f["designation"]!!, ignoreCase = true)

                // Filter: department
                val matchesDepartment = f["department"].isNullOrBlank() ||
                    (contact.department ?: "").contains(f["department"]!!, ignoreCase = true)

                // Filter: source
                val matchesSource = f["source"].isNullOrBlank() ||
                    contact.source.equals(f["source"], ignoreCase = true)

                // Filter: isFavorite
                val matchesFavorite = f["isFavorite"] != "true" || contact.isFavorite

                // Filter: createdAt date range (lexicographic; works for YYYY-MM-DD strings)
                // Contacts with null createdAt are always included when date filter is active
                val createdAt = contact.createdAt
                val matchesFrom = f["createdFrom"].isNullOrBlank() || createdAt == null ||
                    createdAt >= f["createdFrom"]!!
                val matchesTo = f["createdTo"].isNullOrBlank() || createdAt == null ||
                    createdAt <= f["createdTo"]!!

                matchesQuery && matchesName && matchesSalutation && matchesEmail &&
                    matchesPhone && matchesOrg && matchesDesignation && matchesDepartment &&
                    matchesSource && matchesFavorite && matchesFrom && matchesTo
            }

            val grouped = filteredContacts.groupBy {
                it.name.firstOrNull()?.uppercaseChar() ?: '#'
            }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                contacts = filteredContacts,
                groupedContacts = grouped,
                totalContacts = filteredContacts.size,
                currentPage = 1,
                totalPages = 1
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        loadContacts()
    }

    /** Called from ContactFilterScreen on Apply or Reset. */
    fun updateFilter(filter: Map<String, String>) {
        _currentFilter.value = filter
        loadContacts()
    }

    fun getCurrentPageInfo(): Triple<Int, Int, Int> {
        val state = _uiState.value
        return Triple(state.currentPage, state.totalPages, state.totalContacts)
    }

    // ── Suggestion Helpers (for filter dropdowns/chips) ───────────────────────

    fun getAvailableSalutations(): List<String> =
        listOf("Mr", "Mrs", "Ms", "Dr", "Prof")

    fun getAvailableSources(): List<String> =
        FakeContactsData.contacts.value
            .map { it.source }
            .distinct()
            .sorted()

    fun getNameSuggestions(): List<String> =
        FakeContactsData.contacts.value.map { it.name }.distinct()

    fun getOrganizationSuggestions(): List<String> =
        FakeContactsData.contacts.value.map { it.organization }.distinct()

    fun getDesignationSuggestions(): List<String> =
        FakeContactsData.contacts.value.map { it.designation }.distinct()

    fun getDepartmentSuggestions(): List<String> =
        FakeContactsData.contacts.value.mapNotNull { it.department }.distinct()
}
