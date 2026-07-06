package com.akashicsoft.crm.activity.ui

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
import com.akashicsoft.crm.activity.model.*
import com.akashicsoft.crm.activity.ui.components.SearchBottomSheet
import com.akashicsoft.crm.activity.ui.components.TimePickerDialog
import com.akashicsoft.crm.activity.viewmodel.CreateActivityViewModel
import com.akashicsoft.crm.ui.FormSection
import com.akashicsoft.crm.ui.LeadDropdownField
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateActivityScreen(
    viewModel: CreateActivityViewModel,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit
) {
    val isSaved by viewModel.isSaved.collectAsState()
    val error by viewModel.error.collectAsState()
    val scrollState = rememberScrollState()

    var showContactSearch by remember { mutableStateOf(false) }
    var showDealSearch by remember { mutableStateOf(false) }

    // Date Picker State
    var showDatePicker by remember { mutableStateOf(false) }
    var pickingStartDate by remember { mutableStateOf(true) }
    val datePickerState = rememberDatePickerState()

    // Time Picker State
    var showTimePicker by remember { mutableStateOf(false) }
    var pickingStartTime by remember { mutableStateOf(true) }
    val timePickerState = rememberTimePickerState()

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
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. Schedule Section
        FormSection(title = "Schedule", icon = Icons.Default.CalendarToday) {
            OutlinedTextField(
                value = viewModel.title.value,
                onValueChange = { viewModel.title.value = it },
                label = { Text("Activity Title*") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("All Day", modifier = Modifier.weight(1f))
                Switch(
                    checked = viewModel.isAllDay.value,
                    onCheckedChange = { viewModel.isAllDay.value = it }
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f).clickable {
                    pickingStartDate = true
                    showDatePicker = true
                }) {
                    OutlinedTextField(
                        value = viewModel.startDate.value.toString(),
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        label = { Text("Start Date") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = { Icon(Icons.Default.CalendarToday, null) },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = Color.Black,
                            disabledBorderColor = Color.Gray,
                            disabledLabelColor = Color.Gray,
                            disabledTrailingIconColor = Color.Gray,
                            disabledContainerColor = Color.Transparent
                        )
                    )
                }
                if (!viewModel.isAllDay.value) {
                    Box(modifier = Modifier.weight(1f).clickable {
                        pickingStartTime = true
                        showTimePicker = true
                    }) {
                        OutlinedTextField(
                            value = viewModel.startTime.value,
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            label = { Text("Start Time") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            trailingIcon = { Icon(Icons.Default.AccessTime, null) },
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = Color.Black,
                                disabledBorderColor = Color.Gray,
                                disabledLabelColor = Color.Gray,
                                disabledTrailingIconColor = Color.Gray,
                                disabledContainerColor = Color.Transparent
                            )
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f).clickable {
                    pickingStartDate = false
                    showDatePicker = true
                }) {
                    OutlinedTextField(
                        value = viewModel.endDate.value.toString(),
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        label = { Text("End Date") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = { Icon(Icons.Default.CalendarToday, null) },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = Color.Black,
                            disabledBorderColor = Color.Gray,
                            disabledLabelColor = Color.Gray,
                            disabledTrailingIconColor = Color.Gray,
                            disabledContainerColor = Color.Transparent
                        )
                    )
                }
                if (!viewModel.isAllDay.value) {
                    Box(modifier = Modifier.weight(1f).clickable {
                        pickingStartTime = false
                        showTimePicker = true
                    }) {
                        OutlinedTextField(
                            value = viewModel.endTime.value,
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            label = { Text("End Time") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            trailingIcon = { Icon(Icons.Default.AccessTime, null) },
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = Color.Black,
                                disabledBorderColor = Color.Gray,
                                disabledLabelColor = Color.Gray,
                                disabledTrailingIconColor = Color.Gray,
                                disabledContainerColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }

        // 2. Classification Section
        FormSection(title = "Classification", icon = Icons.Default.Category) {
            LeadDropdownField(
                label = "Activity Type*",
                selectedValue = viewModel.activityType.value.name.lowercase().replaceFirstChar { it.uppercase() },
                options = viewModel.activityTypeOptions.map { it.name.lowercase().replaceFirstChar { c -> c.uppercase() } },
                onOptionSelected = { name ->
                    viewModel.activityType.value = ActivityType.valueOf(name.uppercase())
                }
            )

            LeadDropdownField(
                label = "Source",
                selectedValue = viewModel.source.value.name.lowercase().replaceFirstChar { it.uppercase() },
                options = viewModel.sourceOptions.map { it.name.lowercase().replaceFirstChar { c -> c.uppercase() } },
                onOptionSelected = { name ->
                    viewModel.source.value = ActivitySource.valueOf(name.uppercase())
                }
            )

            LeadDropdownField(
                label = "Priority",
                selectedValue = viewModel.priority.value.name.lowercase().replaceFirstChar { it.uppercase() },
                options = viewModel.priorityOptions.map { it.name.lowercase().replaceFirstChar { c -> c.uppercase() } },
                onOptionSelected = { name ->
                    viewModel.priority.value = ActivityPriority.valueOf(name.uppercase())
                }
            )
        }

        // 3. Relationships Section
        FormSection(title = "Relationships", icon = Icons.Default.People) {
            // Contact Search
            Box(modifier = Modifier.fillMaxWidth().clickable { showContactSearch = true }) {
                OutlinedTextField(
                    value = viewModel.selectedContact.value?.name ?: "Select Contact",
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    label = { Text("Contact") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Person, null) },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = if (viewModel.selectedContact.value != null) Color.Black else Color.Gray,
                        disabledBorderColor = Color.Gray,
                        disabledLabelColor = Color.Gray,
                        disabledLeadingIconColor = Color.Gray,
                        disabledContainerColor = Color.Transparent
                    )
                )
            }

            // Organization (Auto-filled)
            OutlinedTextField(
                value = viewModel.organizationName.value,
                onValueChange = { viewModel.organizationName.value = it },
                label = { Text("Organization Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Business, null) },
                helperText = { Text("Auto-filled from contact", fontSize = 10.sp) }
            )

            // Deal Search
            Box(modifier = Modifier.fillMaxWidth().clickable { showDealSearch = true }) {
                OutlinedTextField(
                    value = viewModel.selectedDeal.value?.title ?: "Select Deal",
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    label = { Text("Deal") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Handshake, null) },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = if (viewModel.selectedDeal.value != null) Color.Black else Color.Gray,
                        disabledBorderColor = Color.Gray,
                        disabledLabelColor = Color.Gray,
                        disabledLeadingIconColor = Color.Gray,
                        disabledContainerColor = Color.Transparent
                    )
                )
            }
        }

        // 4. Additional Info Section
        FormSection(title = "Additional Info", icon = Icons.Default.Info) {
            OutlinedTextField(
                value = viewModel.location.value,
                onValueChange = { viewModel.location.value = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Place, null) }
            )

            LeadDropdownField(
                label = "Reminder",
                selectedValue = viewModel.reminder.value,
                options = viewModel.reminderOptions,
                onOptionSelected = { viewModel.reminder.value = it }
            )

            OutlinedTextField(
                value = viewModel.agenda.value,
                onValueChange = { viewModel.agenda.value = it },
                label = { Text("Agenda") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.List, null) }
            )
        }

        // 5. Assignment & Notes Section
        FormSection(title = "Assignment & Notes", icon = Icons.Default.AssignmentInd) {
            OutlinedTextField(
                value = viewModel.product.value,
                onValueChange = { viewModel.product.value = it },
                label = { Text("Product/Service") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Inventory2, null) }
            )

            LeadDropdownField(
                label = "Assigned To",
                selectedValue = viewModel.assignedTo.value?.name ?: "Select User",
                options = viewModel.availableOwners.map { it.name },
                onOptionSelected = { name ->
                    viewModel.assignedTo.value = viewModel.availableOwners.first { it.name == name }
                }
            )

            OutlinedTextField(
                value = viewModel.notes.value,
                onValueChange = { viewModel.notes.value = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(12.dp)
            )
        }

        if (error != null) {
            Text(text = error!!, color = MaterialTheme.colorScheme.error)
        }

        // Footer Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE91E63))
            ) {
                Text("Cancel", color = Color(0xFFE91E63))
            }

            Button(
                onClick = { viewModel.saveActivity() },
                modifier = Modifier.weight(1f).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Check, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }

    // Bottom Sheets & Dialogs
    if (showContactSearch) {
        SearchBottomSheet(
            title = "Search Contacts",
            onDismiss = { showContactSearch = false },
            onSearch = { query -> viewModel.searchContacts(query) },
            onItemsSelected = { items ->
                items.firstOrNull()?.let { viewModel.onContactSelected(it as ActivityParticipant) }
                showContactSearch = false
            },
            multiSelect = false
        )
    }

    if (showDealSearch) {
        SearchBottomSheet(
            title = "Search Deals",
            onDismiss = { showDealSearch = false },
            onSearch = { query -> viewModel.searchDeals(query) },
            onItemsSelected = { items ->
                items.firstOrNull()?.let { viewModel.selectedDeal.value = it as ActivityRelatedRecord }
                showDealSearch = false
            },
            multiSelect = false
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.fromEpochMilliseconds(millis)
                            .toLocalDateTime(TimeZone.currentSystemDefault())
                            .date
                        if (pickingStartDate) {
                            viewModel.startDate.value = date
                        } else {
                            viewModel.endDate.value = date
                        }
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val minute = if (timePickerState.minute < 10) "0${timePickerState.minute}" else "${timePickerState.minute}"
                    val amPm = if (timePickerState.hour < 12) "AM" else "PM"
                    val displayHour = when {
                        timePickerState.hour == 0 -> 12
                        timePickerState.hour > 12 -> timePickerState.hour - 12
                        else -> timePickerState.hour
                    }
                    val formattedTime = "${if (displayHour < 10) "0$displayHour" else displayHour}:$minute $amPm"
                    
                    if (pickingStartTime) {
                        viewModel.startTime.value = formattedTime
                    } else {
                        viewModel.endTime.value = formattedTime
                    }
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }
}

@Composable
private fun OutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(12.dp),
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    enabled: Boolean = true,
    readOnly: Boolean = false,
    helperText: @Composable (() -> Unit)? = null
) {
    Column(modifier = modifier) {
        androidx.compose.material3.OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = label,
            modifier = Modifier.fillMaxWidth(),
            shape = shape,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            colors = colors,
            enabled = enabled,
            readOnly = readOnly
        )
        if (helperText != null) {
            Box(modifier = Modifier.padding(start = 12.dp, top = 4.dp)) {
                helperText()
            }
        }
    }
}
