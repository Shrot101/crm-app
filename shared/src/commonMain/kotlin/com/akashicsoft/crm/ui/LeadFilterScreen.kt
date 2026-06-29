package com.akashicsoft.crm.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akashicsoft.crm.ui.components.*
import com.akashicsoft.crm.viewModel.LeadViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeadFilterScreen(
    viewModel: LeadViewModel,
    onNavigateBack: () -> Unit
) {
    val currentFilter by viewModel.currentFilter.collectAsState()
    
    // Use a derived state or a local mutable map that triggers recomposition correctly
    val tempFilter = remember { mutableStateMapOf<String, String>().apply { putAll(currentFilter) } }
    
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    // Mock data for suggestions
    val allLeads by viewModel.uiState.collectAsState()
    val nameSuggestions = allLeads.leads.map { it.getFullName() }.distinct()
    val orgSuggestions = allLeads.leads.mapNotNull { it.organizationName }.distinct()
    val emailSuggestions = allLeads.leads.map { it.getPrimaryEmail() }.distinct()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lead Column Filters", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        tempFilter.clear() 
                        viewModel.updateFilter(emptyMap())
                    }) {
                        Icon(Icons.Default.FilterListOff, contentDescription = "Reset", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6200EE))
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
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF6200EE))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFF6200EE))
                        Spacer(Modifier.width(8.dp))
                        Text("Cancel", color = Color(0xFF6200EE))
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
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
                    ) {
                        Icon(Icons.Default.Check, null, tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("Apply", color = Color.White)
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
            // Top Selection Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    val filterType = tempFilter["filterType"] ?: "Filter"
                    
                    LeadDropdownField(
                        label = "Filter type (Lead/Ticket)",
                        selectedValue = filterType,
                        options = listOf("Filter", "Department", "Agent"),
                        onOptionSelected = {
                            tempFilter["filterType"] = it
                            // Reset type-specific values when switching
                            tempFilter.remove("department")
                            tempFilter.remove("agent")
                        }
                    )

                    // DYNAMICALLY EMERGING DROPDOWNS
                    AnimatedVisibility(
                        visible = filterType == "Department",
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            LeadDropdownField(
                                label = "Department",
                                selectedValue = tempFilter["department"] ?: "Select Department",
                                options = viewModel.getAvailableDepartments(),
                                onOptionSelected = { tempFilter["department"] = it }
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = filterType == "Agent",
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            LeadDropdownField(
                                label = "Agent",
                                selectedValue = tempFilter["agent"] ?: "Select Agent",
                                options = viewModel.getAvailableAgents(),
                                onOptionSelected = { tempFilter["agent"] = it }
                            )
                        }
                    }
                    
                    LeadDropdownField(
                        label = "Filter Range",
                        selectedValue = tempFilter["timeRange"] ?: "Today",
                        options = listOf("Today", "Yesterday", "This Week", "Last Week", "This Month"),
                        onOptionSelected = { tempFilter["timeRange"] = it }
                    )

                    LeadDropdownField(
                        label = "Frequency",
                        selectedValue = tempFilter["frequency"] ?: "Daily",
                        options = listOf("Daily", "Weekly", "Monthly"),
                        onOptionSelected = { tempFilter["frequency"] = it }
                    )
                }
            }

            // Expandable Sections
            FilterExpandableSection(
                title = "Requested Date & Time",
                icon = Icons.Default.CalendarMonth
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Requested Date", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterDateInput(
                            value = tempFilter["closeFrom"] ?: "From Date",
                            onDateSelected = { tempFilter["closeFrom"] = it },
                            modifier = Modifier.weight(1f)
                        )
                        FilterDateInput(
                            value = tempFilter["closeTo"] ?: "To Date",
                            onDateSelected = { tempFilter["closeTo"] = it },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Text("Requested Time", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterTimeInput(placeholder = "From", modifier = Modifier.weight(1f))
                        FilterTimeInput(placeholder = "To", modifier = Modifier.weight(1f))
                    }
                }
            }

            FilterExpandableSection(title = "Name", icon = Icons.Default.Person) {
                SearchableFilterField(
                    value = tempFilter["name"] ?: "",
                    onValueChange = { tempFilter["name"] = it },
                    placeholder = "Search name...",
                    suggestions = nameSuggestions
                )
            }

            FilterExpandableSection(title = "Organization", icon = Icons.Default.Business) {
                SearchableFilterField(
                    value = tempFilter["organization"] ?: "",
                    onValueChange = { tempFilter["organization"] = it },
                    placeholder = "Search organization...",
                    suggestions = orgSuggestions
                )
            }

            FilterExpandableSection(title = "Email", icon = Icons.Default.Email) {
                SearchableFilterField(
                    value = tempFilter["email"] ?: "",
                    onValueChange = { tempFilter["email"] = it },
                    placeholder = "Search email...",
                    suggestions = emailSuggestions
                )
            }

            FilterExpandableSection(title = "Status", icon = Icons.Default.CheckCircle) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("NEW", "QUALIFIED", "CONTACTED", "WON", "LOST").forEach { status ->
                        FilterChip(
                            label = status,
                            isSelected = tempFilter["status"] == status,
                            onClick = { tempFilter["status"] = status }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
