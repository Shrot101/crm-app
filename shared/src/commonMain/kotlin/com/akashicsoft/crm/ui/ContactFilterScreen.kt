package com.akashicsoft.crm.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import com.akashicsoft.crm.viewModel.ContactViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactFilterScreen(
    viewModel: ContactViewModel,
    onNavigateBack: () -> Unit
) {
    val currentFilter by viewModel.currentFilter.collectAsState()
    // Local mutable copy – written to VM only on "Apply"
    val tempFilter = remember { mutableStateMapOf<String, String>().apply { putAll(currentFilter) } }
    val scrollState = rememberScrollState()

    // ── Live active-count from tempFilter (drives the subtitle) ──────────────
    val activeCount = remember(tempFilter.toMap()) {
        val textKeys = listOf(
            "name", "email", "phone", "organization",
            "designation", "department", "salutation", "source"
        )
        var count = textKeys.count { key -> !tempFilter[key].isNullOrBlank() }
        if (tempFilter["isFavorite"] == "true") count++
        if (!tempFilter["createdFrom"].isNullOrBlank()) count++
        if (!tempFilter["createdTo"].isNullOrBlank()) count++
        count
    }

    // Suggestion data derived from the ViewModel (sourced from FakeContactsData)
    val nameSuggestions      = viewModel.getNameSuggestions()
    val orgSuggestions       = viewModel.getOrganizationSuggestions()
    val designationSuggestions = viewModel.getDesignationSuggestions()
    val departmentSuggestions  = viewModel.getDepartmentSuggestions()
    val sourceSuggestions      = viewModel.getAvailableSources()
    val salutationOptions      = viewModel.getAvailableSalutations()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text  = "Contact Filters",
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
                    // Reset all – clears tempFilter AND persists to VM immediately
                    IconButton(onClick = {
                        tempFilter.clear()
                        viewModel.updateFilter(emptyMap())
                    }) {
                        Icon(
                            Icons.Default.FilterListOff,
                            contentDescription = "Reset Filters",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6200EE))
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
                        border   = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF6200EE))
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
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape    = RoundedCornerShape(24.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
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

            // ── 2. NAME ────────────────────────────────────────────────────────
            FilterExpandableSection(title = "Name", icon = Icons.Default.Person) {
                SearchableFilterField(
                    value         = tempFilter["name"] ?: "",
                    onValueChange = { tempFilter["name"] = it },
                    placeholder   = "Search by name…",
                    suggestions   = nameSuggestions
                )
            }

            // ── 3. SALUTATION ─────────────────────────────────────────────────
            FilterExpandableSection(title = "Salutation", icon = Icons.Default.Badge) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Wrap chips in a flow-like row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        salutationOptions.forEach { salutation ->
                            FilterChip(
                                label      = salutation,
                                isSelected = tempFilter["salutation"] == salutation,
                                onClick    = {
                                    if (tempFilter["salutation"] == salutation) {
                                        tempFilter.remove("salutation") // deselect on re-tap
                                    } else {
                                        tempFilter["salutation"] = salutation
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // ── 4. EMAIL ───────────────────────────────────────────────────────
            FilterExpandableSection(title = "Email", icon = Icons.Default.Email) {
                SearchableFilterField(
                    value         = tempFilter["email"] ?: "",
                    onValueChange = { tempFilter["email"] = it },
                    placeholder   = "Search by email…",
                    suggestions   = emptyList()
                )
            }

            // ── 5. PHONE ───────────────────────────────────────────────────────
            FilterExpandableSection(title = "Phone", icon = Icons.Default.PhoneAndroid) {
                SearchableFilterField(
                    value         = tempFilter["phone"] ?: "",
                    onValueChange = { tempFilter["phone"] = it },
                    placeholder   = "Search by phone number…",
                    suggestions   = emptyList()
                )
            }

            // ── 6. ORGANIZATION ───────────────────────────────────────────────
            FilterExpandableSection(title = "Organization", icon = Icons.Default.Business) {
                SearchableFilterField(
                    value         = tempFilter["organization"] ?: "",
                    onValueChange = { tempFilter["organization"] = it },
                    placeholder   = "Search by organization…",
                    suggestions   = orgSuggestions
                )
            }

            // ── 7. DESIGNATION ────────────────────────────────────────────────
            FilterExpandableSection(title = "Designation", icon = Icons.Default.Work) {
                SearchableFilterField(
                    value         = tempFilter["designation"] ?: "",
                    onValueChange = { tempFilter["designation"] = it },
                    placeholder   = "Search by designation…",
                    suggestions   = designationSuggestions
                )
            }

            // ── 8. DEPARTMENT ─────────────────────────────────────────────────
            FilterExpandableSection(title = "Department", icon = Icons.Default.Groups) {
                SearchableFilterField(
                    value         = tempFilter["department"] ?: "",
                    onValueChange = { tempFilter["department"] = it },
                    placeholder   = "Search by department…",
                    suggestions   = departmentSuggestions
                )
            }

            // ── 9. SOURCE ─────────────────────────────────────────────────────
            FilterExpandableSection(title = "Contact Source", icon = Icons.Default.Hub) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    sourceSuggestions.chunked(3).forEach { rowSources ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowSources.forEach { source ->
                                FilterChip(
                                    label      = source,
                                    isSelected = tempFilter["source"] == source,
                                    onClick    = {
                                        if (tempFilter["source"] == source) {
                                            tempFilter.remove("source") // deselect on re-tap
                                        } else {
                                            tempFilter["source"] = source
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // ── 10. FAVOURITES ONLY ───────────────────────────────────────────
            FilterExpandableSection(title = "Favourites Only", icon = Icons.Default.Star) {
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8F9FE), RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            Icons.Default.Star,
                            null,
                            tint     = Color(0xFFFFB400),
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                "Show Favourites Only",
                                fontWeight = FontWeight.Medium,
                                fontSize   = 14.sp
                            )
                            Text(
                                "Only contacts marked as favourite",
                                fontSize = 11.sp,
                                color    = Color.Gray
                            )
                        }
                    }
                    Switch(
                        checked         = tempFilter["isFavorite"] == "true",
                        onCheckedChange = { isChecked ->
                            if (isChecked) tempFilter["isFavorite"] = "true"
                            else tempFilter.remove("isFavorite")
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF6200EE)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
