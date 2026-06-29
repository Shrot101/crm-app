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
import androidx.compose.ui.unit.sp
import com.akashicsoft.crm.ui.components.CrmTopAppBar
import com.akashicsoft.crm.viewModel.EditContactViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditContactScreen(
    viewModel: EditContactViewModel,
    contactId: String,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit
) {
    val isSaved by viewModel.isSaved.collectAsState()
    val error   by viewModel.error.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(contactId) { viewModel.loadContact(contactId) }
    LaunchedEffect(isSaved)   { if (isSaved) onNavigateBack() }

    Scaffold(
        topBar = {
            CrmTopAppBar(
                title       = "Edit Contact",
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

            // ── Personal Info ─────────────────────────────────────────────────
            EditContactSectionHeader("Personal Info")

            EditContactDropdownField(
                label            = "Salutation",
                selectedValue    = viewModel.salutation.value,
                options          = viewModel.salutationOptions,
                onOptionSelected = { viewModel.salutation.value = it }
            )

            OutlinedTextField(
                value         = viewModel.name.value,
                onValueChange = { viewModel.name.value = it },
                label         = { Text("Full Name*") },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                leadingIcon   = { Icon(Icons.Default.Person, null) },
                singleLine    = true
            )

            // Emails
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                viewModel.emails.forEachIndexed { index, emailValue ->
                    OutlinedTextField(
                        value         = emailValue,
                        onValueChange = { viewModel.updateEmail(index, it) },
                        label         = { Text(if (index == 0) "Email Address*" else "Secondary Email") },
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(12.dp),
                        leadingIcon   = { Icon(Icons.Default.Email, null) },
                        singleLine    = true,
                        trailingIcon  = if (index > 0) ({
                            IconButton(onClick = { viewModel.removeEmail(index) }) {
                                Icon(Icons.Default.RemoveCircle, null, tint = Color(0xFFE91E63))
                            }
                        }) else null
                    )
                }
                Text(
                    text       = "+ Add Email",
                    color      = Color(0xFF6200EE),
                    fontWeight = FontWeight.Medium,
                    fontSize   = 14.sp,
                    modifier   = Modifier
                        .clickable { viewModel.addEmail() }
                        .padding(vertical = 2.dp)
                )
            }

            // ── Phone ─────────────────────────────────────────────────────────
            EditContactSectionHeader("Phone")

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                // Mobile numbers
                viewModel.phones.forEachIndexed { index, phoneEntry ->
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(modifier = Modifier.width(110.dp)) {
                            EditContactCountryCodeDropdown(
                                selectedCode   = phoneEntry.countryCode,
                                options        = viewModel.countryCodes,
                                onCodeSelected = { viewModel.updatePhoneCode(index, it) }
                            )
                        }
                        OutlinedTextField(
                            value         = phoneEntry.number,
                            onValueChange = { viewModel.updatePhone(index, it) },
                            label         = { Text(if (index == 0) "Mobile Number*" else "Mobile ${index + 1}") },
                            modifier      = Modifier.weight(1f),
                            shape         = RoundedCornerShape(12.dp),
                            leadingIcon   = { Icon(Icons.Default.PhoneAndroid, null) },
                            singleLine    = true,
                            trailingIcon  = if (index > 0) ({
                                IconButton(onClick = { viewModel.removePhone(index) }) {
                                    Icon(Icons.Default.RemoveCircle, null, tint = Color(0xFFE91E63))
                                }
                            }) else null
                        )
                    }
                }

                // Landline numbers
                viewModel.landlines.forEachIndexed { index, landlineEntry ->
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(modifier = Modifier.width(110.dp)) {
                            EditContactCountryCodeDropdown(
                                selectedCode   = landlineEntry.countryCode,
                                options        = viewModel.countryCodes,
                                onCodeSelected = { viewModel.updateLandlineCode(index, it) }
                            )
                        }
                        OutlinedTextField(
                            value         = landlineEntry.number,
                            onValueChange = { viewModel.updateLandline(index, it) },
                            label         = { Text("Landline Number") },
                            modifier      = Modifier.weight(1f),
                            shape         = RoundedCornerShape(12.dp),
                            leadingIcon   = { Icon(Icons.Default.Call, null) },
                            singleLine    = true,
                            trailingIcon  = {
                                IconButton(onClick = { viewModel.removeLandline(index) }) {
                                    Icon(Icons.Default.RemoveCircle, null, tint = Color(0xFFE91E63))
                                }
                            }
                        )
                    }
                }

                // Add-more row
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text       = "+ Add Phone",
                        color      = Color(0xFF6200EE),
                        fontWeight = FontWeight.Medium,
                        fontSize   = 14.sp,
                        modifier   = Modifier
                            .clickable { viewModel.addPhone() }
                            .padding(vertical = 2.dp)
                    )
                    Text(
                        text       = "+ Add Landline",
                        color      = Color(0xFF6200EE),
                        fontWeight = FontWeight.Medium,
                        fontSize   = 14.sp,
                        modifier   = Modifier
                            .clickable { viewModel.addLandline() }
                            .padding(vertical = 2.dp)
                    )
                }
            }

            // ── Professional Info ─────────────────────────────────────────────
            EditContactSectionHeader("Professional Info")

            OutlinedTextField(
                value         = viewModel.organization.value,
                onValueChange = { viewModel.organization.value = it },
                label         = { Text("Organization*") },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                leadingIcon   = { Icon(Icons.Default.Business, null) },
                singleLine    = true
            )
            OutlinedTextField(
                value         = viewModel.designation.value,
                onValueChange = { viewModel.designation.value = it },
                label         = { Text("Designation*") },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                leadingIcon   = { Icon(Icons.Default.Badge, null) },
                singleLine    = true
            )
            OutlinedTextField(
                value         = viewModel.department.value,
                onValueChange = { viewModel.department.value = it },
                label         = { Text("Department") },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                leadingIcon   = { Icon(Icons.Default.Groups, null) },
                singleLine    = true
            )

            // ── Contact Source ────────────────────────────────────────────────
            EditContactSectionHeader("Contact Source")

            EditContactDropdownField(
                label            = "Source",
                selectedValue    = viewModel.source.value,
                options          = viewModel.sourceOptions,
                onOptionSelected = { viewModel.source.value = it },
                isAccent         = true
            )

            // ── Preferences ───────────────────────────────────────────────────
            EditContactSectionHeader("Preferences")

            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8F9FE), RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFB400), modifier = Modifier.size(20.dp))
                    Column {
                        Text("Mark as Favourite", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        Text("Contact will appear in the starred section", fontSize = 11.sp, color = Color.Gray)
                    }
                }
                Switch(
                    checked         = viewModel.isFavorite.value,
                    onCheckedChange = { viewModel.isFavorite.value = it },
                    colors          = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF6200EE)
                    )
                )
            }

            // ── Error ─────────────────────────────────────────────────────────
            if (error != null) {
                Text(
                    text     = error!!,
                    color    = MaterialTheme.colorScheme.error,
                    style    = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            // ── Action Buttons ────────────────────────────────────────────────
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
//  Private helpers — prefixed with "EditContact" to avoid name collisions with
//  CreateContactScreen's private helpers in the same compilation unit
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun EditContactSectionHeader(title: String) {
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
private fun EditContactDropdownField(
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
            modifier      = Modifier.menuAnchor().fillMaxWidth(),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditContactCountryCodeDropdown(
    selectedCode: String,
    options: List<Pair<String, String>>,
    onCodeSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedOption = options.find { it.second == selectedCode } ?: options.first()
    ExposedDropdownMenuBox(
        expanded         = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value         = "${selectedOption.first} ${selectedOption.second}",
            onValueChange = {},
            readOnly      = true,
            modifier      = Modifier.menuAnchor(),
            shape         = RoundedCornerShape(12.dp),
            textStyle     = MaterialTheme.typography.bodyMedium,
            colors        = OutlinedTextFieldDefaults.colors(
                focusedContainerColor   = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )
        ExposedDropdownMenu(
            expanded         = expanded,
            onDismissRequest = { expanded = false },
            modifier         = Modifier.background(Color.White)
        ) {
            options.forEach { (flag, code) ->
                DropdownMenuItem(
                    text    = { Text("$flag $code") },
                    onClick = { onCodeSelected(code); expanded = false }
                )
            }
        }
    }
}
