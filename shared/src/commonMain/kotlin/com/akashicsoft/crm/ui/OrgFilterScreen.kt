package com.akashicsoft.crm.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akashicsoft.crm.ui.components.*
import com.akashicsoft.crm.viewModel.OrgFilterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrgFilterScreen(
    viewModel: OrgFilterViewModel,
    onNavigateBack: () -> Unit
) {
    val currentFilter by viewModel.currentFilter.collectAsState()
    val tempFilter = remember { mutableStateMapOf<String, String>().apply { putAll(currentFilter) } }
    val scrollState = rememberScrollState()

    val activeCount = remember(tempFilter.toMap()) {
        val ignoreValues = listOf("", "Select", "From Date", "To Date")
        tempFilter.values.count { it !in ignoreValues }
    }

    val purpleColor = Color(0xFF6200EE)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text  = "Organization Filters",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        AnimatedVisibility(visible = activeCount > 0) {
                            Text(
                                text  = "$activeCount active filter${if (activeCount > 1) "s" else ""}",
                                color = Color.White.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        tempFilter.clear()
                        viewModel.resetFilter()
                    }) {
                        Icon(
                            Icons.Default.FilterListOff,
                            contentDescription = "Reset Filters",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = purpleColor)
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp, color = Color.White) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick  = onNavigateBack,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape    = RoundedCornerShape(24.dp),
                        border   = androidx.compose.foundation.BorderStroke(1.dp, purpleColor)
                    ) {
                        Icon(Icons.Default.Close, null, tint = purpleColor)
                        Spacer(Modifier.width(8.dp))
                        Text("Cancel", color = purpleColor)
                    }
                    Button(
                        onClick = {
                            viewModel.updateFilter(tempFilter.toMap())
                            onNavigateBack()
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape    = RoundedCornerShape(24.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = purpleColor)
                    ) {
                        Icon(Icons.Default.Check, null, tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("Apply Filters", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── 1. CREATED DATE ────────────────────────────────────────────────
            FilterExpandableSection(title = "Created Date", icon = Icons.Default.CalendarMonth) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterDateInput(
                        value          = tempFilter["createdFrom"] ?: "From Date",
                        onDateSelected = { tempFilter["createdFrom"] = it },
                        modifier       = Modifier.weight(1f)
                    )
                    FilterDateInput(
                        value          = tempFilter["createdTo"] ?: "To Date",
                        onDateSelected = { tempFilter["createdTo"] = it },
                        modifier       = Modifier.weight(1f)
                    )
                }
            }

            // ── 2. ORGANIZATION NAME ───────────────────────────────────────────
            FilterExpandableSection(title = "Organization Name", icon = Icons.Default.Business) {
                SearchableFilterField(
                    value         = tempFilter["organizationName"] ?: "",
                    onValueChange = { tempFilter["organizationName"] = it },
                    placeholder   = "Search by name…",
                    suggestions   = viewModel.getOrgNameSuggestions()
                )
            }

            // ── 3. WEBSITE ─────────────────────────────────────────────────────
            FilterExpandableSection(title = "Website", icon = Icons.Default.Language) {
                SearchableFilterField(
                    value         = tempFilter["website"] ?: "",
                    onValueChange = { tempFilter["website"] = it },
                    placeholder   = "Search by website…",
                    suggestions   = emptyList()
                )
            }

            // ── 4. CLASSIFICATION ──────────────────────────────────────────────
            FilterExpandableSection(title = "Classification", icon = Icons.Default.Category) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    LeadDropdownField(
                        label = "Industry",
                        selectedValue = tempFilter["industry"] ?: "Select",
                        options = viewModel.getIndustryOptions(),
                        onOptionSelected = { tempFilter["industry"] = it }
                    )
                    LeadDropdownField(
                        label = "Type",
                        selectedValue = tempFilter["type"] ?: "Select",
                        options = viewModel.getTypeOptions(),
                        onOptionSelected = { tempFilter["type"] = it }
                    )
                    LeadDropdownField(
                        label = "Lead Source",
                        selectedValue = tempFilter["leadSource"] ?: "Select",
                        options = viewModel.getLeadSourceOptions(),
                        onOptionSelected = { tempFilter["leadSource"] = it }
                    )
                }
            }

            // ── 5. ASSOCIATED COMPANY ─────────────────────────────────────────
            FilterExpandableSection(title = "Associated Company", icon = Icons.Default.AccountTree) {
                SearchableFilterField(
                    value         = tempFilter["associatedCompany"] ?: "",
                    onValueChange = { tempFilter["associatedCompany"] = it },
                    placeholder   = "Search associated company…",
                    suggestions   = viewModel.getAssociatedCompanySuggestions()
                )
            }

            // ── 6. EMPLOYEES RANGE ────────────────────────────────────────────
            FilterExpandableSection(title = "No. of Employees", icon = Icons.Default.People) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = tempFilter["empMin"] ?: "",
                        onValueChange = { tempFilter["empMin"] = it },
                        label = { Text("Min") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    Text("-")
                    OutlinedTextField(
                        value = tempFilter["empMax"] ?: "",
                        onValueChange = { tempFilter["empMax"] = it },
                        label = { Text("Max") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            }

            // ── 7. TARGET AMOUNT RANGE ────────────────────────────────────────
            FilterExpandableSection(title = "Target Amount", icon = Icons.Default.AttachMoney) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = tempFilter["targetMin"] ?: "",
                        onValueChange = { tempFilter["targetMin"] = it },
                        label = { Text("Min") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    Text("-")
                    OutlinedTextField(
                        value = tempFilter["targetMax"] ?: "",
                        onValueChange = { tempFilter["targetMax"] = it },
                        label = { Text("Max") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            }

            // ── 8. LOCATION ────────────────────────────────────────────────────
            FilterExpandableSection(title = "Location", icon = Icons.Default.LocationOn) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SearchableFilterField(
                        value         = tempFilter["city"] ?: "",
                        onValueChange = { tempFilter["city"] = it },
                        placeholder   = "Search by city…",
                        suggestions   = viewModel.getCitySuggestions()
                    )
                    SearchableFilterField(
                        value         = tempFilter["state"] ?: "",
                        onValueChange = { tempFilter["state"] = it },
                        placeholder   = "Search by state…",
                        suggestions   = viewModel.getStateSuggestions()
                    )
                }
            }

            // ── 9. ASSIGNMENT ──────────────────────────────────────────────────
            FilterExpandableSection(title = "Assigned To", icon = Icons.Default.PersonPin) {
                LeadDropdownField(
                    label = "Agent",
                    selectedValue = tempFilter["assignedTo"] ?: "Select",
                    options = viewModel.getAgentOptions(),
                    onOptionSelected = { tempFilter["assignedTo"] = it }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
