package com.akashicsoft.crm

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import androidx.compose.ui.tooling.preview.Preview
import com.akashicsoft.crm.ui.*
import com.akashicsoft.crm.ui.components.CrmTopAppBar
import com.akashicsoft.crm.viewModel.*

/**
 * Available Screens in the App
 */
enum class Screen {
    LEAD_LIST,
    DEAL_LIST,
    CONTACT_LIST,
    CREATE_LEAD,
    CREATE_DEAL,
    CREATE_CONTACT,
    EDIT_LEAD,
    EDIT_DEAL,
    LEAD_DETAILS,
    CONTACT_DETAILS,
    EDIT_CONTACT,
    LEAD_FILTER,
    DEAL_FILTER,
    CONTACT_FILTER
}

@Composable
@Preview
fun App() {
    var currentScreen by remember { mutableStateOf(Screen.CONTACT_LIST) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedLeadId by remember { mutableStateOf<String?>(null) }
    var selectedDealId by remember { mutableStateOf<String?>(null) }
    var selectedContactId by remember { mutableStateOf<String?>(null) }
    
    // ViewModels
    val leadViewModel = remember { LeadViewModel() }
    val dealViewModel = remember { DealViewModel() }
    val contactViewModel = remember { ContactViewModel() }
    val createLeadViewModel    = remember { CreateLeadViewModel() }
    val createDealViewModel    = remember { CreateDealViewModel() }
    val createContactViewModel = remember { CreateContactViewModel() }
    val leadDetailsViewModel = remember { LeadDetailsViewModel() }
    val editLeadViewModel = remember { EditLeadViewModel() }
    val editDealViewModel = remember { EditDealViewModel() }
    val editContactViewModel = remember { EditContactViewModel() }
    val contactDetailViewModel = remember { ContactDetailViewModel() }

    val activeDealFilters by dealViewModel.activeFilterCount.collectAsState()
    val activeContactFilters by contactViewModel.activeFilterCount.collectAsState()

    MaterialTheme {
        Scaffold(
            topBar = {
                // Some screens manage their own headers
                if (currentScreen != Screen.LEAD_FILTER && 
                    currentScreen != Screen.DEAL_FILTER && 
                    currentScreen != Screen.CONTACT_FILTER && 
                    currentScreen != Screen.LEAD_DETAILS &&
                    currentScreen != Screen.CONTACT_DETAILS &&
                    currentScreen != Screen.EDIT_CONTACT
                ) {
                    CrmTopAppBar(
                        title = when(currentScreen) {
                            Screen.LEAD_LIST     -> "Leads"
                            Screen.DEAL_LIST     -> "Deals"
                            Screen.CONTACT_LIST  -> "Contacts"
                            Screen.CREATE_LEAD   -> "Create Lead"
                            Screen.CREATE_DEAL   -> "Create Deal"
                            Screen.CREATE_CONTACT -> "Create Contact"
                            Screen.EDIT_LEAD     -> "Edit Lead"
                            Screen.EDIT_DEAL     -> "Edit Deal"
                            Screen.EDIT_CONTACT  -> "Edit Contact"
                            else -> "CRM"
                        },
                        isSubScreen = currentScreen == Screen.CREATE_LEAD   ||
                                     currentScreen == Screen.EDIT_LEAD      ||
                                     currentScreen == Screen.CREATE_DEAL    ||
                                     currentScreen == Screen.EDIT_DEAL      ||
                                     currentScreen == Screen.CREATE_CONTACT ||
                                     currentScreen == Screen.EDIT_CONTACT,
                        hasActiveFilters = when(currentScreen) {
                            Screen.DEAL_LIST -> activeDealFilters > 0
                            Screen.CONTACT_LIST -> activeContactFilters > 0
                            else -> false
                        },
                        filterBadgeColor = when(currentScreen) {
                            Screen.CONTACT_LIST -> Color(0xFFFF9800) // Orange point for contacts
                            else -> Color.Red
                        },
                        onMenuClick = {
                            when (currentScreen) {
                                Screen.CREATE_LEAD    -> currentScreen = Screen.LEAD_LIST
                                Screen.EDIT_LEAD      -> currentScreen = Screen.LEAD_LIST
                                Screen.CREATE_DEAL    -> currentScreen = Screen.DEAL_LIST
                                Screen.EDIT_DEAL      -> currentScreen = Screen.DEAL_LIST
                                Screen.CREATE_CONTACT -> currentScreen = Screen.CONTACT_LIST
                                else -> currentScreen = if (currentScreen == Screen.LEAD_LIST)
                                    Screen.DEAL_LIST else Screen.LEAD_LIST
                            }
                        },
                        onFilterClick = {
                            if (currentScreen == Screen.LEAD_LIST) currentScreen = Screen.LEAD_FILTER
                            if (currentScreen == Screen.DEAL_LIST) currentScreen = Screen.DEAL_FILTER
                            if (currentScreen == Screen.CONTACT_LIST) currentScreen = Screen.CONTACT_FILTER
                        },
                        onRefreshClick = {
                            if (currentScreen == Screen.LEAD_LIST) leadViewModel.loadLeads()
                            if (currentScreen == Screen.DEAL_LIST) dealViewModel.loadDeads()
                            if (currentScreen == Screen.CONTACT_LIST) contactViewModel.loadContacts()
                        }
                    )
                }
            },
            bottomBar = {
                if (currentScreen == Screen.LEAD_LIST || 
                    currentScreen == Screen.DEAL_LIST || 
                    currentScreen == Screen.CONTACT_LIST) {
                    NavigationBar(
                        containerColor = Color.White,
                        tonalElevation = 8.dp
                    ) {
                        NavigationBarItem(
                            selected = currentScreen == Screen.LEAD_LIST,
                            onClick = { currentScreen = Screen.LEAD_LIST },
                            icon = { Icon(Icons.Default.Leaderboard, "Leads") },
                            label = { Text("Leads") },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF6E40FF), selectedTextColor = Color(0xFF6E40FF))
                        )
                        NavigationBarItem(
                            selected = currentScreen == Screen.DEAL_LIST,
                            onClick = { currentScreen = Screen.DEAL_LIST },
                            icon = { Icon(Icons.Default.Handshake, "Deals") },
                            label = { Text("Deals") },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF6E40FF), selectedTextColor = Color(0xFF6E40FF))
                        )
                        NavigationBarItem(
                            selected = currentScreen == Screen.CONTACT_LIST,
                            onClick = { currentScreen = Screen.CONTACT_LIST },
                            icon = { Icon(Icons.Default.Groups, "Contacts") },
                            label = { Text("Contacts") },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF6E40FF), selectedTextColor = Color(0xFF6E40FF))
                        )
                        NavigationBarItem(
                            selected = false,
                            onClick = { /* More */ },
                            icon = { Icon(Icons.Default.Menu, "More") },
                            label = { Text("More") }
                        )
                    }
                }
            },
            containerColor = Color.White
        ) { innerPadding ->
            when (currentScreen) {
                Screen.LEAD_LIST -> {
                    LeadScreen(
                        viewModel = leadViewModel,
                        modifier = Modifier.padding(innerPadding),
                        onAddLeadClick = {
                            createLeadViewModel.resetForm()
                            currentScreen = Screen.CREATE_LEAD 
                        },
                        onLeadSelected = { leadId ->
                            selectedLeadId = leadId
                            currentScreen = Screen.LEAD_DETAILS
                        },
                        onEditLead = { leadId ->
                            selectedLeadId = leadId
                            currentScreen = Screen.EDIT_LEAD
                        }
                    )
                }
                Screen.DEAL_LIST -> {
                    DealScreen(
                        viewModel = dealViewModel,
                        modifier = Modifier.padding(innerPadding),
                        onAddDealClick = {
                            createDealViewModel.resetForm()
                            currentScreen = Screen.CREATE_DEAL
                        },
                        onEditDeal = { dealId ->
                            selectedDealId = dealId
                            currentScreen = Screen.EDIT_DEAL
                        }
                    )
                }
                Screen.CONTACT_LIST -> {
                    ContactScreen(
                        viewModel        = contactViewModel,
                        modifier         = Modifier.padding(innerPadding),
                        onContactSelected = { contactId ->
                            selectedContactId = contactId
                            currentScreen = Screen.CONTACT_DETAILS
                        },
                        onAddContactClick = {
                            createContactViewModel.resetForm()
                            currentScreen = Screen.CREATE_CONTACT
                        }
                    )
                }
                Screen.CREATE_LEAD -> {
                    CreateLeadScreen(
                        viewModel = createLeadViewModel,
                        modifier = Modifier.padding(innerPadding),
                        onNavigateBack = { currentScreen = Screen.LEAD_LIST }
                    )
                }
                Screen.CREATE_DEAL -> {
                    CreateDealScreen(
                        viewModel      = createDealViewModel,
                        modifier       = Modifier.padding(innerPadding),
                        onNavigateBack = { currentScreen = Screen.DEAL_LIST }
                    )
                }
                Screen.CREATE_CONTACT -> {
                    CreateContactScreen(
                        viewModel      = createContactViewModel,
                        modifier       = Modifier.padding(innerPadding),
                        onNavigateBack = { currentScreen = Screen.CONTACT_LIST }
                    )
                }
                Screen.EDIT_LEAD -> {
                    selectedLeadId?.let { id ->
                        EditLeadScreen(
                            viewModel = editLeadViewModel,
                            leadId = id,
                            modifier = Modifier.padding(innerPadding),
                            onNavigateBack = { currentScreen = Screen.LEAD_LIST }
                        )
                    }
                }
                Screen.EDIT_DEAL -> {
                    selectedDealId?.let { id ->
                        EditDealScreen(
                            viewModel = editDealViewModel,
                            dealId = id,
                            modifier = Modifier.padding(innerPadding),
                            onNavigateBack = { currentScreen = Screen.DEAL_LIST }
                        )
                    }
                }
                Screen.LEAD_FILTER -> {
                    LeadFilterScreen(
                        viewModel = leadViewModel,
                        onNavigateBack = { currentScreen = Screen.LEAD_LIST }
                    )
                }
                Screen.DEAL_FILTER -> {
                    DealFilterScreen(
                        viewModel = dealViewModel,
                        onNavigateBack = { currentScreen = Screen.DEAL_LIST }
                    )
                }
                Screen.CONTACT_FILTER -> {
                    ContactFilterScreen(
                        viewModel = contactViewModel,
                        onNavigateBack = { currentScreen = Screen.CONTACT_LIST }
                    )
                }
                Screen.LEAD_DETAILS -> {
                    selectedLeadId?.let { id ->
                        LeadDetailsScreen(
                            viewModel = leadDetailsViewModel,
                            leadId = id,
                            onNavigateBack = { currentScreen = Screen.LEAD_LIST },
                            onEditLead = { leadId ->
                                selectedLeadId = leadId
                                currentScreen = Screen.EDIT_LEAD
                            }
                        )
                    }
                }
                Screen.CONTACT_DETAILS -> {
                    selectedContactId?.let { id ->
                        ContactDetailsScreen(
                            viewModel = contactDetailViewModel,
                            contactId = id,
                            onNavigateBack = { currentScreen = Screen.CONTACT_LIST },
                            onEditClick = { contactId ->
                                selectedContactId = contactId
                                currentScreen = Screen.EDIT_CONTACT
                            }
                        )
                    }
                }
                Screen.EDIT_CONTACT -> {
                    selectedContactId?.let { id ->
                        EditContactScreen(
                            viewModel = editContactViewModel,
                            contactId = id,
                            onNavigateBack = { currentScreen = Screen.CONTACT_DETAILS }
                        )
                    }
                }
            }
        }
    }
}
