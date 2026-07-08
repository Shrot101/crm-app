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
import com.akashicsoft.crm.activity.ui.ActivityScreen
import com.akashicsoft.crm.activity.ui.CreateActivityScreen
import com.akashicsoft.crm.ui.components.CrmTopAppBar
import com.akashicsoft.crm.viewModel.*
import com.akashicsoft.crm.activity.viewmodel.ActivityViewModel
import com.akashicsoft.crm.activity.viewmodel.CreateActivityViewModel
import com.akashicsoft.crm.activity.viewmodel.EditActivityViewModel
import com.akashicsoft.crm.activity.ui.EditActivityScreen

/**
 * Available Screens in the App
 */
enum class Screen {
    LEAD_LIST,
    DEAL_LIST,
    CONTACT_LIST,
    ORG_LIST,
    CREATE_LEAD,
    CREATE_DEAL,
    CREATE_CONTACT,
    CREATE_ORG,
    EDIT_LEAD,
    EDIT_DEAL,
    EDIT_ORG,
    LEAD_DETAILS,
    CONTACT_DETAILS,
    ORG_DETAILS,
    EDIT_CONTACT,
    LEAD_FILTER,
    DEAL_FILTER,
    CONTACT_FILTER,
    ACTIVITY,
    CREATE_ACTIVITY,
    EDIT_ACTIVITY
}

