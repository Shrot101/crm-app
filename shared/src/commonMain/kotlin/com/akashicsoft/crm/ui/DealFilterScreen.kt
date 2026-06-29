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
import com.akashicsoft.crm.viewModel.DealViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DealFilterScreen(
    viewModel: DealViewModel,
    onNavigateBack: () -> Unit
) {
    val currentFilter by viewModel.currentFilter.collectAsState()
    val tempFilter = remember { mutableStateMapOf<String, String>().apply { putAll(currentFilter) } }
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    // Mock data for suggestions
    val allDealsState by viewModel.uiState.collectAsState()
    val titleSuggestions = allDealsState.deals.map { it.title }.distinct()
    val ownerSuggestions = allDealsState.deals.map { it.owner.name }.distinct()
    val orgSuggestions = allDealsState.deals.mapNotNull { it.organization }.distinct()
    val assigneeSuggestions = allDealsState.deals.mapNotNull { it.assignedTo?.name }.distinct()
    val productSuggestions = viewModel.getAvailableProducts()

    // Calculate count for header
    val activeCount = remember(tempFilter.toMap()) {
        var count = 0
        if (tempFilter["filterType"] != null && tempFilter["filterType"] != "Filter") count++
        if (tempFilter["timeRange"] != null && tempFilter["timeRange"] != "Today") count++
        if (tempFilter["frequency"] != null && tempFilter["frequency"] != "Daily") count++
        
        val stringFields = listOf("title", "owner", "organization", "email", "status", "product", "tag", "department", "agent", "rating", "stage", "assignedTo")
        stringFields.forEach { field ->
            if (!tempFilter[field].isNullOrBlank()) count++
        }

        if (tempFilter["closeFrom"] != null && tempFilter["closeFrom"] != "From Date") count++
        if (tempFilter["closeTo"] != null && tempFilter["closeTo"] != "To Date") count++
        if (!tempFilter["valueMin"].isNullOrBlank()) count++
        if (!tempFilter["valueMax"].isNullOrBlank()) count++
        count
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Deal Column Filters", color = Color.White, style = MaterialTheme.typography.titleMedium)
                        if (activeCount > 0) {
                            Text(
                                text = "$activeCount active filter(s)",
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
                        viewModel.updateFilter(emptyMap()) // Clear global state too
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
                        Icon(Icons.Default.Close, null, tint = Color(0xFF6200EE))
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
                // Removed imePadding() from here because Scaffold padding already handles bottom insets,
                // and adjustResize in Android handles the keyboard push. 
                // Double padding was causing the white gap.
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // PRIMARY SELECTION CARD
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
                            tempFilter.remove("department")
                            tempFilter.remove("agent")
                        }
                    )

                    AnimatedVisibility(visible = filterType == "Department", enter = expandVertically(), exit = shrinkVertically()) {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            LeadDropdownField(
                                label = "Department",
                                selectedValue = tempFilter["department"] ?: "Select Department",
                                options = viewModel.getAvailableDepartments(),
                                onOptionSelected = { tempFilter["department"] = it }
                            )
                        }
                    }

                    AnimatedVisibility(visible = filterType == "Agent", enter = expandVertically(), exit = shrinkVertically()) {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
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

            // SECTION: CLOSE DATE
            FilterExpandableSection(title = "Close Date", icon = Icons.Default.CalendarMonth) {
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
            }

            // SECTION: DEAL TITLE
            FilterExpandableSection(title = "Deal Title", icon = Icons.Default.Title) {
                SearchableFilterField(
                    value = tempFilter["title"] ?: "",
                    onValueChange = { tempFilter["title"] = it },
                    placeholder = "Search deal title...",
                    suggestions = titleSuggestions
                )
            }

            // SECTION: OWNER
            FilterExpandableSection(title = "Owner", icon = Icons.Default.Person) {
                SearchableFilterField(
                    value = tempFilter["owner"] ?: "",
                    onValueChange = { tempFilter["owner"] = it },
                    placeholder = "Search owner...",
                    suggestions = ownerSuggestions
                )
            }

            // SECTION: ORGANIZATION
            FilterExpandableSection(title = "Organization", icon = Icons.Default.Business) {
                SearchableFilterField(
                    value = tempFilter["organization"] ?: "",
                    onValueChange = { tempFilter["organization"] = it },
                    placeholder = "Search organization...",
                    suggestions = orgSuggestions
                )
            }

            // SECTION: RATING/LABEL
            FilterExpandableSection(title = "Rating/Label", icon = Icons.Default.Star) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    viewModel.getAvailableRatings().forEach { rating ->
                        FilterChip(
                            label = rating,
                            isSelected = tempFilter["rating"] == rating,
                            onClick = { tempFilter["rating"] = rating }
                        )
                    }
                }
            }

            // SECTION: ASSIGNED TO
            FilterExpandableSection(title = "Assigned To", icon = Icons.Default.AssignmentInd) {
                SearchableFilterField(
                    value = tempFilter["assignedTo"] ?: "",
                    onValueChange = { tempFilter["assignedTo"] = it },
                    placeholder = "Search assigned user...",
                    suggestions = assigneeSuggestions
                )
            }

            // SECTION: DEAL STAGE
            FilterExpandableSection(title = "Deal Stage", icon = Icons.Default.StackedLineChart) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    viewModel.getAvailableStages().forEach { stage ->
                        FilterChip(
                            label = stage,
                            isSelected = tempFilter["stage"] == stage,
                            onClick = { tempFilter["stage"] = stage }
                        )
                    }
                }
            }

            // SECTION: PRODUCT
            FilterExpandableSection(title = "Product", icon = Icons.Default.Inventory) {
                SearchableFilterField(
                    value = tempFilter["product"] ?: "",
                    onValueChange = { tempFilter["product"] = it },
                    placeholder = "Search product...",
                    suggestions = productSuggestions
                )
            }

            // SECTION: TAG
            FilterExpandableSection(title = "Tag", icon = Icons.Default.Label) {
                OutlinedTextField(
                    value = tempFilter["tag"] ?: "",
                    onValueChange = { tempFilter["tag"] = it },
                    placeholder = { Text("Filter by tag") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true, // FORCES DONE BUTTON
                    leadingIcon = { Icon(Icons.Default.Tag, null) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                )
            }

            // SECTION: DEAL VALUE
            FilterExpandableSection(title = "Deal Value", icon = Icons.Default.Payments) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = tempFilter["valueMin"] ?: "",
                        onValueChange = { tempFilter["valueMin"] = it },
                        label = { Text("Min") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true, // FORCES DONE BUTTON
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                    )
                    Text("-")
                    OutlinedTextField(
                        value = tempFilter["valueMax"] ?: "",
                        onValueChange = { tempFilter["valueMax"] = it },
                        label = { Text("Max") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true, // FORCES DONE BUTTON
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
