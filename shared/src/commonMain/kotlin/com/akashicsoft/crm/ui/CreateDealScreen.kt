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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akashicsoft.crm.ui.components.CrmTopAppBar
import com.akashicsoft.crm.viewModel.CreateDealViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDealScreen(
    viewModel: CreateDealViewModel,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit
) {
    val isSaved by viewModel.isSaved.collectAsState()
    val error by viewModel.error.collectAsState()
    val scrollState = rememberScrollState()
    
    var tagText by remember { mutableStateOf("") }

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
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // BLOCK 1: IDENTITY
        FormSection(title = "Identity", icon = Icons.Default.Badge) {
            OutlinedTextField(
                value = viewModel.title.value,
                onValueChange = { viewModel.title.value = it },
                label = { Text("Deal Title*") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = viewModel.organization.value,
                onValueChange = { viewModel.organization.value = it },
                label = { Text("Organization Name*") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Business, null) }
            )
        }

        // BLOCK 2: KEY PERSONNEL
        FormSection(title = "Key Personnel", icon = Icons.Default.Groups) {
            OutlinedTextField(
                value = viewModel.contactPerson.value,
                onValueChange = { viewModel.contactPerson.value = it },
                label = { Text("Contact Person") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Person, null) }
            )
            LeadDropdownField(
                label = "Assigned To",
                selectedValue = viewModel.assignedOwner.value?.name ?: "Select User",
                options = viewModel.availableOwners.map { it.name },
                onOptionSelected = { name ->
                    viewModel.assignedOwner.value = viewModel.availableOwners.first { it.name == name }
                },
                leadingIcon = { Icon(Icons.Default.Search, null) }
            )
        }

        // BLOCK 3: CLASSIFICATION
        FormSection(title = "Classification", icon = Icons.Default.Category) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    LeadDropdownField(
                        label = "Category",
                        selectedValue = viewModel.category.value,
                        options = viewModel.availableCategories,
                        onOptionSelected = { viewModel.category.value = it }
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    LeadDropdownField(
                        label = "Product",
                        selectedValue = viewModel.product.value,
                        options = viewModel.availableProducts,
                        onOptionSelected = { viewModel.product.value = it }
                    )
                }
            }
        }

        // BLOCK 4: STATUS & QUALITY
        FormSection(title = "Status & Quality", icon = Icons.Default.Verified) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    LeadDropdownField(
                        label = "Deal Stage",
                        selectedValue = viewModel.stage.value,
                        options = viewModel.availableStages,
                        onOptionSelected = { viewModel.stage.value = it },
                        isAccent = true
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    LeadDropdownField(
                        label = "Rating",
                        selectedValue = viewModel.rating.value,
                        options = viewModel.availableRatings,
                        onOptionSelected = { viewModel.rating.value = it },
                        isAccent = true
                    )
                }
            }
        }

        // BLOCK 5: FINANCIALS
        FormSection(title = "Financials", icon = Icons.Default.Payments) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Currency Selector
                Box(modifier = Modifier.width(90.dp)) {
                    CurrencyDropdown(
                        selectedSymbol = viewModel.currency.value,
                        options = viewModel.currencies,
                        onSymbolSelected = { viewModel.currency.value = it }
                    )
                }

                OutlinedTextField(
                    value = viewModel.dealValue.value,
                    onValueChange = { viewModel.dealValue.value = it },
                    label = { Text("Deal Value") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        // BLOCK 6: TAGS & TIMING
        FormSection(title = "Tags & Timing", icon = Icons.Default.Label) {
            OutlinedTextField(
                value = viewModel.closeDate.value,
                onValueChange = { viewModel.closeDate.value = it },
                label = { Text("Close Date") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = { Icon(Icons.Default.CalendarToday, null) }
            )
            
            // Dynamic Tag Creation
            Column {
                OutlinedTextField(
                    value = tagText,
                    onValueChange = { tagText = it },
                    label = { Text("Add Tags") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        IconButton(onClick = {
                            if (tagText.isNotBlank()) {
                                viewModel.addTag(tagText)
                                tagText = ""
                            }
                        }) {
                            Icon(Icons.Default.Add, null)
                        }
                    }
                )
                if (viewModel.tags.isNotEmpty()) {
                    FlowRow(
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        viewModel.tags.forEach { tag ->
                            SuggestionChip(
                                onClick = { viewModel.removeTag(tag) },
                                label = { Text("#$tag") },
                                modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        // FINAL BLOCK: NOTES
        FormSection(title = "Description", icon = Icons.Default.Description) {
            OutlinedTextField(
                value = viewModel.finalNotes.value,
                onValueChange = { viewModel.finalNotes.value = it },
                label = { Text("Final Notes") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(12.dp)
            )
        }

        if (error != null) {
            Text(text = error!!, color = MaterialTheme.colorScheme.error)
        }

        // FOOTER ACTIONS
        Button(
            onClick = { viewModel.saveDeal() },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Check, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create Deal", fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
            onClick = onNavigateBack,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE91E63))
        ) {
            Text("Cancel", color = Color(0xFFE91E63))
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun FormSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyDropdown(
    selectedSymbol: String,
    options: List<Pair<String, String>>,
    onSymbolSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedOption = options.find { it.first == selectedSymbol } ?: options.first()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = "${selectedOption.first} ${selectedOption.second}",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.menuAnchor(),
            shape = RoundedCornerShape(12.dp),
            textStyle = MaterialTheme.typography.bodyMedium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            options.forEach { (symbol, code) ->
                DropdownMenuItem(
                    text = { Text("$symbol $code") },
                    onClick = {
                        onSymbolSelected(symbol)
                        expanded = false
                    }
                )
            }
        }
    }
}
