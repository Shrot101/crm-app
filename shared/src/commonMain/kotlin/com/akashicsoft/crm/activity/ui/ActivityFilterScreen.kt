package com.akashicsoft.crm.activity.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.akashicsoft.crm.ui.components.*
import com.akashicsoft.crm.ui.LeadDropdownField
import com.akashicsoft.crm.activity.viewmodel.ActivityFilterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityFilterScreen(
    viewModel: ActivityFilterViewModel,
    onNavigateBack: () -> Unit
) {
    val currentFilter by viewModel.currentFilter.collectAsState()
    val tempFilter = remember { mutableStateMapOf<String, String>().apply { putAll(currentFilter) } }
    val scrollState = rememberScrollState()
    val activeCount = remember(tempFilter.toMap()) {
        val ignoreValues = listOf("", "Select Type", "Select Source", "Select Priority", "Select Reminder", "From Date", "To Date")
        tempFilter.values.count { it !in ignoreValues }
    }

    val purpleColor = Color(0xFF6200EE)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            text = "Activity Filters", 
                            color = Color.White, 
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        AnimatedVisibility(visible = activeCount > 0) {
                            Text(
                                text = "$activeCount active filter${if (activeCount > 1) "s" else ""}",
                                color = Color.White.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        tempFilter.clear() 
                        viewModel.resetFilter() 
                    }) {
                        Icon(Icons.Default.FilterListOff, contentDescription = "Reset Filters", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = purpleColor)
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, purpleColor)
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
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = purpleColor)
                    ) {
                        Icon(Icons.Default.Check, null, tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("Apply Filters", color = Color.White)
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
            // SECTION: ACTIVITY TITLE
            FilterExpandableSection(title = "Activity Title", icon = Icons.Default.Title) {
                SearchableFilterField(
                    value = tempFilter["title"] ?: "",
                    onValueChange = { tempFilter["title"] = it },
                    placeholder = "Search activity title...",
                    suggestions = viewModel.getTitleSuggestions()
                )
            }

            // SECTION: DATE RANGE
            FilterExpandableSection(title = "Date Range", icon = Icons.Default.CalendarMonth) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterDateInput(
                        value = tempFilter["dateFrom"] ?: "From Date",
                        onDateSelected = { tempFilter["dateFrom"] = it },
                        modifier = Modifier.weight(1f)
                    )
                    FilterDateInput(
                        value = tempFilter["dateTo"] ?: "To Date",
                        onDateSelected = { tempFilter["dateTo"] = it },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // SECTION: CLASSIFICATION
            FilterExpandableSection(title = "Classification", icon = Icons.Default.Category) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    LeadDropdownField(
                        label = "Activity Type",
                        selectedValue = tempFilter["type"] ?: "Select Type",
                        options = viewModel.getAvailableTypes(),
                        onOptionSelected = { tempFilter["type"] = it }
                    )

                    LeadDropdownField(
                        label = "Source",
                        selectedValue = tempFilter["source"] ?: "Select Source",
                        options = viewModel.getAvailableSources(),
                        onOptionSelected = { tempFilter["source"] = it }
                    )

                    LeadDropdownField(
                        label = "Priority",
                        selectedValue = tempFilter["priority"] ?: "Select Priority",
                        options = viewModel.getAvailablePriorities(),
                        onOptionSelected = { tempFilter["priority"] = it }
                    )
                }
            }

            // SECTION: RELATIONSHIPS
            FilterExpandableSection(title = "Relationships", icon = Icons.Default.People) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SearchableFilterField(
                        value = tempFilter["contact"] ?: "",
                        onValueChange = { tempFilter["contact"] = it },
                        placeholder = "Search contact...",
                        suggestions = viewModel.getContactSuggestions()
                    )

                    SearchableFilterField(
                        value = tempFilter["organization"] ?: "",
                        onValueChange = { tempFilter["organization"] = it },
                        placeholder = "Search organization...",
                        suggestions = viewModel.getOrganizationSuggestions()
                    )

                    SearchableFilterField(
                        value = tempFilter["deal"] ?: "",
                        onValueChange = { tempFilter["deal"] = it },
                        placeholder = "Search deal...",
                        suggestions = viewModel.getDealSuggestions()
                    )
                }
            }

            // SECTION: ADDITIONAL INFO
            FilterExpandableSection(title = "Additional Info", icon = Icons.Default.Info) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SearchableFilterField(
                        value = tempFilter["location"] ?: "",
                        onValueChange = { tempFilter["location"] = it },
                        placeholder = "Search location...",
                        suggestions = viewModel.getLocationSuggestions()
                    )

                    LeadDropdownField(
                        label = "Reminder",
                        selectedValue = tempFilter["reminder"] ?: "Select Reminder",
                        options = viewModel.getReminderOptions(),
                        onOptionSelected = { tempFilter["reminder"] = it }
                    )

                    OutlinedTextField(
                        value = tempFilter["agenda"] ?: "",
                        onValueChange = { tempFilter["agenda"] = it },
                        placeholder = { Text("Filter by agenda") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.List, null) }
                    )
                }
            }

            // SECTION: ASSIGNMENT & NOTES
            FilterExpandableSection(title = "Assignment & Notes", icon = Icons.Default.AssignmentInd) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SearchableFilterField(
                        value = tempFilter["product"] ?: "",
                        onValueChange = { tempFilter["product"] = it },
                        placeholder = "Search product/service...",
                        suggestions = viewModel.getProductSuggestions()
                    )

                    SearchableFilterField(
                        value = tempFilter["assignedTo"] ?: "",
                        onValueChange = { tempFilter["assignedTo"] = it },
                        placeholder = "Search assigned user...",
                        suggestions = viewModel.getAssignedToSuggestions()
                    )

                    OutlinedTextField(
                        value = tempFilter["notes"] ?: "",
                        onValueChange = { tempFilter["notes"] = it },
                        placeholder = { Text("Filter by notes") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Notes, null) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