@Composable
@Preview
fun App() {
    var currentScreen by remember { mutableStateOf(Screen.ACTIVITY) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedLeadId by remember { mutableStateOf<String?>(null) }
    var selectedDealId by remember { mutableStateOf<String?>(null) }
    var selectedContactId by remember { mutableStateOf<String?>(null) }
    var selectedOrgId by remember { mutableStateOf<String?>(null) }
    var selectedActivityId by remember { mutableStateOf<String?>(null) }
    
    // ViewModels
    val leadViewModel = remember { LeadViewModel() }
    val dealViewModel = remember { DealViewModel() }
    val contactViewModel = remember { ContactViewModel() }
    val orgViewModel = remember { OrgViewModel() }
    val createLeadViewModel    = remember { CreateLeadViewModel() }
    val createDealViewModel    = remember { CreateDealViewModel() }
    val createContactViewModel = remember { CreateContactViewModel() }
    val createOrgViewModel     = remember { CreateOrgViewModel() }
    val leadDetailsViewModel = remember { LeadDetailsViewModel() }
    val editLeadViewModel = remember { EditLeadViewModel() }
    val editDealViewModel = remember { EditDealViewModel() }
    val editContactViewModel = remember { EditContactViewModel() }
    val editOrgViewModel = remember { EditOrgViewModel() }
    val contactDetailViewModel = remember { ContactDetailViewModel() }
    val orgDetailViewModel = remember { OrgDetailViewModel() }
    val activityViewModel = remember { ActivityViewModel() }
    val createActivityViewModel = remember { CreateActivityViewModel() }
    val editActivityViewModel = remember { EditActivityViewModel() }

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
                    currentScreen != Screen.ORG_DETAILS &&
                    currentScreen != Screen.EDIT_CONTACT &&
                    currentScreen != Screen.EDIT_ORG
                ) {
                    CrmTopAppBar(
                        title = when(currentScreen) {
                            Screen.LEAD_LIST     -> "Leads"
                            Screen.DEAL_LIST     -> "Deals"
                            Screen.CONTACT_LIST  -> "Contacts"
                            Screen.ORG_LIST      -> "Organizations"
                            Screen.CREATE_LEAD   -> "Create Lead"
                            Screen.CREATE_DEAL   -> "Create Deal"
                            Screen.CREATE_CONTACT -> "Create Contact"
                            Screen.CREATE_ORG    -> "Create Organization"
                            Screen.EDIT_LEAD     -> "Edit Lead"
                            Screen.EDIT_DEAL     -> "Edit Deal"
                            Screen.EDIT_CONTACT  -> "Edit Contact"
                            Screen.EDIT_ORG      -> "Edit Organization"
                            Screen.ACTIVITY      -> "Activity"
                            Screen.CREATE_ACTIVITY -> "Create Activity"
                            Screen.EDIT_ACTIVITY -> "Edit Activity"
                            else -> "CRM"
                        },
                        isSubScreen = currentScreen == Screen.CREATE_LEAD   ||
                                     currentScreen == Screen.EDIT_LEAD      ||
                                     currentScreen == Screen.CREATE_DEAL    ||
                                     currentScreen == Screen.EDIT_DEAL      ||
                                     currentScreen == Screen.CREATE_CONTACT ||
                                     currentScreen == Screen.CREATE_ORG     ||
                                     currentScreen == Screen.EDIT_CONTACT   ||
                                     currentScreen == Screen.EDIT_ORG       ||
                                     currentScreen == Screen.CREATE_ACTIVITY ||
                                     currentScreen == Screen.EDIT_ACTIVITY,
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
                                Screen.CREATE_ORG     -> currentScreen = Screen.ORG_LIST
                                Screen.CREATE_ACTIVITY -> currentScreen = Screen.ACTIVITY
                                Screen.EDIT_ACTIVITY -> currentScreen = Screen.ACTIVITY
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
                    currentScreen == Screen.CONTACT_LIST ||
                    currentScreen == Screen.ORG_LIST ||
                    currentScreen == Screen.ACTIVITY) {
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
                            selected = currentScreen == Screen.ACTIVITY,
                            onClick = { currentScreen = Screen.ACTIVITY },
                            icon = { Icon(Icons.Default.CalendarToday, "Activity") },
                            label = { Text("Activity") },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF6E40FF), selectedTextColor = Color(0xFF6E40FF))
                        )
                        NavigationBarItem(
                            selected = currentScreen == Screen.ORG_LIST,
                            onClick = { currentScreen = Screen.ORG_LIST },
                            icon = { Icon(Icons.Default.Menu, "More") },
                            label = { Text("More") },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF6E40FF), selectedTextColor = Color(0xFF6E40FF))
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
                Screen.ORG_LIST -> {
                    OrgScreen(
                        viewModel = orgViewModel,
                        modifier = Modifier.padding(innerPadding),
                        onAddOrgClick = {
                            createOrgViewModel.resetForm()
                            currentScreen = Screen.CREATE_ORG
                        },
                        onOrgSelected = { orgId ->
                            selectedOrgId = orgId
                            currentScreen = Screen.ORG_DETAILS
                        },
                        onEditOrg = { orgId ->
                            selectedOrgId = orgId
                            currentScreen = Screen.EDIT_ORG
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
                Screen.CREATE_ORG -> {
                    CreateOrgScreen(
                        viewModel      = createOrgViewModel,
                        modifier       = Modifier.padding(innerPadding),
                        onNavigateBack = { currentScreen = Screen.ORG_LIST }
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
                Screen.ORG_DETAILS -> {
                    selectedOrgId?.let { id ->
                        OrgDetailScreen(
                            viewModel = orgDetailViewModel,
                            orgId = id,
                            onNavigateBack = { currentScreen = Screen.ORG_LIST },
                            onEditOrg = { orgId ->
                                selectedOrgId = orgId
                                currentScreen = Screen.EDIT_ORG
                            }
                        )
                    }
                }
                Screen.EDIT_ORG -> {
                    selectedOrgId?.let { id ->
                        EditOrgScreen(
                            viewModel = editOrgViewModel,
                            orgId = id,
                            onNavigateBack = { currentScreen = Screen.ORG_LIST }
                        )
                    }
                }
                Screen.ACTIVITY -> {
                    ActivityScreen(
                        viewModel = activityViewModel,
                        modifier = Modifier.padding(innerPadding),
                        onCreateActivity = {
                            createActivityViewModel.resetForm(activityViewModel.uiState.value.selectedDate)
                            currentScreen = Screen.CREATE_ACTIVITY
                        },
                        onEditActivity = { activityId ->
                            selectedActivityId = activityId
                            currentScreen = Screen.EDIT_ACTIVITY
                        }
                    )
                }
                Screen.CREATE_ACTIVITY -> {
                    CreateActivityScreen(
                        viewModel = createActivityViewModel,
                        modifier = Modifier.padding(innerPadding),
                        onNavigateBack = { currentScreen = Screen.ACTIVITY }
                    )
                }
                Screen.EDIT_ACTIVITY -> {
                    selectedActivityId?.let { id ->
                        EditActivityScreen(
                            viewModel = editActivityViewModel,
                            activityId = id,
                            modifier = Modifier.padding(innerPadding),
                            onNavigateBack = { currentScreen = Screen.ACTIVITY }
                        )
                    }
                }
            }
        }
    }
}
