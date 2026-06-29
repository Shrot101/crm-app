package com.akashicsoft.crm.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akashicsoft.crm.ui.components.CrmTopAppBar
import com.akashicsoft.crm.viewModel.EditOrgViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditOrgScreen(
    viewModel: EditOrgViewModel,
    orgId: String,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit
) {
    val isSaved by viewModel.isSaved.collectAsState()
    val error   by viewModel.error.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(orgId)   { viewModel.loadOrg(orgId) }
    LaunchedEffect(isSaved) { if (isSaved) onNavigateBack() }

    Scaffold(
        topBar = {
            CrmTopAppBar(
                title       = "Edit Organization",
                isSubScreen = true,
                onMenuClick = onNavigateBack
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ── Organization Identity ─────────────────────────────────────────────
            EditOrgSectionHeader("Organization Identity")

            OutlinedTextField(
                value         = viewModel.organizationName.value,
                onValueChange = { viewModel.organizationName.value = it },
                label         = { Text("Organization Name*") },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                leadingIcon   = { Icon(Icons.Default.Business, null) },
                singleLine    = true
            )

            OutlinedTextField(
                value         = viewModel.website.value,
                onValueChange = { viewModel.website.value = it },
                label         = { Text("Website") },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                leadingIcon   = { Icon(Icons.Default.Language, null) },
                singleLine    = true
            )

            // ── Classification ────────────────────────────────────────────────────
            EditOrgSectionHeader("Classification")

            EditOrgDropdownField(
                label            = "Industry",
                selectedValue    = viewModel.industry.value,
                options          = viewModel.industryOptions,
                onOptionSelected = { viewModel.industry.value = it },
                leadingIcon      = { Icon(Icons.Default.Factory, null) }
            )

            EditOrgDropdownField(
                label            = "Type",
                selectedValue    = viewModel.type.value,
                options          = viewModel.typeOptions,
                onOptionSelected = { viewModel.type.value = it },
                isAccent         = true,
                leadingIcon      = { Icon(Icons.Default.Category, null) }
            )

            EditOrgDropdownField(
                label            = "Lead Source",
                selectedValue    = viewModel.leadSource.value,
                options          = viewModel.leadSourceOptions,
                onOptionSelected = { viewModel.leadSource.value = it },
                leadingIcon      = { Icon(Icons.Default.Campaign, null) }
            )

            // ── Company Details ───────────────────────────────────────────────────
            EditOrgSectionHeader("Company Details")

            OutlinedTextField(
                value         = viewModel.numberOfEmployees.value,
                onValueChange = { viewModel.numberOfEmployees.value = it },
                label         = { Text("No. of Employees") },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                leadingIcon   = { Icon(Icons.Default.People, null) },
                singleLine    = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value         = viewModel.targetAmount.value,
                onValueChange = { viewModel.targetAmount.value = it },
                label         = { Text("Target Amount ($)") },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                leadingIcon   = { Icon(Icons.Default.AttachMoney, null) },
                singleLine    = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            OutlinedTextField(
                value         = viewModel.associatedCompany.value,
                onValueChange = { viewModel.associatedCompany.value = it },
                label         = { Text("Associated Company") },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                leadingIcon   = { Icon(Icons.Default.AccountTree, null) },
                singleLine    = true
            )

            // ── Location ──────────────────────────────────────────────────────────
            EditOrgSectionHeader("Location")

            OutlinedTextField(
                value         = viewModel.address.value,
                onValueChange = { viewModel.address.value = it },
                label         = { Text("Address") },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                leadingIcon   = { Icon(Icons.Default.Home, null) },
                singleLine    = true
            )

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value         = viewModel.city.value,
                    onValueChange = { viewModel.city.value = it },
                    label         = { Text("City") },
                    modifier      = Modifier.weight(1f),
                    shape         = RoundedCornerShape(12.dp),
                    leadingIcon   = { Icon(Icons.Default.LocationCity, null) },
                    singleLine    = true
                )
                OutlinedTextField(
                    value         = viewModel.state.value,
                    onValueChange = { viewModel.state.value = it },
                    label         = { Text("State") },
                    modifier      = Modifier.weight(1f),
                    shape         = RoundedCornerShape(12.dp),
                    leadingIcon   = { Icon(Icons.Default.Map, null) },
                    singleLine    = true
                )
            }

            // ── Assignment ────────────────────────────────────────────────────────
            EditOrgSectionHeader("Assignment")

            EditOrgDropdownField(
                label            = "Assigned To",
                selectedValue    = viewModel.assignedTo.value,
                options          = viewModel.agentOptions,
                onOptionSelected = { viewModel.assignedTo.value = it },
                leadingIcon      = { Icon(Icons.Default.PersonPin, null) }
            )

            // ── Error ─────────────────────────────────────────────────────────────
            if (error != null) {
                Text(
                    text     = error!!,
                    color    = MaterialTheme.colorScheme.error,
                    style    = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            // ── Action Buttons ────────────────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick  = { viewModel.saveChanges() },
                    modifier = Modifier.weight(1f).height(52.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                    shape    = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Check, null)
                    Spacer(Modifier.width(6.dp))
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick  = onNavigateBack,
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape    = RoundedCornerShape(12.dp),
                    border   = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE91E63))
                ) {
                    Text("Cancel", color = Color(0xFFE91E63), fontWeight = FontWeight.Medium)
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Private helpers – prefixed with "EditOrg" to avoid name clashes
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun EditOrgSectionHeader(title: String) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE0E0E0))
        Text(
            text       = title.uppercase(),
            style      = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color      = Color(0xFF9E9E9E),
            fontSize   = 10.sp
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE0E0E0))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditOrgDropdownField(
    label: String,
    selectedValue: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    isAccent: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }
    val accentColor = Color(0xFF6200EE)
    ExposedDropdownMenuBox(
        expanded         = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier         = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value         = selectedValue,
            onValueChange = {},
            readOnly      = true,
            label         = { Text(label) },
            leadingIcon   = leadingIcon,
            trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier      = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
            shape         = RoundedCornerShape(12.dp),
            colors        = OutlinedTextFieldDefaults.colors(
                focusedTextColor   = if (isAccent) accentColor else Color.Unspecified,
                unfocusedTextColor = if (isAccent) accentColor else Color.Unspecified
            ),
            textStyle = if (isAccent)
                MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
            else LocalTextStyle.current
        )
        ExposedDropdownMenu(
            expanded         = expanded,
            onDismissRequest = { expanded = false },
            modifier         = Modifier.background(Color.White)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text           = { Text(option) },
                    onClick        = { onOptionSelected(option); expanded = false },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}