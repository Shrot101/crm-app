package com.akashicsoft.crm.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.akashicsoft.crm.data.local.FakeLeadsData

@Composable
fun LeadFilterDialog(
    currentFilter: Map<String, String>,
    onFilterChange: (Map<String, String>) -> Unit,
    onDismiss: () -> Unit
) {
    var tempFilter by remember { mutableStateOf(currentFilter.toMutableMap()) }
    val filterType = tempFilter["filterType"] ?: "Filter"
    
    val timeOptions = listOf("Today", "Yesterday", "This Week", "Last Week", "This Month", "Custom")
    val frequencyOptions = listOf("Daily", "Weekly", "Monthly")
    val agents = FakeLeadsData.getMockUsers().map { it.name }
    val departments = listOf("Sales", "Marketing", "Support", "Engineering")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Lead Filters",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6200EE)
                )

                // Filter Type Dropdown (Primary Selector)
                FilterDropdownField(
                    label = "Filter type (Lead/Ticket)",
                    selectedValue = filterType,
                    options = listOf("Filter", "Department", "Agent"),
                    onOptionSelected = {
                        tempFilter.clear()
                        tempFilter["filterType"] = it
                    }
                )

                // Conditional Fields based on Type
                when (filterType) {
                    "Department" -> {
                        FilterDropdownField(
                            label = "Department",
                            selectedValue = tempFilter["department"] ?: "Select Department",
                            options = departments,
                            onOptionSelected = { tempFilter["department"] = it }
                        )
                    }
                    "Agent" -> {
                        FilterDropdownField(
                            label = "Agent",
                            selectedValue = tempFilter["agent"] ?: "Select Agent",
                            options = agents,
                            onOptionSelected = { tempFilter["agent"] = it }
                        )
                    }
                }

                // Common Time Filters (Today/Daily style from images)
                FilterDropdownField(
                    label = "Filter",
                    selectedValue = tempFilter["timeRange"] ?: "Today",
                    options = timeOptions,
                    onOptionSelected = { tempFilter["timeRange"] = it }
                )

                FilterDropdownField(
                    label = "Filter",
                    selectedValue = tempFilter["frequency"] ?: "Daily",
                    options = frequencyOptions,
                    onOptionSelected = { tempFilter["frequency"] = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onFilterChange(tempFilter)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6200EE)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Apply Filters", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterDropdownField(
    label: String,
    selectedValue: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                .clickable { expanded = true }
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = selectedValue, fontSize = 16.sp)
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = Color(0xFF6200EE)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.7f).background(Color.White)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }) {
                        Text(text = option)
                    }
                }
            }
        }
    }
}
