package com.akashicsoft.crm.activity.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akashicsoft.crm.activity.model.ActivityType
import com.akashicsoft.crm.activity.util.CalendarUtils
import com.akashicsoft.crm.activity.ui.components.ActivityTimelineItem
import com.akashicsoft.crm.activity.ui.components.CalendarCard
import com.akashicsoft.crm.activity.viewmodel.ActivityViewModel

@Composable
fun ActivityScreen(
    viewModel: ActivityViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val showTodayButton by viewModel.showTodayButton.collectAsState()
    var showAddOptions by remember { mutableStateOf(false) }

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
                onTodayClick = viewModel::goToToday,
                onDateSelected = viewModel::onDateSelected,
                onWeekSwiped = viewModel::onDateSelected
            )

            if (uiState.activities.isEmpty()) {
                // Empty State
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyStateContent()
                }
            } else {
                // Activity Timeline List
                Column(modifier = Modifier.fillMaxSize()) {
                    ActivityHeaderRow(
                        date = uiState.selectedDate,
                        taskCount = uiState.activities.size
                    )
                    ActivityTimelineList(uiState.activities)
                }
            }
        }

        // Speed Dial FAB
        AddActivitySpeedDial(
            expanded = showAddOptions,
            onToggle = { showAddOptions = !showAddOptions },
            onOptionClick = { type ->
                viewModel.addActivity(type)
                showAddOptions = false
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)
        )
    }
}

@Composable
private fun ActivityHeaderRow(date: kotlinx.datetime.LocalDate, taskCount: Int) {
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
            text = "$taskCount Tasks",
            fontSize = 12.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ActivityTimelineList(activities: List<com.akashicsoft.crm.activity.model.Activity>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
    ) {
        itemsIndexed(activities) { index, activity ->
            ActivityTimelineItem(
                activity = activity,
                isFirst = index == 0,
                isLast = index == activities.size - 1
            )
        }
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

@Composable
private fun AddActivitySpeedDial(
    expanded: Boolean,
    onToggle: () -> Unit,
    onOptionClick: (ActivityType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SpeedDialOption(
                    icon = Icons.Default.Call,
                    label = "Add Call",
                    color = Color(0xFF27AE60),
                    onClick = { onOptionClick(ActivityType.CALL) }
                )
                SpeedDialOption(
                    icon = Icons.Default.Groups,
                    label = "Add Meeting",
                    color = Color(0xFF2D9CDB),
                    onClick = { onOptionClick(ActivityType.MEETING) }
                )
                SpeedDialOption(
                    icon = Icons.Default.TaskAlt,
                    label = "Add Task",
                    color = Color(0xFF3B229D),
                    onClick = { onOptionClick(ActivityType.TASK) }
                )
            }
        }

        FloatingActionButton(
            onClick = onToggle,
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
    }
}

@Composable
private fun SpeedDialOption(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            shadowElevation = 4.dp
        ) {
            Text(
                text = label,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }

        SmallFloatingActionButton(
            onClick = onClick,
            containerColor = color,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp))
        }
    }
}
