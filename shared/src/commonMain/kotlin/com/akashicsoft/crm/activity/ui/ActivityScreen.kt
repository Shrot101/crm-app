package com.akashicsoft.crm.activity.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.akashicsoft.crm.activity.model.Activity
import com.akashicsoft.crm.activity.model.ActivityType
import com.akashicsoft.crm.activity.ui.components.ActivityTimelineItem
import com.akashicsoft.crm.activity.ui.components.CalendarCard
import com.akashicsoft.crm.activity.ui.components.TimePickerDialog
import com.akashicsoft.crm.activity.util.CalendarUtils
import com.akashicsoft.crm.activity.viewmodel.ActivityViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(
    viewModel: ActivityViewModel,
    modifier: Modifier = Modifier,
    onCreateActivity: () -> Unit = {},
    onEditActivity: (String) -> Unit = {},
    onActivityClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val showTodayButton by viewModel.showTodayButton.collectAsState()
    val datesWithActivities by viewModel.datesWithActivities.collectAsState()
    
    // Action Sheet State
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var selectedActivityForActions by remember { mutableStateOf<Activity?>(null) }
    var showActionSheet by remember { mutableStateOf(false) }

    // Reschedule Picker State
    var showRescheduleDatePicker by remember { mutableStateOf(false) }
    var showRescheduleTimePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()
    var rescheduledDate by remember { mutableStateOf<LocalDate?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    Box(modifier = modifier.fillMaxSize().background(Color(0xFFF8F9FE))) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Calendar Card
            CalendarCard(
                monthTitle = uiState.monthTitle,
                selectedDate = uiState.selectedDate,
                showTodayButton = showTodayButton,
                datesWithActivities = datesWithActivities,
                onTodayClick = viewModel::goToToday,
                onDateSelected = viewModel::onDateSelected,
                onWeekSwiped = viewModel::onDateSelected
            )

            if (uiState.activities.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyStateContent()
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    ActivityHeaderRow(
                        date = uiState.selectedDate,
                        taskCount = uiState.activities.size
                    )
                    
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
                    ) {
                        itemsIndexed(uiState.activities) { index, activity ->
                            ActivityTimelineItem(
                                activity = activity,
                                isFirst = index == 0,
                                isLast = index == uiState.activities.size - 1,
                                onMenuClick = {
                                    selectedActivityForActions = activity
                                    showActionSheet = true
                                },
                                onItemClick = {
                                    onActivityClick(activity.id)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Action Bottom Sheet
        if (showActionSheet && selectedActivityForActions != null) {
            ModalBottomSheet(
                onDismissRequest = { showActionSheet = false },
                sheetState = sheetState,
                dragHandle = { BottomSheetDefaults.DragHandle() },
                containerColor = Color.White
            ) {
                ActionSheetContent(
                    activity = selectedActivityForActions!!,
                    onAction = { action ->
                        when (action) {
                            "complete" -> viewModel.toggleTaskCompletion(selectedActivityForActions!!.id)
                            "clone" -> {
                                viewModel.cloneActivity(selectedActivityForActions!!.id)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Activity cloned", actionLabel = "Undo")
                                }
                            }
                            "delete" -> viewModel.deleteActivity(selectedActivityForActions!!.id)
                            "reschedule" -> showRescheduleDatePicker = true
                            "edit" -> onEditActivity(selectedActivityForActions!!.id)
                        }
                        showActionSheet = false
                    }
                )
            }
        }

        // Reschedule Date Picker
        if (showRescheduleDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showRescheduleDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            rescheduledDate = Instant.fromEpochMilliseconds(millis)
                                .toLocalDateTime(TimeZone.currentSystemDefault())
                                .date
                            showRescheduleDatePicker = false
                            showRescheduleTimePicker = true
                        }
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showRescheduleDatePicker = false }) { Text("Cancel") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        // Reschedule Time Picker
        if (showRescheduleTimePicker) {
            TimePickerDialog(
                onDismissRequest = { showRescheduleTimePicker = false },
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
                        
                        rescheduledDate?.let { date ->
                            selectedActivityForActions?.let { activity ->
                                viewModel.rescheduleActivity(activity.id, date, formattedTime)
                            }
                        }
                        showRescheduleTimePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showRescheduleTimePicker = false }) { Text("Cancel") }
                }
            ) {
                TimePicker(state = timePickerState)
            }
        }

        // FAB - Navigates directly to Create Activity
        FloatingActionButton(
            onClick = onCreateActivity,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            containerColor = Color(0xFF3B229D),
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Activity",
                modifier = Modifier.size(24.dp)
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 90.dp)
        )
    }
}

@Composable
private fun ActionSheetContent(
    activity: Activity,
    onAction: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp)
    ) {
        // Task logic removed as per requirements

        ActionItem(
            icon = Icons.Default.Edit,
            label = "Edit",
            onClick = { onAction("edit") }
        )

        ActionItem(
            icon = Icons.Default.Event,
            label = "Reschedule",
            onClick = { onAction("reschedule") }
        )

        ActionItem(
            icon = Icons.Default.ContentCopy,
            label = "Clone",
            onClick = { onAction("clone") }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF0F0F0))

        ActionItem(
            icon = Icons.Default.Delete,
            label = "Delete",
            color = Color.Red,
            onClick = { onAction("delete") }
        )
    }
}

@Composable
private fun ActionItem(
    icon: ImageVector,
    label: String,
    color: Color = Color.Black,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = label, color = color, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun ActivityHeaderRow(date: LocalDate, taskCount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = CalendarUtils.fullDateHeader(date),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = Color.Gray,
            letterSpacing = 1.sp
        )

        Text(
            text = "$taskCount Activities",
            fontSize = 12.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun EmptyStateContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(bottom = 120.dp)
    ) {
        Surface(
            modifier = Modifier.size(100.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = Color(0xFF6E40FF),
                    modifier = Modifier.size(48.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "No activities for this day",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.Black
        )
        
        Text(
            text = "You have no activities for this day.",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
