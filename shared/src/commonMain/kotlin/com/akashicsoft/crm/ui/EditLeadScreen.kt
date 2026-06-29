package com.akashicsoft.crm.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.akashicsoft.crm.viewModel.EditLeadViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditLeadScreen(
    viewModel: EditLeadViewModel,
    leadId: String,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit
) {
    val isSaved by viewModel.isSaved.collectAsState()
    val error by viewModel.error.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(leadId) {
        viewModel.loadLead(leadId)
    }

    LaunchedEffect(isSaved) {
        if (isSaved) {
            onNavigateBack()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Salutation Dropdown
        LeadDropdownField(
            label = "Salutation",
            selectedValue = viewModel.salutation.value,
            options = viewModel.salutationOptions,
            onOptionSelected = { viewModel.salutation.value = it }
        )

        OutlinedTextField(
            value = viewModel.firstName.value,
            onValueChange = { viewModel.firstName.value = it },
            label = { Text("First Name*") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = viewModel.lastName.value,
            onValueChange = { viewModel.lastName.value = it },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        // Dynamic Emails Section
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            viewModel.emails.forEachIndexed { index, emailContact ->
                OutlinedTextField(
                    value = emailContact.email,
                    onValueChange = { viewModel.updateEmail(index, it) },
                    label = { Text(if (index == 0) "Primary Email*" else "Secondary Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        if (index > 0) {
                            IconButton(onClick = { viewModel.removeEmail(index) }) {
                                Icon(Icons.Default.RemoveCircle, null, tint = Color(0xFFE91E63))
                            }
                        }
                    }
                )
            }
            Text(
                text = "+ Add Contact",
                color = Color(0xFF6200EE),
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clickable { viewModel.addEmail() }
                    .padding(vertical = 4.dp)
            )
        }

        // Dynamic Phones Section (Mobile)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            viewModel.phones.forEachIndexed { index, phoneContact ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.width(100.dp)) {
                        CountryCodeDropdown(
                            selectedCode = phoneContact.countryCode ?: "+91",
                            options = viewModel.countryCodes,
                            onCodeSelected = { viewModel.updatePhoneCountryCode(index, it) }
                        )
                    }

                    OutlinedTextField(
                        value = phoneContact.phone,
                        onValueChange = { viewModel.updatePhone(index, it) },
                        label = { Text(if (index == 0) "Primary Mobile*" else "Secondary Mobile") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            if (index > 0) {
                                IconButton(onClick = { viewModel.removePhone(index) }) {
                                    Icon(Icons.Default.RemoveCircle, null, tint = Color(0xFFE91E63))
                                }
                            }
                        }
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "+ Add Phone",
                    color = Color(0xFF6200EE),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable { viewModel.addPhone() }
                        .padding(vertical = 4.dp)
                )

                if (viewModel.landlines.isEmpty()) {
                    Text(
                        text = "+ Add Landline",
                        color = Color(0xFF6200EE),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clickable { viewModel.addLandline() }
                            .padding(vertical = 4.dp)
                    )
                }
            }
        }

        // Optional Landline Section (Fields only)
        if (viewModel.landlines.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                viewModel.landlines.forEachIndexed { index, landlineContact ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(modifier = Modifier.width(100.dp)) {
                            CountryCodeDropdown(
                                selectedCode = landlineContact.countryCode ?: "+91",
                                options = viewModel.countryCodes,
                                onCodeSelected = { viewModel.updateLandlineCountryCode(index, it) }
                            )
                        }

                        OutlinedTextField(
                            value = landlineContact.phone,
                            onValueChange = { viewModel.updateLandline(index, it) },
                            label = { Text("Landline Number") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            trailingIcon = {
                                IconButton(onClick = { viewModel.removeLandline(index) }) {
                                    Icon(Icons.Default.RemoveCircle, null, tint = Color(0xFFE91E63))
                                }
                            }
                        )
                    }
                }
            }
        }

        // Organization & Designation
        OutlinedTextField(
            value = viewModel.organizationName.value,
            onValueChange = { viewModel.organizationName.value = it },
            label = { Text("Organization Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = viewModel.designation.value,
            onValueChange = { viewModel.designation.value = it },
            label = { Text("Designation") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        // Status and Lead Source Row
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                LeadDropdownField(
                    label = "Status",
                    selectedValue = viewModel.status.value,
                    options = viewModel.statusOptions,
                    onOptionSelected = { viewModel.status.value = it },
                    isAccent = true
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                LeadDropdownField(
                    label = "Lead Source",
                    selectedValue = viewModel.leadSource.value,
                    options = viewModel.sourceOptions,
                    onOptionSelected = { viewModel.leadSource.value = it },
                    isAccent = true
                )
            }
        }

        // Assigned To
        Column {
            Text("Assigned To", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            LeadDropdownField(
                label = "Select User",
                selectedValue = viewModel.assignedOwner.value?.name ?: "Select User",
                options = viewModel.availableOwners.map { it.name },
                onOptionSelected = { name ->
                    viewModel.assignedOwner.value = viewModel.availableOwners.first { it.name == name }
                },
                leadingIcon = { Icon(Icons.Default.Search, null) }
            )
        }

        // Notes Section
        OutlinedTextField(
            value = viewModel.notes.value,
            onValueChange = { viewModel.notes.value = it },
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            shape = RoundedCornerShape(12.dp)
        )

        if (error != null) {
            Text(text = error!!, color = MaterialTheme.colorScheme.error)
        }

        // Footer Actions
        Button(
            onClick = { viewModel.saveChanges() },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Check, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save Changes", fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
            onClick = onNavigateBack,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE91E63))
        ) {
            Text("Cancel", color = Color(0xFFE91E63))
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}
